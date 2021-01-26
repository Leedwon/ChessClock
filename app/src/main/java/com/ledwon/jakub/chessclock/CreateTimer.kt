package com.ledwon.jakub.chessclock

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue

@Composable
fun CreateTimerScreen(createTimerViewModel: CreateTimerViewModel) {
    val text by createTimerViewModel.text.observeAsState(initial = "initial")

    Text(text = text)
}