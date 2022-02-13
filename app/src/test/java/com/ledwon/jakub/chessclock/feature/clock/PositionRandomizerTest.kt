package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.feature.clock.model.Player
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

        randomizer.randomizePositions(
            minRandomRounds = 6,
            white = white,
            black = black
        ).test {
            awaitItem().should.beEqualTo(white to black)

            testDispatcher.advanceTimeBy(250)
            awaitItem().should.beEqualTo(black to white)

            testDispatcher.advanceTimeBy(250)
            awaitItem().should.beEqualTo(white to black)

            testDispatcher.advanceTimeBy(250)
            awaitItem().should.beEqualTo(black to white)

            testDispatcher.advanceTimeBy(75 * 4)
            awaitItem().should.beEqualTo(white to black)

            testDispatcher.advanceTimeBy(75 * 5)
            awaitItem().should.beEqualTo(black to white)

            testDispatcher.advanceTimeBy(75 * 6)
            awaitItem().should.beEqualTo(white to black)

            awaitComplete()
        }
    }

    private fun createPositionRandomizer(randomNumber: Int) = PositionRandomizer(
        randomNumberProvider = { randomNumber },
        defaultDispatcher = testDispatcher
    )

    companion object {
        private val white = Player.White(1_500f)
        private val black = Player.Black(1_000f)
    }
}
