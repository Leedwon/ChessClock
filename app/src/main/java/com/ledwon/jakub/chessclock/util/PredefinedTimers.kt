package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer

object PredefinedTimers {
    val timers = listOf(
        Timer(
            whiteClockTime = ClockTime(minutes = 1),
            description = "Bullet 1",
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 1, increment = 1),
            description = "Bullet 1 + 1"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 2, increment = 1),
            description = "Bullet 2 + 1"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 3),
            description = "Blitz 3"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 3, increment = 2),
            description = "Blitz 3 + 2"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 5),
            description = "Blitz 5"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 10),
            description = "Rapid 10"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 15),
            description = "Rapid 15"
        ),
        Timer(
            whiteClockTime = ClockTime(minutes = 30),
            description = "Rapid 30"
        ),
        Timer(
            whiteClockTime = ClockTime(hours = 1),
            description = "Standard 60"
        )
    )
}