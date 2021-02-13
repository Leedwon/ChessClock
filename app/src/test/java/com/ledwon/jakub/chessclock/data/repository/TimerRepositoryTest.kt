package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.data.persistance.TimerDao
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations.openMocks

class TimerRepositoryTest {

    @Mock
    private lateinit var timerDaoMock: TimerDao

    @Before
    fun before() {
        openMocks(this)
    }


    private fun createRepo(): TimerRepository = TimerRepository(timerDao = timerDaoMock)

    @ExperimentalCoroutinesApi
    @Test
    fun `should get all timers`() {
        runBlockingTest {

            val timers = listOf(
                Timer(
                    id = 1,
                    description = "testTimer",
                    whiteClockTime = ClockTime(),
                    isFavourite = false
                )
            )

            whenever(timerDaoMock.getAllTimers()).thenReturn(
                flowOf(timers)
            )

            val repo = createRepo()

            repo.timers.take(1).collect {
                assertEquals(it, timers)
                verify(timerDaoMock).getAllTimers()
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
                    description = "testTimer",
                    whiteClockTime = ClockTime(),
                    isFavourite = false
                )
            )

            whenever(timerDaoMock.getAllTimers()).thenReturn(
                flowOf(timers)
            )

            val repo = createRepo()

            repo.timers.take(1).collect() // populate cache
            assertEquals(repo.getTimerById(1), timers[0])
            verify(timerDaoMock, never()).getById(1)
        }
    }


}