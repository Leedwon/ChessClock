package com.ledwon.jakub.chessclock.feature.clock.model

sealed class Player(
    private val initialMillis: Float,
    private val incrementMillis: Int = 0
) {
    var millisLeft: Float = initialMillis

    private val seconds: Int
        get() = millisLeft.toInt() / 1000 % 60
    private val minutes: Int
        get() = millisLeft.toInt() / 1000 / 60 % 60
    private val hours: Int
        get() = millisLeft.toInt() / 1000 / 60 / 60

    val percentageLeft: Float
        get() = millisLeft / initialMillis

    open val text: String
        get() {
            val hoursText = if (hours > 0) {
                "${formatTime(hours)}:"
            } else {
                ""
            }
            return "$hoursText${formatTime(minutes)}:${formatTime(seconds)}"
        }


    fun incrementTime() {
        millisLeft += incrementMillis
    }

    fun resetTime() {
        millisLeft = initialMillis
    }

    val hasLost: Boolean
        get() = millisLeft <= 0

    private fun formatTime(time: Int): String =
        if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }

    override fun toString(): String {
        return "Player(millisLeft=$millisLeft, increment=$incrementMillis)"
    }

    class White(initialMillis: Float, increment: Int = 0) : Player(initialMillis, increment) {
        override val text: String
            get() = if (hasLost) "Black wins" else super.text
    }

    class Black(initialMillis: Float, increment: Int = 0) : Player(initialMillis, increment) {
        override val text: String
            get() = if (hasLost) "White wins" else super.text
    }
}