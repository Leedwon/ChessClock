package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.assertThrows
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.should
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import java.lang.IllegalStateException

class MovesTrackerTest {

    private var currentMillisMock = 0L

    private val movesTracker = MovesTracker(currentTimeMillisProvider = { currentMillisMock })

    private fun mockCurrentTimeMillis(value: Long) {
        currentMillisMock = value
    }

    @Test
    fun `should throw exception when move is ended but not started`() {
        val actual = assertThrows<IllegalStateException> {
            movesTracker.moveEnded()
        }

        actual.message.should.beEqualTo("You have to call moveStarted before calling moveEnded")
    }

    @Test
    fun `should throw exception when pausing move which is not started`() {
        val actual = assertThrows<IllegalStateException> {
            movesTracker.movePaused()
        }

        actual.message.should.beEqualTo("You can't pause move which is not started, call moveStarted first")
    }

    @Test
    fun `should throw exception when pausing move which is already paused`() {
        val actual = assertThrows<IllegalStateException> {
            movesTracker.moveStarted()
            movesTracker.movePaused()
            movesTracker.movePaused()
        }

        actual.message.should.beEqualTo("Pause already started and not resumed, to start another pause call moveResumed first")
    }

    @Test
    fun `should throw exception when resuming move which is not paused`() {
        val actual = assertThrows<IllegalStateException> {
            movesTracker.moveResumed()
        }

        actual.message.should.beEqualTo("You have to call movePaused before calling moveResumed")
    }

    @Test
    fun `should throw exception when ending move which is still paused`() {
        val actual = assertThrows<IllegalStateException> {
            movesTracker.moveStarted()
            movesTracker.movePaused()
            movesTracker.moveEnded()
        }

        actual.message.should.beEqualTo("You can't end move which is still paused, call moveResumed first")
    }

    @Test
    fun `should throw exception when ending move which is not started`() {
        val actual = assertThrows<IllegalStateException> {
            movesTracker.moveStarted()
            movesTracker.moveEnded()
            movesTracker.moveEnded()
        }

        actual.message.should.beEqualTo("You have to call moveStarted before calling moveEnded")
    }

    @Test
    fun `should correctly track moves with no pause`() = runBlocking {
        movesTracker.movesMillis.test {
            awaitItem().should.beEqualTo(emptyList())

            mockCurrentTimeMillis(100)
            movesTracker.moveStarted()

            mockCurrentTimeMillis(200)
            movesTracker.moveEnded()

            awaitItem().should.beEqualTo(listOf(100))

            mockCurrentTimeMillis(300)
            movesTracker.moveStarted()

            mockCurrentTimeMillis(500)
            movesTracker.moveEnded()

            awaitItem().should.beEqualTo(listOf(100,200))
        }
       
    }

    @Test
    fun `should correctly track moves with pauses`() = runBlocking {
        movesTracker.movesMillis.test {
            awaitItem().should.beEqualTo(emptyList())

            mockCurrentTimeMillis(100)
            movesTracker.moveStarted()

            mockCurrentTimeMillis(200)
            movesTracker.movePaused()

            mockCurrentTimeMillis(300)
            movesTracker.moveResumed()

            mockCurrentTimeMillis(500)
            movesTracker.movePaused()

            mockCurrentTimeMillis(600)
            movesTracker.moveResumed()

            mockCurrentTimeMillis(800)
            movesTracker.moveEnded()

            awaitItem().should.beEqualTo(listOf(500))

            mockCurrentTimeMillis(900)
            movesTracker.moveStarted()

            mockCurrentTimeMillis(950)
            movesTracker.movePaused()

            mockCurrentTimeMillis(1000)
            movesTracker.moveResumed()

            mockCurrentTimeMillis(1100)
            movesTracker.moveEnded()

            awaitItem().should.beEqualTo(listOf(500, 150))
        }
    }
}
