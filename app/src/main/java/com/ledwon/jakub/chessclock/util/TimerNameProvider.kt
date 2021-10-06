package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer

object TimerNameProvider {

    fun Timer.obtainDeferrableName(): DeferrableString = createTimerName(
        whiteClockTime = this.whiteClockTime,
        blackClockTime = this.blackClockTime
    )

    private fun createTimerName(whiteClockTime: ClockTime, blackClockTime: ClockTime): DeferrableString {
        return when {
            whiteClockTime != blackClockTime -> DeferrableString(R.string.clock_name_custom)
            else -> createNameForClock(whiteClockTime)
        }
    }

    private fun createNameForClock(clockTime: ClockTime): DeferrableString {
        val incrementSuffix = createSuffixIncrement(clockTime.increment)
        val twoDigitMinutes =
            ":${formatToTwoDigitsString(clockTime.minutes)}".takeIf { clockTime.minutes > 0 } ?: ""
        val twoDigitSeconds =
            ":${formatToTwoDigitsString(clockTime.seconds)}".takeIf { clockTime.seconds > 0 } ?: ""
        return when {
            clockTime.hours > 0 -> DeferrableString(
                resId = R.string.clock_name_classic,
                formatArgs = listOf("${clockTime.hours}$twoDigitMinutes$twoDigitSeconds$incrementSuffix")
            )
            clockTime.minutes > 10 -> DeferrableString(
                resId = R.string.clock_name_rapid,
                formatArgs = listOf("${clockTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            clockTime.minutes in 3..10 -> DeferrableString(
                resId = R.string.clock_name_blitz,
                formatArgs = listOf("${clockTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            clockTime.minutes > 0 -> DeferrableString(
                resId = R.string.clock_name_bullet,
                formatArgs = listOf("${clockTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            else -> DeferrableString(
                resId = R.string.clock_name_bullet,
                formatArgs = listOf("${clockTime.seconds}s$incrementSuffix")
            )
        }
    }

    private fun createSuffixIncrement(increment: Int): String {
        return " + $increment".takeIf { increment > 0 } ?: ""
    }

    private fun formatToTwoDigitsString(value: Int) = if (value < 10) "0$value" else value

}