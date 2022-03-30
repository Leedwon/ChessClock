package com.ledwon.jakub.chessclock.util

import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.choose_clock.ClockTimeType
import com.ledwon.jakub.chessclock.feature.choose_clock.clockTimeType
import com.ledwon.jakub.chessclock.model.Clock

object ClockNameProvider {

    fun Clock.obtainDeferrableName(): ResDeferrableString {
        val playerTime = whitePlayerTime
        val incrementSuffix = createSuffixIncrement(playerTime.increment)
        val twoDigitMinutes =
            ":${formatToTwoDigitsString(playerTime.minutes)}".takeIf { playerTime.minutes > 0 } ?: ""
        val twoDigitSeconds =
            ":${formatToTwoDigitsString(playerTime.seconds)}".takeIf { playerTime.seconds > 0 } ?: ""
        return when (this.clockTimeType) {
            ClockTimeType.Bullet ->
                if (playerTime.minutes > 0) {
                    ResDeferrableString(
                        resId = R.string.clock_name_bullet,
                        formatArgs = listOf("${playerTime.minutes}$twoDigitSeconds$incrementSuffix")
                    )
                } else {
                    ResDeferrableString(
                        resId = R.string.clock_name_bullet,
                        formatArgs = listOf("${playerTime.seconds}s$incrementSuffix")
                    )
                }
            ClockTimeType.Blitz -> ResDeferrableString(
                resId = R.string.clock_name_blitz,
                formatArgs = listOf("${playerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            ClockTimeType.Rapid -> ResDeferrableString(
                resId = R.string.clock_name_rapid,
                formatArgs = listOf("${playerTime.minutes}$twoDigitSeconds$incrementSuffix")
            )
            ClockTimeType.Classic -> ResDeferrableString(
                resId = R.string.clock_name_classic,
                formatArgs = listOf("${playerTime.hours}$twoDigitMinutes$twoDigitSeconds$incrementSuffix")
            )
            ClockTimeType.Custom -> ResDeferrableString(R.string.clock_name_custom)
        }
    }

    private fun createSuffixIncrement(increment: Int): String = " + $increment".takeIf { increment > 0 } ?: ""

    private fun formatToTwoDigitsString(value: Int) = if (value < 10) "0$value" else value
}
