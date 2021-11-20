package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.model.Clock

object PredefinedClocks {
    val clocks = listOf(
        Clock(whitePlayerTime = PlayerTime(minutes = 1)),
        Clock(whitePlayerTime = PlayerTime(minutes = 1, increment = 1)),
        Clock(whitePlayerTime = PlayerTime(minutes = 2, increment = 1)),
        Clock(whitePlayerTime = PlayerTime(minutes = 3)),
        Clock(whitePlayerTime = PlayerTime(minutes = 3, increment = 2)),
        Clock(whitePlayerTime = PlayerTime(minutes = 5)),
        Clock(whitePlayerTime = PlayerTime(minutes = 10)),
        Clock(whitePlayerTime = PlayerTime(minutes = 15)),
        Clock(whitePlayerTime = PlayerTime(minutes = 30)),
        Clock(whitePlayerTime = PlayerTime(hours = 1))
    )
}