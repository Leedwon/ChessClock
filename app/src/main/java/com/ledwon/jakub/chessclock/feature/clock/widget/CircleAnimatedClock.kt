package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay
import com.ledwon.jakub.chessclock.ui.*

@Composable
fun CircleAnimatedClock(
    playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    onClockButtonClick: (PlayerDisplay) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
        val btnModifier = Modifier.weight(1f).fillMaxWidth()
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.first,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                FilledCircle(
                    modifier = Modifier.size(250.dp),
                    filledPercentage = playersDisplay.first.percentageLeft,
                    borderColor = playersDisplay.first.contentColor()
                )
                Text(
                    modifier = Modifier.rotate(180f),
                    text = playersDisplay.first.text,
                    color = playersDisplay.first.contentColor(),
                    fontSize = 35.sp
                )
            }
        }
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.second,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(top = 64.dp),
                contentAlignment = Alignment.Center
            ) {
                FilledCircle(
                    modifier = Modifier.size(250.dp),
                    filledPercentage = playersDisplay.second.percentageLeft,
                    borderColor = playersDisplay.second.contentColor()
                )
                Text(
                    text = playersDisplay.second.text,
                    color = playersDisplay.second.contentColor(),
                    fontSize = 35.sp
                )
            }
        }
    }
}

@Composable
fun FilledCircle(modifier: Modifier = Modifier, filledPercentage: Float, borderColor: Color) {
    val color = animateColorAsState(
        targetValue = when (filledPercentage) {
            in 0.5f..Float.MAX_VALUE -> green500
            in 0.2f..0.5f -> yellow500
            else -> red500
        }
    )

    Box(modifier = modifier.border(color = borderColor, shape = CircleShape, width = 1.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().rotate(270f), onDraw = {
            drawArc(
                color = color.value,
                startAngle = 0.0f,
                sweepAngle = 360f * filledPercentage,
                useCenter = true
            )
        })
    }
}

@Preview
@Composable
fun FilledCirclePrev() {
    CircleAnimatedClock(
        playersDisplay = PlayerDisplay.White("01:00", 1.0f) to PlayerDisplay.Black("00:30", 0.5f),
        onClockButtonClick = { }
    )
}