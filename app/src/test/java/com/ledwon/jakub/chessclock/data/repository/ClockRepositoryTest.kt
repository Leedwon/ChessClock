package com.ledwon.jakub.chessclock.data.repository

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.should
import com.ledwon.jakub.database.DatabaseInteractor
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClockRepositoryTest {

    @MockK
    private lateinit var dbInteractorMock: DatabaseInteractor

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    private fun createRepo(): ClockRepository = ClockRepository(
        databaseInteractor = dbInteractorMock,
        ioDispatcher = testDispatcher
    )

    @Test
    fun `should get all clocks`() = runTest(testDispatcher) {
        val clocks = listOf(
            Clock(
                id = 1,
                whitePlayerTime = PlayerTime(),
                isFavourite = false
            )
        )

        every { dbInteractorMock.getAllClocks() } returns flowOf(clocks)

        val repo = createRepo()

        repo.clocks.test {
            awaitItem().should.beEqualTo(clocks)
            awaitComplete()
            verify { dbInteractorMock.getAllClocks() }
        }
    }
}