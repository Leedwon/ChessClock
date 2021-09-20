package com.ledwon.jakub.chessclock.data.repository

import androidx.annotation.StringRes
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay

class ClockTypesRepository {
    data class NamedClockDisplayType(
        val id: String,
        val display: ClockDisplay,
        @StringRes val name: Int
    )

    val clockTypes = listOf(
        NamedClockDisplayType(
            id = "classic",
            display = ClockDisplay.OwnPlayerTimeClock(180f to 0f),
            name = R.string.classic_clock
        ),
        NamedClockDisplayType(
            id = "classic90",
            display = ClockDisplay.OwnPlayerTimeClock(90f to 90f),
            name = R.string.classic_clock_90
        ),
        NamedClockDisplayType(
            id = "classic270",
            display = ClockDisplay.OwnPlayerTimeClock(270f to 270f),
            name = R.string.classic_clock_270
        ),
        NamedClockDisplayType(
            id = "bothPlayers",
            display = ClockDisplay.BothPlayersTimeClock,
            name = R.string.both_players_time_clock
        ),
        NamedClockDisplayType(
            id = "circle",
            display = ClockDisplay.CircleAnimatedClock(180f to 0f),
            name = R.string.circle_animation_clock
        ),
        NamedClockDisplayType(
            id = "circle90",
            display = ClockDisplay.CircleAnimatedClock(90f to 90f),
            name = R.string.circle_animation_clock_90
        ),
        NamedClockDisplayType(
            id = "circle270",
            display = ClockDisplay.CircleAnimatedClock(270f to 270f),
            name = R.string.circle_animation_clock_270
        )
    )
}