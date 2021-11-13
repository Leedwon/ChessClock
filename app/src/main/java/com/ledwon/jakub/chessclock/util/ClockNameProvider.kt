package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.model.Clock

object ClockNameProvider {

    fun Clock.obtainDeferrableName(): DeferrableString = createClockName(
        whitePlayerTime = this.whitePlayerTime,
        blackPlayerTime = this.blackPlayerTime
    )

    private fun createClockName(whitePlayerTime: PlayerTime, blackPlayerTime: PlayerTime): DeferrableString {
        return when {
            whitePlayerTime != blackPlayerTime -> DeferrableString(R.string.clock_name_custom)
            else -> createNameForClock(whitePlayerTime)
        }
    }

    private fun createNameForClock(PlayerTime: PlayerTime): DeferrableString {
        val incrementSuffix = createSuffixIncrement(PlayerTime.increment)
        val twoDigitMinutes =
            ":${formatToTwoDigitsString(PlayerTime.minutes)}".takeIf { PlayerTime.minutes > 0 } ?: ""
        val twoDigitSeconds =
            ":${formatToTwoDigitsString(PlayerTime.seconds)}".takeIf { PlayerTime.seconds > 0 } ?: ""
        return when {
            PlayerTime.hours > 0 -> DeferrableString(
                resId = R.string.clock_name_classic,
                formatArgs = listOf("${PlayerTime.hours}$twoDigitMinutes$twoDigitSeconds$incrementSuffix")
            )
            PlayerTime.minutes > 10 -> DeferrableString(
                resId = R.string.clock_name_rapid,
                formatArgs = listOf("${PlayerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            PlayerTime.minutes in 3..10 -> DeferrableString(
                resId = R.string.clock_name_blitz,
                formatArgs = listOf("${PlayerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            PlayerTime.minutes > 0 -> DeferrableString(
                resId = R.string.clock_name_bullet,
                formatArgs = listOf("${PlayerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            else -> DeferrableString(
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