package com.ledwon.jakub.chessclock.feature.clock

import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.ledwon.jakub.chessclock.feature.clock.widget.ClockButton
import com.ledwon.jakub.chessclock.feature.clock.widget.ClockCenterButton
import com.ledwon.jakub.chessclock.feature.clock.widget.RotatingDice
import com.ledwon.jakub.chessclock.util.LocalWindowProvider

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val state: State by clockViewModel.state.observeAsState(
        State(
            PlayerDisplay.White(""),
            PlayerDisplay.Black(""),
            GameState.BeforeStarted
        )
    )

    val window = LocalWindowProvider.current

    //todo it works fine but investigate if we should use such a simple key here
    LaunchedEffect(key1 = "clock", block = {
        window?.addFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))

    })

    DisposableEffect(key1 = "clock", effect = {
        onDispose {
            window?.clearFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))
        }
    })

    Box(contentAlignment = Alignment.Center) {
        val clockEnabled =
            state.gameState != GameState.Paused && state.gameState != GameState.RandomizingPositions
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
            val btnModifier = Modifier.weight(1f).fillMaxWidth()
            ClockButton(
                modifier = btnModifier,
                player = state.first,
                onClick = { clockViewModel.clockClicked(state.first) },
                rotateDegrees = 180f,
                enabled = clockEnabled
            )
            ClockButton(
                modifier = btnModifier,
                player = state.second,
                onClick = { clockViewModel.clockClicked(state.second) },
                enabled = clockEnabled
            )
        }
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
                    horizontalArrangement = Arrangement.Center
                ) {
                    ClockCenterButton(
                        modifier = Modifier.weight(1f),
                        onClick = { clockViewModel.startTimer() },
                        icon = painterResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_play_48
                        ),
                        iconTint = Color.Green
                    )
                    ClockCenterButton(
                        modifier = Modifier.weight(1f),
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






