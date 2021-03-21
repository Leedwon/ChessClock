package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.feature.common.ClockDisplay

class ClockTypesRepository {
    data class NamedClockDisplayType(
        val display: ClockDisplay,
        val name: String
    )


    val clockTypes = listOf(
        NamedClockDisplayType(
            display = ClockDisplay.OwnPlayerTimeClock(180f to 0f),
            name = "Classic clock"
        ),
        NamedClockDisplayType(
            display = ClockDisplay.OwnPlayerTimeClock(90f to 90f),
            name = "Classic clock 90°"
        ),
        NamedClockDisplayType(
            display = ClockDisplay.OwnPlayerTimeClock(270f to 270f),
            name = "Classic clock 270°"
        ),
        NamedClockDisplayType(
            display = ClockDisplay.BothPlayersTimeClock,
            name = "Both players time clock"
        ),
        NamedClockDisplayType(
            display = ClockDisplay.CircleAnimatedClock(180f to 0f),
            name = "Circle animation clock"
        ),
        NamedClockDisplayType(
            display = ClockDisplay.CircleAnimatedClock(90f to 90f),
            name = "Circle animation clock 90°"
        ),
        NamedClockDisplayType(
            display = ClockDisplay.CircleAnimatedClock(270f to 270f),
            name = "Circle animation clock 270°"
        )
    )
}