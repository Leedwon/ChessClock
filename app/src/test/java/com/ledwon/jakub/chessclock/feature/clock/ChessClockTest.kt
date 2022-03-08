package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.should
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChessClockTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val interval = 50L
    private val currentTimeProvider = { millis }

    private var millis = 0L

    @Before
    fun setUp() {
        millis = 0L
    }

    @Test
    fun `test time passage`() = runBlocking {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1000)
            advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
            advanceTimeBy(interval)
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
            advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
            clock.stop()
            advanceTimeBy(interval)
            //if stop doesn't work there should be uncollected item which would fail the test
        }

        clock.stop()
    }

    @Test
    fun `test countdown to 0`() = runBlocking {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            repeat(1_000 / interval.toInt()) {
                advanceTimeBy(interval)
                awaitItem().should.beEqualTo(1_000L - interval * (it + 1))
            }
            advanceTimeBy(interval)
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
            advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
        }

        clock.restart()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            advanceTimeBy(interval)
            //if count would continue there should be uncollected item which would fail the test
        }

        clock.restart(startImmediately = true)

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            advanceTimeBy(interval)
            awaitItem().should.beEqualTo(950)
        }

        clock.stop()
    }

    @Test
    fun `test increment`() = runBlocking {
        val clock = createCountdownClock(incrementMillis = 30)

        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            advanceTimeBy(interval)
            clock.increment()
            awaitItem().should.beEqualTo(950L)
            awaitItem().should.beEqualTo(980L)
        }
    }

    private fun advanceTimeBy(intervalMillis: Long = interval) {
        millis += intervalMillis
        testDispatcher.advanceTimeBy(intervalMillis)
    }

    private fun createCountdownClock(initialMillis: Long = 1000, intervalMillis: Long = interval, incrementMillis: Long = 0) = ChessClock(
        initialMillis = initialMillis,
        intervalMillis = intervalMillis,
        incrementMillis = incrementMillis,
        defaultDispatcher = testDispatcher,
        currentTimeMillisProvider = currentTimeProvider,
    )
}
