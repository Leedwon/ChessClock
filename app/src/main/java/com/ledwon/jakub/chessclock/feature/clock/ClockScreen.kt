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
import com.ledwon.jakub.chessclock.feature.clock.widget.*
import com.ledwon.jakub.chessclock.util.LocalWindowProvider

sealed class ClockType {
    data class OwnPlayerTimeClock(val rotations: Pair<Float, Float>) : ClockType()
    data class CircleAnimatedClock(val rotations: Pair<Float, Float>) : ClockType()
    object BothPlayersTimeClock : ClockType()
}

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val launchedEffectKey = "clock_key"

    val state: State by clockViewModel.state.observeAsState(
        State(
            PlayerDisplay.White("", 1.0f),
            PlayerDisplay.Black("", 1.0f),
            GameState.BeforeStarted
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

    val clockType = clockViewModel.clockType.collectAsState()

    val rotations = when (val clockTypeValue = clockType.value) {
        is ClockType.CircleAnimatedClock -> clockTypeValue.rotations
        is ClockType.OwnPlayerTimeClock -> clockTypeValue.rotations
        is ClockType.BothPlayersTimeClock -> 0f to 0f
    }

    Box(contentAlignment = Alignment.Center) {
        val clockEnabled =
            state.gameState != GameState.Paused && state.gameState != GameState.RandomizingPositions
        val playersDisplay = state.first to state.second
        when (clockType.value) {
            is ClockType.OwnPlayerTimeClock -> OwnPlayerTimeClock(
                playersDisplay = playersDisplay,
                onClockButtonClick = clockViewModel::clockClicked,
                rotations = rotations,
                enabled = clockEnabled
            )
            is ClockType.BothPlayersTimeClock -> BothPlayersTimeClock(
                playersDisplay = playersDisplay,
                onClockButtonClick = clockViewModel::clockClicked,
                enabled = clockEnabled
            )
            is ClockType.CircleAnimatedClock -> CircleAnimatedClock(
                playersDisplay = playersDisplay,
                rotations = rotations,
                onClockButtonClick = clockViewModel::clockClicked,
                enabled = clockEnabled,
            )
        }

        val centerButtonRotations = if (rotations.first == rotations.second) rotations.first else 0f
        when (state.gameState) {
            GameState.RandomizingPositions -> {
                RotatingDice(onDiceClick = clockViewModel::cancelRandomization)
            }
            GameState.BeforeStarted -> {
                ClockCenterButton(
                    modifier = Modifier.rotate(centerButtonRotations),
                    onClick = { clockViewModel.swapSides() },
                    icon = painterResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_swap_48
                    ),
                    iconTint = Color.Green
                )
            }
            GameState.Running -> {
                ClockCenterButton(
                    modifier = Modifier.rotate(centerButtonRotations),
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
                        modifier = Modifier.weight(1f, fill = false).rotate(centerButtonRotations),
                        onClick = { clockViewModel.startTimer() },
                        icon = painterResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_play_48
                        ),
                        iconTint = Color.Green
                    )
                    ClockCenterButton(
                        modifier = Modifier.weight(1f, fill = false).rotate(centerButtonRotations),
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
                    modifier = Modifier.rotate(centerButtonRotations),
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






