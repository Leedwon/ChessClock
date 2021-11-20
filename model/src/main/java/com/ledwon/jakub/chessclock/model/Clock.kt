package com.ledwon.jakub.chessclock.model

data class Clock(
    val id: Int = 0,
    val whitePlayerTime: PlayerTime,
    val blackPlayerTime: PlayerTime = whitePlayerTime,
    val isFavourite: Boolean = false
)