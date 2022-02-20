package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.model.Clock

object ClockNameProvider {

    fun Clock.obtainDeferrableName(): ResDeferrableString = createClockName(
        whitePlayerTime = this.whitePlayerTime,
        blackPlayerTime = this.blackPlayerTime
    )

    private fun createClockName(whitePlayerTime: PlayerTime, blackPlayerTime: PlayerTime): ResDeferrableString {
        return when {
            whitePlayerTime != blackPlayerTime -> ResDeferrableString(R.string.clock_name_custom)
            else -> createNameForClock(whitePlayerTime)
        }
    }

    private fun createNameForClock(PlayerTime: PlayerTime): ResDeferrableString {
        val incrementSuffix = createSuffixIncrement(PlayerTime.increment)
        val twoDigitMinutes =
            ":${formatToTwoDigitsString(PlayerTime.minutes)}".takeIf { PlayerTime.minutes > 0 } ?: ""
        val twoDigitSeconds =
            ":${formatToTwoDigitsString(PlayerTime.seconds)}".takeIf { PlayerTime.seconds > 0 } ?: ""
        return when {
            PlayerTime.hours > 0 -> ResDeferrableString(
                resId = R.string.clock_name_classic,
                formatArgs = listOf("${PlayerTime.hours}$twoDigitMinutes$twoDigitSeconds$incrementSuffix")
            )
            PlayerTime.minutes > 10 -> ResDeferrableString(
                resId = R.string.clock_name_rapid,
                formatArgs = listOf("${PlayerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            PlayerTime.minutes in 3..10 -> ResDeferrableString(
                resId = R.string.clock_name_blitz,
                formatArgs = listOf("${PlayerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            PlayerTime.minutes > 0 -> ResDeferrableString(
                resId = R.string.clock_name_bullet,
                formatArgs = listOf("${PlayerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            else -> ResDeferrableString(
                resId = R.string.clock_name_bullet,
                formatArgs = listOf("${PlayerTime.seconds}s$incrementSuffix")
            )
        }
    }

    private fun createSuffixIncrement(increment: Int): String {
        return " + $increment".takeIf { increment > 0 } ?: ""
    }

    private fun formatToTwoDigitsString(value: Int) = if (value < 10) "0$value" else value

}