package com.ledwon.jakub.chessclock.feature.choose_clock

import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.model.PlayerTime

enum class ClockTimeType {
    Bullet,
    Blitz,
    Rapid,
    Classic,
    Custom
}

val Clock.clockTimeType: ClockTimeType
    get() = when {
        whitePlayerTime != blackPlayerTime -> ClockTimeType.Custom
        else -> whitePlayerTime.getClockTimeType()
    }

private fun PlayerTime.getClockTimeType() = when {
    hours > 0 -> ClockTimeType.Classic
    minutes > 10 -> ClockTimeType.Rapid
    minutes in 3..10 -> ClockTimeType.Blitz
    else -> ClockTimeType.Bullet
}
