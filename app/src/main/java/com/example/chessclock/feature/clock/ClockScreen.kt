package com.example.chessclock.feature.clock

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.chessclock.feature.clock.widget.ClockButton
import com.example.chessclock.feature.clock.widget.ClockCenterButton
import com.example.chessclock.feature.clock.widget.ClockCenterInteractions
import com.example.chessclock.feature.clock.widget.RotatingDice

@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val state: State by clockViewModel.state.observeAsState(
        State(
            PlayerDisplay.White(""),
            PlayerDisplay.Black(""),
            GameState.BeforeStarted
        )
    )

    Box(contentAlignment = Alignment.Center) {
        val clockEnabled = state.gameState != GameState.Paused && state.gameState != GameState.RandomizingPositions
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
        if (state.gameState == GameState.RandomizingPositions) {
            RotatingDice()
        } else {
            ClockCenterButton(
                state = state, clockCenterInteractions = ClockCenterInteractions(
                    swapSides = clockViewModel::swapSides,
                    stopTimer = clockViewModel::stopTimer,
                    startTimer = clockViewModel::startTimer,
                    restart = clockViewModel::restart
                )
            )
        }
    }
}






