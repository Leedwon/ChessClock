package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer

object PredefinedTimers {
    val timers = listOf(
        Timer(whiteClockTime = ClockTime(minutes = 1)),
        Timer(whiteClockTime = ClockTime(minutes = 1, increment = 1)),
        Timer(whiteClockTime = ClockTime(minutes = 2, increment = 1)),
        Timer(whiteClockTime = ClockTime(minutes = 3)),
        Timer(whiteClockTime = ClockTime(minutes = 3, increment = 2)),
        Timer(whiteClockTime = ClockTime(minutes = 5)),
        Timer(whiteClockTime = ClockTime(minutes = 10)),
        Timer(whiteClockTime = ClockTime(minutes = 15)),
        Timer(whiteClockTime = ClockTime(minutes = 30)),
        Timer(whiteClockTime = ClockTime(hours = 1))
    )
}