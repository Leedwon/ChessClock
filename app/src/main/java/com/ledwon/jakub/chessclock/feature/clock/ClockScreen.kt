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
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.clock.widget.*
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.util.LocalWindowProvider
import com.ledwon.jakub.chessclock.util.rememberString

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val centerButtonSize = remember { 96.dp }

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

    DisposableEffect(key1 = Unit, effect = {
        window.addFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))
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
                enabled = clockEnabled,
                paddingFromCenter = centerButtonSize / 2
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
                circlePaddingFromCenter = centerButtonSize / 2
            )
        }

        val centerButtonRotations = if (rotations.first == rotations.second) rotations.first else 0f
        when (state.gameState) {
            GameState.RandomizingPositions -> {
                RotatingDice(
                    modifier = Modifier.size(centerButtonSize),
                    onDiceClick = clockViewModel::cancelRandomization
                )
            }
            GameState.BeforeStarted -> {
                ClockCenterButton(
                    modifier = Modifier.size(centerButtonSize),
                    onClick = { clockViewModel.swapSides() },
                    icon = painterResource(id = R.drawable.ic_swap_48),
                    iconTint = Color.Green,
                    iconContentDescription = rememberString(resId = R.string.swap_sides_content_description)
                )
            }
            GameState.Running -> {
                ClockCenterButton(
                    modifier = Modifier
                        .size(centerButtonSize)
                        .rotate(centerButtonRotations),
                    onClick = { clockViewModel.stopTimer() },
                    icon = painterResource(id = R.drawable.ic_pause_48),
                    iconTint = Color.Red,
                    iconContentDescription = rememberString(resId = R.string.pause_clock_content_description)
                )
            }
            GameState.Paused -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .weight(1f, fill = false)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.startTimer() },
                        icon = painterResource(id = R.drawable.ic_play_48),
                        iconTint = Color.Green,
                        iconContentDescription = rememberString(resId = R.string.resume_clock_content_description)
                    )
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .weight(1f, fill = false)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.restartGame() },
                        icon = painterResource(id = R.drawable.ic_replay_48),
                        iconTint = Color.Green,
                        iconContentDescription = rememberString(resId = R.string.restart_clock_content_description)
                    )
                }
            }
            GameState.Over -> {
                ClockCenterButton(
                    modifier = Modifier
                        .size(centerButtonSize)
                        .rotate(centerButtonRotations),
                    onClick = { clockViewModel.restartGame() },
                    icon = painterResource(id = R.drawable.ic_replay_48),
                    iconTint = Color.Green,
                    iconContentDescription = rememberString(resId = R.string.restart_clock_content_description)
                )
            }
        }
    }
}






