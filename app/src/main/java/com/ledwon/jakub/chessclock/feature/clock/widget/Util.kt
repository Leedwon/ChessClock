package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay

fun PlayerDisplay.contentColor(): Color = when (this) {
    is PlayerDisplay.White -> Color.Black
    is PlayerDisplay.Black -> Color.White
}

fun PlayerDisplay.backgroundColor(): Color = when (this) {
    is PlayerDisplay.White -> Color.White
    is PlayerDisplay.Black -> Color.Black
}

@Composable
fun InfiniteTransition.animateClockFontSize(isActive: Boolean): State<Float> = this.animateFloat(
    initialValue = 35f,
    targetValue = if (isActive) 45f else 35f,
    animationSpec = infiniteRepeatable(
        animation = tween(750, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    )
)
