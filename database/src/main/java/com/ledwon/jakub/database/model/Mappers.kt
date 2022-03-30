package com.ledwon.jakub.database.model
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.model.PlayerTime

internal object Mappers {

    fun Timer.toClock() = Clock(
        id = this.id,
        whitePlayerTime = this.whiteClockTime.toPlayerTime(),
        blackPlayerTime = this.blackClockTime.toPlayerTime(),
        isFavourite = this.isFavourite
    )

    private fun ClockTime.toPlayerTime() = PlayerTime(
        hours = this.hours,
        minutes = this.minutes,
        seconds = this.seconds,
        increment = this.increment
    )

    fun Clock.toTimer() = Timer(
        id = this.id,
        whiteClockTime = this.whitePlayerTime.toClockTime(),
        blackClockTime = this.blackPlayerTime.toClockTime(),
        isFavourite = this.isFavourite
    )

    private fun PlayerTime.toClockTime() = ClockTime(
        hours = this.hours,
        minutes = this.minutes,
        seconds = this.seconds,
        increment = this.increment
    )
}