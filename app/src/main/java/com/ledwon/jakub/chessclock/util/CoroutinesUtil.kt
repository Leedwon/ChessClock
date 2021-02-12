package com.ledwon.jakub.chessclock.util

import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableStateFlow<T>.tryUpdate(mapper: (T) -> T) {
    this.tryEmit(mapper(this.value))
}