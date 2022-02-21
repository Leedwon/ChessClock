package com.ledwon.jakub.chessclock.feature.clock.model

import androidx.annotation.FloatRange
import com.ledwon.jakub.chessclock.util.DeferrableString
import com.ledwon.jakub.chessclock.util.toDeferrableString

sealed class PlayerDisplay(
    val text: DeferrableString,
    @FloatRange(from = 0.0, to = 1.0)
    val percentageLeft: Float,
    val isActive: Boolean
) {
    class White(
        text: DeferrableString,
        percentageLeft: Float,
        isActive: Boolean
    ) : PlayerDisplay(text, percentageLeft, isActive)

    class Black(
        text: DeferrableString,
        percentageLeft: Float,
        isActive: Boolean
    ) : PlayerDisplay(text, percentageLeft, isActive)

    fun isFor(player: Player): Boolean =
        this is White && player is Player.White || this is Black && player is Player.Black

    companion object {
        fun from(player: Player, isActive: Boolean): PlayerDisplay {
            return when (player) {
                is Player.White -> White(
                    player.text.toDeferrableString(),
                    player.percentageLeft,
                    isActive
                )
                is Player.Black -> Black(
                    player.text.toDeferrableString(),
                    player.percentageLeft,
                    isActive
                )
            }
        }
    }
}