package com.example.chessclock.feature.clock.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.unit.dp
import com.example.chessclock.R
import com.example.chessclock.feature.clock.GameState
import com.example.chessclock.feature.clock.State


data class ClockCenterInteractions(
    val swapSides: () -> Unit,
    val stopTimer: () -> Unit,
    val startTimer: () -> Unit,
    val restart: () -> Unit
)

@Composable
fun ClockCenterButton(
    state: State,
    clockCenterInteractions: ClockCenterInteractions
) {
    OutlinedButton(
        modifier = Modifier.height(96.dp).width(96.dp),
        onClick = {
            when (state.gameState) {
                GameState.BeforeStarted -> clockCenterInteractions.swapSides()
                GameState.Running -> clockCenterInteractions.stopTimer()
                GameState.Paused -> clockCenterInteractions.startTimer()
                GameState.Over -> clockCenterInteractions.restart()
                else -> {
                }
            }
        },
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.Black,
        ),
        border = BorderStroke(2.dp, color = Color.White),
        shape = CircleShape
    ) {
        val image = loadVectorResource(
            id = when (state.gameState) {
                GameState.RandomizingPositions -> R.drawable.ic_dice_one_48
                GameState.BeforeStarted -> R.drawable.ic_swap_48
                GameState.Running -> R.drawable.ic_pause_48
                GameState.Paused -> R.drawable.ic_play_48
                GameState.Over -> R.drawable.ic_replay_48
            }
        )
        image.resource.resource?.let {
            Icon(
                imageVector = it,
                tint = when (state.gameState) {
                    GameState.Running -> Color.Red
                    else -> Color.Green
                }
            )
        }
    }
}