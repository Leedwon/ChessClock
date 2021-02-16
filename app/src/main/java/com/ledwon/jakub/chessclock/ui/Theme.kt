package com.ledwon.jakub.chessclock.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable


@Composable
fun ChessClockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorTheme: ColorTheme = ColorTheme.Purple,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        colorTheme.colorTheme.darkColors
    } else {
        colorTheme.colorTheme.lightColors
    }

    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}