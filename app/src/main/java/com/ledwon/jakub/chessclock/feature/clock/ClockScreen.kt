package com.ledwon.jakub.chessclock.feature.clock

import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ledwon.jakub.chessclock.feature.clock.ClockScreenMetrics.centerButtonSize
import com.ledwon.jakub.chessclock.feature.clock.widget.*
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.util.LocalWindowProvider

//todo more elegant way?
object ClockScreenMetrics {
    const val centerButtonSize = 96f
}

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val launchedEffectKey = "clock_key"

    val state: State by clockViewModel.state.observeAsState(
        State(
            playersDisplay = PlayerDisplay.White(
                text = "",
                percentageLeft = 1.0f,
                isActive = false
            ) to PlayerDisplay.Black(
                text = "",
                percentageLeft = 1.0f,
                isActive = false
            ),
            gameState = GameState.BeforeStarted
        )
    )

    val window = LocalWindowProvider.current

    LaunchedEffect(key1 = launchedEffectKey, block = {
        window.addFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))

    })

    DisposableEffect(key1 = launchedEffectKey, effect = {
        onDispose {
            window.clearFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))
        }
    })

    val clockType = clockViewModel.clockDisplay.collectAsState()
    val pulsationEnabled = clockViewModel.pulsationEnabled.collectAsState()

    val rotations = when (val clockTypeValue = clockType.value) {
        is ClockDisplay.CircleAnimatedClock -> clockTypeValue.rotations
        is ClockDisplay.OwnPlayerTimeClock -> clockTypeValue.rotations
        is ClockDisplay.BothPlayersTimeClock -> 0f to 0f
    }

    Box(contentAlignment = Alignment.Center) {
        val clockEnabled =
            state.gameState != GameState.Paused && state.gameState != GameState.RandomizingPositions
        when (clockType.value) {
            is ClockDisplay.OwnPlayerTimeClock -> OwnPlayerTimeClock(
                playersDisplay = state.playersDisplay,
                onClockButtonClick = clockViewModel::clockClicked,
                rotations = rotations,
                pulsationEnabled = pulsationEnabled.value,
                enabled = clockEnabled
            )
            is ClockDisplay.BothPlayersTimeClock -> BothPlayersTimeClock(
                playersDisplay = state.playersDisplay,
                onClockButtonClick = clockViewModel::clockClicked,
                pulsationEnabled = pulsationEnabled.value,
                enabled = clockEnabled
            )
            is ClockDisplay.CircleAnimatedClock -> CircleAnimatedClock(
                playersDisplay = state.playersDisplay,
                rotations = rotations,
                onClockButtonClick = clockViewModel::clockClicked,
                pulsationEnabled = pulsationEnabled.value,
                enabled = clockEnabled,
            )
        }

        val centerButtonRotations = if (rotations.first == rotations.second) rotations.first else 0f
        when (state.gameState) {
            GameState.RandomizingPositions -> {
                RotatingDice(
                    modifier = Modifier.size(centerButtonSize.dp),
                    onDiceClick = clockViewModel::cancelRandomization
                )
            }
            GameState.BeforeStarted -> {
                ClockCenterButton(
                    modifier = Modifier.size(centerButtonSize.dp),
                    onClick = { clockViewModel.swapSides() },
                    icon = painterResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_swap_48
                    ),
                    iconTint = Color.Green
                )
            }
            GameState.Running -> {
                ClockCenterButton(
                    modifier = Modifier.size(centerButtonSize.dp).rotate(centerButtonRotations),
                    onClick = { clockViewModel.stopTimer() },
                    icon = painterResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_pause_48
                    ),
                    iconTint = Color.Red
                )
            }
            GameState.Paused -> {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClockCenterButton(
                        modifier = Modifier.size(centerButtonSize.dp).weight(1f, fill = false)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.startTimer() },
                        icon = painterResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_play_48
                        ),
                        iconTint = Color.Green
                    )
                    ClockCenterButton(
                        modifier = Modifier.size(centerButtonSize.dp).weight(1f, fill = false)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.restartGame() },
                        icon = painterResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_replay_48
                        ),
                        iconTint = Color.Green
                    )
                }
            }
            GameState.Over -> {
                ClockCenterButton(
                    modifier = Modifier.size(centerButtonSize.dp).rotate(centerButtonRotations),
                    onClick = { clockViewModel.restartGame() },
                    icon = painterResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_replay_48
                    ),
                    iconTint = Color.Green
                )
            }
        }
    }
}






