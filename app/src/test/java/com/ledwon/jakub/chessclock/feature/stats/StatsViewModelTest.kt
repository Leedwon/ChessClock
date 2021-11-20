package com.ledwon.jakub.chessclock.feature.stats

import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.feature.stats.data.Move
import com.ledwon.jakub.chessclock.feature.stats.data.Moves
import com.ledwon.jakub.chessclock.feature.stats.data.PlayerColor
import com.ledwon.jakub.chessclock.should
import org.junit.Test

class StatsViewModelTest {

    private lateinit var viewModel: StatsViewModel

    private fun init(movesMillis: List<Long>) {
        viewModel = StatsViewModel(movesMillis)
    }

    @Test
    fun `empty moves should result in empty state`() {
        init(emptyList())

        viewModel.data.should.beEqualTo(StatsViewModel.Stats.Empty)
    }

    @Test
    fun `not empty moves should result in correct data`() {
        val slowest = 1000L

        fun Int.toMoveWithRatio(color: PlayerColor) = Move(this.toLong(), ratioToSlowestMove = this.toFloat() / slowest, color = color)
        fun Int.toMove(color: PlayerColor) = Move(this.toLong(), color = color)

        val movesMillis: List<Long> = listOf(
            50,
            100,
            100,
            100,
            100,
            100,
            100,
            200,
            150,
            slowest
        )

        init(movesMillis)

        viewModel.data.should.beEqualTo(
            StatsViewModel.Stats.Data(
                moves = listOf(
                    50.toMoveWithRatio(color = PlayerColor.White),
                    100.toMoveWithRatio(color = PlayerColor.Black),
                    100.toMoveWithRatio(color = PlayerColor.White),
                    100.toMoveWithRatio(color = PlayerColor.Black),
                    100.toMoveWithRatio(color = PlayerColor.White),
                    100.toMoveWithRatio(color = PlayerColor.Black),
                    100.toMoveWithRatio(color = PlayerColor.White),
                    200.toMoveWithRatio(color = PlayerColor.Black),
                    150.toMoveWithRatio(color = PlayerColor.White),
                    Move(millis = slowest, ratioToSlowestMove = 1.0f, color = PlayerColor.Black)
                ),
                fastestMoves = Moves(
                    white = 50.toMoveWithRatio(PlayerColor.White),
                    black = 100.toMoveWithRatio(PlayerColor.Black),
                    general = 50.toMoveWithRatio(PlayerColor.Unspecified)
                ),
                averageMoves = Moves(
                    white = 100.toMove(PlayerColor.White),
                    black = 300.toMove(PlayerColor.Black),
                    general = 200.toMove(PlayerColor.Unspecified)
                ),
                slowestMoves = Moves(
                    white = 150.toMoveWithRatio(PlayerColor.White),
                    black = 1000.toMoveWithRatio(PlayerColor.Black),
                    general = 1000.toMoveWithRatio(PlayerColor.Unspecified)
                )
            )
        )
    }

    @Test
    fun `should handle one move scenario`() {
        val movesMillis = listOf(100L)

        init(movesMillis)

        viewModel.data.should.beEqualTo(
            StatsViewModel.Stats.Data(
                moves = listOf(Move(100L, PlayerColor.White, 1.0f)),
                slowestMoves = Moves(
                    white = Move(100L, PlayerColor.White, 1.0f),
                    black = null,
                    general = Move(100L, PlayerColor.Unspecified, 1.0f)
                ),
                fastestMoves = Moves(
                    white = Move(100L, PlayerColor.White, 1.0f),
                    black = null,
                    general = Move(100L, PlayerColor.Unspecified, 1.0f)
                ),
                averageMoves = Moves(
                    white = Move(100L, PlayerColor.White),
                    black = null,
                    general = Move(100L, PlayerColor.Unspecified)
                )
            )
        )
    }
}