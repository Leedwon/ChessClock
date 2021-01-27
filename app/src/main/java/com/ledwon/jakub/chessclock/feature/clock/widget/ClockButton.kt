package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay

@Composable
fun ClockButton(
    modifier: Modifier,
    player: PlayerDisplay,
    onClick: () -> Unit,
    rotateDegrees: Float = 0f,
    enabled: Boolean = true
) {
    Button(
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = when (player) {
                is PlayerDisplay.White -> Color.White
                is PlayerDisplay.Black -> Color.Black
            },
            contentColor = when(player) {
                is PlayerDisplay.White -> Color.Black
                is PlayerDisplay.Black -> Color.White
            },
            disabledBackgroundColor = when (player) {
                is PlayerDisplay.White -> Color.White
                is PlayerDisplay.Black -> Color.Black
            }
        ),
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier.rotate(rotateDegrees),
            text = player.text, fontSize = TextUnit.Sp(35), color = when (player) {
                is PlayerDisplay.White -> Color.Black
                is PlayerDisplay.Black -> Color.White
            }
        )
    }
}