package com.ledwon.jakub.chessclock.feature.clock

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadVectorResource
import com.ledwon.jakub.chessclock.feature.clock.widget.ClockButton
import com.ledwon.jakub.chessclock.feature.clock.widget.ClockCenterButton
import com.ledwon.jakub.chessclock.feature.clock.widget.RotatingDice

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val state: State by clockViewModel.state.observeAsState(
        State(
            PlayerDisplay.White(""),
            PlayerDisplay.Black(""),
            Player.White(0f),
            GameState.BeforeStarted
        )
    )

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
                enabled = clockEnabled && state.first.isFor(state.currentPlayer) || state.gameState == GameState.BeforeStarted
            )
            ClockButton(
                modifier = btnModifier,
                player = state.second,
                onClick = { clockViewModel.clockClicked(state.second) },
                enabled = clockEnabled && state.second.isFor(state.currentPlayer) || state.gameState == GameState.BeforeStarted
            )
        }
        when (state.gameState) {
            GameState.RandomizingPositions -> {
                RotatingDice()
            }
            GameState.BeforeStarted -> {
                ClockCenterButton(
                    onClick = { clockViewModel.swapSides() },
                    icon = loadVectorResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_swap_48
                    ),
                    iconTint = Color.Green
                )
            }
            GameState.Running -> {
                ClockCenterButton(
                    onClick = { clockViewModel.stopTimer() },
                    icon = loadVectorResource(
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
                        icon = loadVectorResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_play_48
                        ),
                        iconTint = Color.Green
                    )
                    ClockCenterButton(
                        modifier = Modifier.weight(1f),
                        onClick = { clockViewModel.restartGame() },
                        icon = loadVectorResource(
                            id = com.ledwon.jakub.chessclock.R.drawable.ic_replay_48
                        ),
                        iconTint = Color.Green
                    )
                }
            }
            GameState.Over -> {
                ClockCenterButton(
                    onClick = { clockViewModel.restartGame() },
                    icon = loadVectorResource(
                        id = com.ledwon.jakub.chessclock.R.drawable.ic_replay_48
                    ),
                    iconTint = Color.Green
                )
            }
        }
    }
}






