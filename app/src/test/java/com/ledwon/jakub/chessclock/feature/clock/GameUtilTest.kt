package com.ledwon.jakub.chessclock.feature.clock

import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.feature.clock.GameUtil.percentageLeft
import com.ledwon.jakub.chessclock.feature.clock.GameUtil.textFromMillis
import com.ledwon.jakub.chessclock.should
import com.ledwon.jakub.chessclock.util.DeferrableString
import com.ledwon.jakub.chessclock.util.ResDeferrableString
import com.ledwon.jakub.chessclock.util.toDeferrableString
import org.junit.Test

class GameUtilTest {

    @Test
    fun `should create correct text from millis left`() {
        val testData = listOf(
            TestTextData(
                millisLeft = 30 * 1_000,
                expected = "00:30".toDeferrableString(),
            ),
            TestTextData(
                millisLeft = 90 * 1_000,
                expected = "01:30".toDeferrableString()
            ),
            TestTextData(
                millisLeft = 12 * 60 * 1_000 + 30 * 1_000,
                expected = "12:30".toDeferrableString()
            ),
            TestTextData(
                millisLeft = 1 * 60 * 60 * 1_000 + 10 * 60 * 1_000 + 5 * 1_000,
                expected = "01:10:05".toDeferrableString()
            ),
            TestTextData(
                millisLeft = 0,
                expected = ResDeferrableString(R.string.black_wins)
            ),
            TestTextData(
                millisLeft = 0,
                playerColor = Game.PlayerColor.Black,
                expected = ResDeferrableString(R.string.white_wins)
            )
        )

        testData.forEach { it.assertText() }
    }

    @Test
    fun `should create correct percentages left`() {
        val testData = listOf(
            TestPercentageData(
                player = Player(1_000),
                millisLeft = 1_000,
                expected = 1.0f
            ),
            TestPercentageData(
                player = Player(1_000),
                millisLeft = 500,
                expected = 0.5f
            ),
            TestPercentageData(
                player = Player(1_000),
                millisLeft = 0,
                expected = 0.0f
            ),
            TestPercentageData(
                player = Player(1_000),
                millisLeft = 250,
                expected = 0.25f
            ),
        )

        testData.forEach { it.assertPercentage() }
    }

    private fun TestTextData.assertText() {
        textFromMillis(millisLeft, playerColor).should.beEqualTo(expected)
    }

    private fun TestPercentageData.assertPercentage() {
        player.percentageLeft(millisLeft).should.beEqualTo(expected)
    }

    data class TestTextData(
        val millisLeft: Long,
        val expected: DeferrableString,
        val playerColor: Game.PlayerColor = Game.PlayerColor.White
    )

    data class TestPercentageData(
        val player: Player,
        val millisLeft: Long,
        val expected: Float,
    )
}
