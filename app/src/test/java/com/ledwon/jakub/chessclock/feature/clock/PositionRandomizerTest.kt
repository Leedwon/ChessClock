package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor
import com.ledwon.jakub.chessclock.should
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test

class PositionRandomizerTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = TestCoroutineDispatcher()

    @Test
    fun `test randomizing positions`() = runBlocking {
        val randomizer = createPositionRandomizer(0)

        randomizer.randomizePositions(minRandomRounds = 6).test {
            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            testDispatcher.advanceTimeBy(250)
            awaitItem().should.beEqualTo(PlayerColor.Black to PlayerColor.White)

            testDispatcher.advanceTimeBy(250)
            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            testDispatcher.advanceTimeBy(250)
            awaitItem().should.beEqualTo(PlayerColor.Black to PlayerColor.White)

            testDispatcher.advanceTimeBy(75 * 4)
            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            testDispatcher.advanceTimeBy(75 * 5)
            awaitItem().should.beEqualTo(PlayerColor.Black to PlayerColor.White)

            testDispatcher.advanceTimeBy(75 * 6)
            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            awaitComplete()
        }
    }

    private fun createPositionRandomizer(randomNumber: Int) = PositionRandomizer(
        randomNumberProvider = { randomNumber },
        defaultDispatcher = testDispatcher
    )
}
