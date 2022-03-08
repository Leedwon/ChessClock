package com.ledwon.jakub.chessclock.feature.clock.model

enum class PlayerColor {
    White, Black;

    fun opposite(): PlayerColor = when (this) {
        White -> Black
        Black -> White
    }
}
