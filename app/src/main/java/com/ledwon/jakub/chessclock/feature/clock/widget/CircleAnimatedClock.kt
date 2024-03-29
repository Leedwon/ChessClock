package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay
import com.ledwon.jakub.chessclock.ui.*
import com.ledwon.jakub.chessclock.util.toDeferrableString

@Composable
fun CircleAnimatedClock(
    modifier: Modifier = Modifier,
    playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    rotations: Pair<Float, Float>,
    onClockButtonClick: (PlayerDisplay) -> Unit,
    pulsationEnabled: Boolean,
    enabled: Boolean = true,
    circlePaddingFromCenter: Dp = 0.dp
) {
    val infiniteTransition: InfiniteTransition? = if (pulsationEnabled) rememberInfiniteTransition() else null

    val firstClockFontSize: Float = if (pulsationEnabled && infiniteTransition != null) {
        infiniteTransition.animateClockFontSize(isActive = playersDisplay.first.isActive).value
    } else {
        35f
    }
    val secondClockFontSize: Float = if (pulsationEnabled && infiniteTransition != null) {
        infiniteTransition.animateClockFontSize(isActive = playersDisplay.second.isActive).value
    } else {
        35f
    }

    val height = LocalConfiguration.current.screenHeightDp

    val circleSize = remember { height / 2 * 0.75f }

    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
        val btnModifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.first,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = circlePaddingFromCenter)
                    .rotate(rotations.first),
                contentAlignment = Alignment.Center
            ) {
                FilledCircle(
                    modifier = Modifier.size(circleSize.dp),
                    filledPercentage = playersDisplay.first.percentageLeft,
                    borderColor = playersDisplay.first.contentColor()
                )
                Text(
                    text = playersDisplay.first.text.getString(),
                    color = playersDisplay.first.contentColor(),
                    fontSize = firstClockFontSize.sp
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = circlePaddingFromCenter)
                    .rotate(rotations.second),
                contentAlignment = Alignment.Center
            ) {
                FilledCircle(
                    modifier = Modifier.size(circleSize.dp),
                    filledPercentage = playersDisplay.second.percentageLeft,
                    borderColor = playersDisplay.second.contentColor()
                )
                Text(
                    text = playersDisplay.second.text.getString(),
                    color = playersDisplay.second.contentColor(),
                    fontSize = secondClockFontSize.sp
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
        Canvas(modifier = Modifier
            .fillMaxSize()
            .rotate(270f), onDraw = {
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
        rotations = 180f to 0f,
        playersDisplay = PlayerDisplay.White("01:00".toDeferrableString(), 1.0f, true) to PlayerDisplay.Black(
            "00:30".toDeferrableString(),
            0.5f,
            false
        ),
        onClockButtonClick = { },
        pulsationEnabled = true
    )
}