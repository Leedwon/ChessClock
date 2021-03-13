package com.ledwon.jakub.chessclock.feature.clock

import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ledwon.jakub.chessclock.feature.clock.widget.*
import com.ledwon.jakub.chessclock.util.LocalWindowProvider

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val launchedEffectKey = "clock_key"

    val state: State by clockViewModel.state.observeAsState(
        State(
            PlayerDisplay.White(""),
            PlayerDisplay.Black(""),
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

    Box(contentAlignment = Alignment.Center) {
        val clockEnabled =
            state.gameState != GameState.Paused && state.gameState != GameState.RandomizingPositions
        BothPlayersTimeClock(
            playersDisplay = state.first to state.second,
            onClockButtonClick = clockViewModel::clockClicked,
            enabled = clockEnabled
        )
        when (state.gameState) {
            GameState.RandomizingPositions -> {
                RotatingDice(onDiceClick = clockViewModel::cancelRandomization)
            }
            GameState.BeforeStarted -> {
                ClockCenterButton(
                    onClick = { clockViewModel.swapSides() },
                    icon = painterResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_swap_48
                    ),
                    iconTint = Color.Green
                )
            }
            GameState.Running -> {
                ClockCenterButton(
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
                        modifier = Modifier.weight(1f, fill = false),
                        onClick = { clockViewModel.startTimer() },
                        icon = painterResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_play_48
                        ),
                        iconTint = Color.Green
                    )
                    ClockCenterButton(
                        modifier = Modifier.weight(1f, fill = false),
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






