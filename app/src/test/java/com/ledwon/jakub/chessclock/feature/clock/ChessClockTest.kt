package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.should
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChessClockTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val interval = 50L

    @Test
    fun `test time passage`() = runBlocking {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1000)
            testDispatcher.advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
            testDispatcher.advanceTimeBy(interval)
            awaitItem().should.beEqualTo(900)
        }

        clock.stop()
    }

    @Test
    fun `test stopping clock`() = runBlocking {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1000)
            testDispatcher.advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
            clock.stop()
            testDispatcher.advanceTimeBy(interval)
            //if stop doesn't work there should be uncollected item which would fail the test
        }

        clock.stop()
    }

    @Test
    fun `test countdown to 0`() = runBlocking {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            testDispatcher.advanceTimeBy(1_000)
            repeat(1_000 / interval.toInt() + 1) {
                awaitItem().should.beEqualTo(1_000L - interval * it)
            }
            testDispatcher.advanceTimeBy(interval)
            //if count would continue there should be uncollected item which would fail the test
        }

        clock.stop()
    }

    @Test
    fun `test resetting clock`() = runBlocking {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            testDispatcher.advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
        }

        clock.reset()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            testDispatcher.advanceTimeBy(interval)
            //if count would continue there should be uncollected item which would fail the test
        }

        clock.reset(startImmediately = true)

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            testDispatcher.advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
        }

        clock.stop()
    }

    private fun createCountdownClock(initialMillis: Long = 1000, intervalMillis: Long = interval) = ChessClock(
        initialMillis = initialMillis,
        intervalMillis = intervalMillis,
        defaultDispatcher = testDispatcher,
    )
}
