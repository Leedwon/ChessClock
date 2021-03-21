package com.ledwon.jakub.chessclock.feature.common

sealed class ClockDisplay {
    data class OwnPlayerTimeClock(val rotations: Pair<Float, Float>) : ClockDisplay()
    data class CircleAnimatedClock(val rotations: Pair<Float, Float>) : ClockDisplay()
    object BothPlayersTimeClock : ClockDisplay()
}