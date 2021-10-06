package com.ledwon.jakub.chessclock.feature.clock.model

data class ClockInitialData(
    val whiteSeconds: Int,
    val whiteIncrementSeconds: Int = 0,
    val blackSeconds: Int,
    val blackIncrementSeconds: Int = 0
)