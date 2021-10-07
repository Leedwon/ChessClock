package com.ledwon.jakub.chessclock.feature.stats.data

/**
 * @param ratioToSlowestMove - this move millis/slowestMoveMillis
 */
data class Move(
    val millis: Long,
    val color: PlayerColor = PlayerColor.Unspecified,
    val ratioToSlowestMove: Float? = null
) {
    val duration: String
        get() {
            return "${(millis / 10 / 100f)}s"
        }
}