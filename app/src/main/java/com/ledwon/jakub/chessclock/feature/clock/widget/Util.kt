package com.ledwon.jakub.chessclock.feature.clock.widget

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