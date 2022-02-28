package com.ledwon.jakub.chessclock.feature.clock

import androidx.annotation.FloatRange
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.util.DeferrableString
import com.ledwon.jakub.chessclock.util.ResDeferrableString
import com.ledwon.jakub.chessclock.util.toDeferrableString

object GameUtil {

    /**
     * Get formatted time text from millis
     */
    fun textFromMillis(millisLeft: Long, playerColor: Game.PlayerColor): DeferrableString {
        return if (millisLeft <= 0) {
            playerColor.lostDeferrableString
        } else {
            val hoursText = if(millisLeft.hours > 0) "${millisLeft.hours.toTwoDigitsString()}:" else ""
            val minutesText = millisLeft.minutes.toTwoDigitsString()
            val secondsLeft = millisLeft.seconds.toTwoDigitsString()
            return "$hoursText$minutesText:$secondsLeft".trimIndent().toDeferrableString()
        }
    }

    private val Game.PlayerColor.lostDeferrableString: ResDeferrableString
        get() = when (this) {
            Game.PlayerColor.White -> ResDeferrableString(R.string.black_wins)
            Game.PlayerColor.Black -> ResDeferrableString(R.string.white_wins)
        }

    @FloatRange(from = 0.0, to = 1.0, fromInclusive = true, toInclusive = true)
    fun Player.percentageLeft(millisLeft: Long): Float = millisLeft.toFloat() / millis

    /**
     * Get hours from millis
     */
    private val Long.hours: Long
        get() = this / 1000 / 60 / 60

    /**
     * Get minutes from millis
     */
    private val Long.minutes: Long
        get() = this / 1000 / 60 % 60

    /**
     * Get seconds from millis
     */
    private val Long.seconds: Long
        get() = this / 1000 % 60

    private fun Long.toTwoDigitsString(): String =
        if (this < 10) "0$this" else this.toString()
}
