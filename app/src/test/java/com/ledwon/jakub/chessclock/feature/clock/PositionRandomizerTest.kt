package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor
import com.ledwon.jakub.chessclock.should
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PositionRandomizerTest {
    
    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun `test randomizing positions`() = runBlocking {
        val randomizer = createPositionRandomizer(0)

        randomizer.randomizePositions(minRandomRounds = 6).test {
            testDispatcher.scheduler.advanceUntilIdle()

            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            testDispatcher.scheduler.advanceTimeBy(250)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(PlayerColor.Black to PlayerColor.White)

            testDispatcher.scheduler.advanceTimeBy(250)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            testDispatcher.scheduler.advanceTimeBy(250)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(PlayerColor.Black to PlayerColor.White)

            testDispatcher.scheduler.advanceTimeBy(75 * 4)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            testDispatcher.scheduler.advanceTimeBy(75 * 5)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(PlayerColor.Black to PlayerColor.White)

            testDispatcher.scheduler.advanceTimeBy(75 * 6)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(PlayerColor.White to PlayerColor.Black)

            awaitComplete()
        }
    }

    private fun createPositionRandomizer(randomNumber: Int) = PositionRandomizer(
        randomNumberProvider = { randomNumber },
        defaultDispatcher = testDispatcher
    )
}
