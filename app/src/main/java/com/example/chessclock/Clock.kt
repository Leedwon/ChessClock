package com.example.chessclock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

//todo split into many composables
@Composable
fun ClockScreen(clockViewModel: ClockViewModel) {
    val state: State by clockViewModel.state.observeAsState(
        State(
            PlayerDisplay.White(""),
            PlayerDisplay.Black(""),
            false
        )
    )

    Box(contentAlignment = Alignment.Center) {
        val clockModifier = Modifier.fillMaxSize()
        Column(modifier = clockModifier, verticalArrangement = Arrangement.SpaceAround) {
            val btnModifier = Modifier.weight(1f).fillMaxWidth()
            ClockButton(
                modifier = btnModifier,
                player = state.first,
                onClick = {
                    clockViewModel.clockClicked(state.first)
                },
                rotateDegrees = 180f,
            )
            ClockButton(
                modifier = btnModifier,
                player = state.second,
                onClick = {
                    clockViewModel.clockClicked(state.second)
                })
        }
        if (!state.gameStarted) {
            OutlinedButton(
                modifier = Modifier.height(96.dp).width(96.dp),
                onClick = clockViewModel::swapSides,
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Black,
                ),
                border = BorderStroke(2.dp, color = Color.White),
                shape = CircleShape
            ) {
                val image = androidx.compose.ui.res.loadVectorResource(id = R.drawable.ic_swap_48)
                image.resource.resource?.let {
                    Icon(
                        imageVector = it,
                        tint = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun ClockButton(
    modifier: Modifier,
    player: PlayerDisplay,
    onClick: () -> Unit,
    rotateDegrees: Float = 0f
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = when (player) {
                is PlayerDisplay.White -> Color.White
                is PlayerDisplay.Black -> Color.Black
            }
        ),
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier.rotate(rotateDegrees),
            text = player.text, fontSize = TextUnit.Companion.Sp(35), color = when (player) {
                is PlayerDisplay.White -> Color.Black
                is PlayerDisplay.Black -> Color.White
            }
        )
    }
}
