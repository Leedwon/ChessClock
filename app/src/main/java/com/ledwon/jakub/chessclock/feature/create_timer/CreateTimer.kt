package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun CreateTimerScreen() {
    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        NumberPicker(
            0..50,
            modifier = Modifier.background(Color.Green),
            onValueChangeListener = { Timber.d("dupa $it") }
        )
    }
}

