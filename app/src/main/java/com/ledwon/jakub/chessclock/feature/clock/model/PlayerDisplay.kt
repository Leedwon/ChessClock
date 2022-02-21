package com.ledwon.jakub.chessclock.feature.clock.model

import androidx.annotation.FloatRange
import com.ledwon.jakub.chessclock.util.DeferrableString
import com.ledwon.jakub.chessclock.util.toDeferrableString

sealed class PlayerDisplay {
    abstract val text: DeferrableString
    abstract val percentageLeft: Float
    abstract val isActive: Boolean

    data class White(
        override val text: DeferrableString,
        @FloatRange(from = 0.0, to = 1.0)
        override val percentageLeft: Float,
        override val isActive: Boolean
    ) : PlayerDisplay()

    data class Black(
        override val text: DeferrableString,
        @FloatRange(from = 0.0, to = 1.0)
        override val percentageLeft: Float,
        override val isActive: Boolean
    ) : PlayerDisplay()

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