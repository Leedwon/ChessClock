package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.should
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChessClockTest {

    private val testDispatcher = StandardTestDispatcher()
    private val interval = 50L
    private val currentTimeProvider = { millis }

    private var millis = 0L

    @Before
    fun setUp() {
        millis = 0L
    }

    @Test
    fun `should have correct initial state`() = runTest {
        val clock = createCountdownClock()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1000)
        }
    }

    @Test
    fun `test time passage`() = runTest {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1000)

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(950)

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(900)
        }

        clock.stop()
    }

    @Test
    fun `test stopping clock`() = runTest {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1000)

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(950)

            clock.stop()

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()
            //if stop doesn't work there should be uncollected item which would fail the test
        }

        clock.stop()
    }

    @Test
    fun `test countdown to 0`() = runTest {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)
            repeat(1_000 / interval.toInt()) {
                advanceTimeBy(interval)
                testDispatcher.scheduler.runCurrent()

                awaitItem().should.beEqualTo(1_000L - interval * (it + 1))
            }
            advanceTimeBy(interval)
            //if count would continue there should be uncollected item which would fail the test
        }

        clock.stop()
    }

    @Test
    fun `test resetting clock`() = runTest {
        val clock = createCountdownClock()
        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(950)
        }

        clock.restart()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()
            //if count would continue there should be uncollected item which would fail the test
        }

        clock.restart(startImmediately = true)

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)

            advanceTimeBy(interval)
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(950)
        }

        clock.stop()
    }

    @Test
    fun `test increment`() = runTest {
        val clock = createCountdownClock(incrementMillis = 30)

        clock.start()

        clock.millisLeft.test {
            awaitItem().should.beEqualTo(1_000L)

            advanceTimeBy(interval)
            clock.increment()
            testDispatcher.scheduler.runCurrent()

            awaitItem().should.beEqualTo(980L)
        }
    }

    private fun advanceTimeBy(intervalMillis: Long = interval) {
        millis += intervalMillis
        testDispatcher.scheduler.advanceTimeBy(intervalMillis)
    }

    private fun createCountdownClock(initialMillis: Long = 1000, intervalMillis: Long = interval, incrementMillis: Long = 0) = ChessClock(
        initialMillis = initialMillis,
        intervalMillis = intervalMillis,
        incrementMillis = incrementMillis,
        defaultDispatcher = testDispatcher,
        currentTimeMillisProvider = currentTimeProvider,
    )
}
