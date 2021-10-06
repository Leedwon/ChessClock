package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.data.persistance.TimerDao
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TimerRepositoryTest {

    @MockK
    private lateinit var timerDaoMock: TimerDao

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    private fun createRepo(): TimerRepository = TimerRepository(timerDao = timerDaoMock)

    @ExperimentalCoroutinesApi
    @Test
    fun `should get all timers`() {
        runBlockingTest {

            val timers = listOf(
                Timer(
                    id = 1,
                    whiteClockTime = ClockTime(),
                    isFavourite = false
                )
            )

            every { timerDaoMock.getAllTimers() } returns flowOf(timers)

            val repo = createRepo()

            repo.timers.take(1).collect {
                assertEquals(it, timers)
                verify { timerDaoMock.getAllTimers() }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should get timer from cache`() {
        runBlockingTest {
            val timers = listOf(
                Timer(
                    id = 1,
                    whiteClockTime = ClockTime(),
                    isFavourite = false
                )
            )

            every { timerDaoMock.getAllTimers() } returns flowOf(timers)

            val repo = createRepo()

            launch(this.coroutineContext) { repo.timers.take(1).collect() }
            launch(this.coroutineContext) {
                assertEquals(repo.getTimerById(1), timers[0])
            } // populate cache
            coVerify(exactly = 0) { timerDaoMock.getById(1) }
        }
    }
}