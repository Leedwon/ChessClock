package com.ledwon.jakub.chessclock.feature.stats

import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.analytics.AnalyticsEvent
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.feature.stats.data.Move
import com.ledwon.jakub.chessclock.feature.stats.data.Moves
import com.ledwon.jakub.chessclock.feature.stats.data.PlayerColor

class StatsViewModel(
    private val movesMillis: List<Long>,
    analyticsManager: AnalyticsManager
) : ViewModel() {

    init {
        analyticsManager.logEvent(AnalyticsEvent.OpenStats)
    }

    val data: Stats
        get() {
            if (movesMillis.isEmpty()) return Stats.Empty

            val slowestMoveMillis = movesMillis.maxOrNull()!!

            val moves = movesMillis.mapIndexed { index, value ->
                Move(
                    millis = value,
                    ratioToSlowestMove = value.toFloat() / slowestMoveMillis,
                    color = if (index % 2 == 0) PlayerColor.White else PlayerColor.Black
                )
            }

            val whiteMoves = moves.filter { it.color == PlayerColor.White }
            val blackMoves = moves.filter { it.color == PlayerColor.Black }

            return Stats.Data(
                moves = moves,
                slowestMoves = Moves(
                    white = whiteMoves.maxByOrNull { it.millis },
                    black = blackMoves.maxByOrNull { it.millis },
                    general = moves.maxByOrNull { it.millis }?.copy(color = PlayerColor.Unspecified)
                ),
                fastestMoves = Moves(
                    white = whiteMoves.minByOrNull { it.millis },
                    black = blackMoves.minByOrNull { it.millis },
                    general = moves.minByOrNull { it.millis }?.copy(color = PlayerColor.Unspecified)
                ),
                averageMoves = Moves(
                    white = Move(whiteMoves.map { it.millis }.average().toLong(), color = PlayerColor.White).takeIf { whiteMoves.isNotEmpty() },
                    black = Move(blackMoves.map { it.millis }.average().toLong(), color = PlayerColor.Black).takeIf { blackMoves.isNotEmpty() },
                    general = Move(moves.map { it.millis }.average().toLong()).takeIf { moves.isNotEmpty() },
                )
            )
        }

    sealed class Stats {
        data class Data(
            val moves: List<Move>,
            val slowestMoves: Moves,
            val averageMoves: Moves,
            val fastestMoves: Moves
        ) : Stats()

        object Empty : Stats()
    }
}