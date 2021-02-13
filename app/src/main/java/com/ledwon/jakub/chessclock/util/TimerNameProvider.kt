package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.data.model.ClockTime

object TimerNameProvider {

    fun createTimerName(whiteClockTime: ClockTime, blackClockTime: ClockTime): String {
        return when {
            whiteClockTime != blackClockTime -> "Custom"
            else -> createNameForClock(whiteClockTime)
        }
    }

    private fun createNameForClock(clockTime: ClockTime): String {
        val incrementSuffix = createSuffixIncrement(clockTime.increment)
        val twoDigitMinutes =
            ":${formatToTwoDigitsString(clockTime.minutes)}".takeIf { clockTime.minutes > 0 } ?: ""
        val twoDigitSeconds =
            ":${formatToTwoDigitsString(clockTime.seconds)}".takeIf { clockTime.seconds > 0 } ?: ""
        return when {
            clockTime.hours > 0 -> "Standard ${clockTime.hours}$twoDigitMinutes$twoDigitSeconds$incrementSuffix"
            clockTime.minutes > 10 -> "Rapid ${clockTime.minutes}$twoDigitSeconds$incrementSuffix"
            clockTime.minutes in 3..10 -> "Blitz ${clockTime.minutes}$twoDigitSeconds$incrementSuffix"
            clockTime.minutes > 0 -> "Bullet ${clockTime.minutes}$twoDigitSeconds$incrementSuffix"
            else -> "Bullet ${clockTime.seconds}s$incrementSuffix"
        }
    }

    private fun createSuffixIncrement(increment: Int): String {
        return " + $increment".takeIf { increment > 0 } ?: ""
    }

    private fun formatToTwoDigitsString(value: Int) = if (value < 10) "0$value" else value

}