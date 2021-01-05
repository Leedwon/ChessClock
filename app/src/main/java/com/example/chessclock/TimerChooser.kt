package com.example.chessclock

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun TimerChooser(navController : NavController) {
    Column() {
        Slider(value = 10f, valueRange = 1f..120f, onValueChange = { })
        Slider(value = 10f, valueRange = 1f..120f, onValueChange = { })

    }
}