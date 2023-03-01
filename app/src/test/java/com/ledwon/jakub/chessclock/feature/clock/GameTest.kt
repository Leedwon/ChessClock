package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.feature.clock.Game.*
import com.ledwon.jakub.chessclock.feature.clock.model.ClockState
import com.ledwon.jakub.chessclock.should
import com.ledwon.jakub.chessclock.util.ResDeferrableString
import com.ledwon.jakub.chessclock.util.toDeferrableString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameTest {

    private val testDispatcher = StandardTestDispatcher()
    private val movesTracker = MovesTracker(currentTimeMillisProvider = { millis })
    private val interval = 100L

    private var millis = 0L

    private fun createGame(
        white: Player = testWhite,
        black: Player = testBlack,
    ): Game = Game(
        white = white,
        black = black,
        movesTracker = movesTracker,
        defaultDispatcher = testDispatcher,
        intervalMillis = interval,
        currentTimeMillisProvider = { millis }
    )

    @Before
    fun setUp() {
        movesTracker.restart()
        millis = 0
    }

    @Test
    fun `should have correct initial state`() = runBlocking {
        val game = createGame()

        game.state.test {
            awaitItem()
                .assertWhite(PlayerUiState("01:00".toDeferrableString(), percentageLeft = 1.0f, isActive = false))
                .assertBlack(PlayerUiState("01:00".toDeferrableString(), percentageLeft = 1.0f, isActive = false))
                .assertGameState(ClockState.BeforeStarted)
                .assertMoves(emptyList())
        }
    }

    @Test
    fun `test players moves`() = runGameTest {
        game.run {
            state.test {
                white = white.copy(text = "01:00".toDeferrableString())
                black = black.copy(text = "01:00".toDeferrableString())
                awaitItem().should.beEqualTo(createState())

                start()

                white = white.copy(isActive= true)
                clockState = ClockState.Running
                awaitItem().should.beEqualTo(createState())

                repeat(10) {
                    advanceTimeBy(interval)
                    testDispatcher.scheduler.runCurrent()

                    white = PlayerUiState(
                        "00:59".toDeferrableString(),
                        percentageLeft = (60_000.toFloat() - interval * (it + 1)) / 60_000,
                        isActive = true
                    )
                    awaitItem().should.beEqualTo(createState())
                }

                playerMoveFinished()

                movesTracked = listOf(1_000L)
                awaitItem().should.beEqualTo(createState())

                white = white.copy(isActive = false)
                black = black.copy(isActive = true)
                awaitItem().should.beEqualTo(createState())

                repeat(15) {
                    val timePassed = it + 1

                    advanceTimeBy(interval)
                    testDispatcher.scheduler.runCurrent()

                    val text = if (timePassed <= 10) "00:59".toDeferrableString() else "00:58".toDeferrableString()
                    black = PlayerUiState(
                        text = text,
                        percentageLeft = (60_000.toFloat() - interval * timePassed) / 60_000,
                        isActive = true
                    )
                    awaitItem().should.beEqualTo(createState())
                }

                playerMoveFinished()

                movesTracked = listOf(1_000L, 1_500L)
                awaitItem().should.beEqualTo(createState())

                white = white.copy(isActive = true)
                black = black.copy(isActive = false)
                awaitItem().should.beEqualTo(createState())
            }
        }
    }

    @Test
    fun `test players moves with pause`() = runGameTest {
        game.run {
            state.test {
                white = white.copy(text = "01:00".toDeferrableString())
                black = black.copy(text = "01:00".toDeferrableString())
                awaitItem().should.beEqualTo(createState())

                start()

                white = white.copy(isActive= true)
                clockState = ClockState.Running
                awaitItem().should.beEqualTo(createState())

                repeat(5) {
                    advanceTimeBy(interval)
                    testDispatcher.scheduler.runCurrent()

                    white = PlayerUiState(
                        "00:59".toDeferrableString(),
                        percentageLeft = (60_000.toFloat() - interval * (it + 1)) / 60_000,
                        isActive = true
                    )
                    awaitItem().should.beEqualTo(createState())
                }

                pause()
                clockState = ClockState.Paused
                awaitItem().should.beEqualTo(createState())

                advanceTimeBy(500)
                testDispatcher.scheduler.runCurrent()

                resume()
                clockState = ClockState.Running
                awaitItem().should.beEqualTo(createState())

                repeat(5) {
                    advanceTimeBy(interval)
                    testDispatcher.scheduler.runCurrent()

                    white = PlayerUiState(
                        "00:59".toDeferrableString(),
                        percentageLeft = (59_500.toFloat() - interval * (it + 1)) / 60_000,
                        isActive = true
                    )
                    awaitItem().should.beEqualTo(createState())
                }

                playerMoveFinished()

                movesTracked = listOf(1_000L)
                awaitItem().should.beEqualTo(createState())

                white = white.copy(isActive = false)
                black = black.copy(isActive = true)
                awaitItem().should.beEqualTo(createState())
            }
        }
    }

    @Test
    fun `test players moves with increment`() = runGameTest(
        white = testWhite.copy(incrementMillis = 50L),
        black = testBlack.copy(incrementMillis = 50L),
    ) {
        game.run {
            state.test {
                white = white.copy(text = "01:00".toDeferrableString())
                black = black.copy(text = "01:00".toDeferrableString())
                awaitItem().should.beEqualTo(createState())

                start()

                white = white.copy(isActive= true)
                clockState = ClockState.Running
                awaitItem().should.beEqualTo(createState())

                advanceTimeBy(100)
                testDispatcher.scheduler.runCurrent()

                white = PlayerUiState(
                    "00:59".toDeferrableString(),
                    percentageLeft = 59_900.toFloat() / 60_000,
                    isActive = true
                )
                awaitItem().should.beEqualTo(createState())

                playerMoveFinished()

                white = PlayerUiState(
                    "00:59".toDeferrableString(),
                    percentageLeft = 59_950.toFloat() / 60_000,
                    isActive = true
                )
                awaitItem().should.beEqualTo(createState())

                movesTracked = listOf(100L)
                awaitItem().should.beEqualTo(createState())

                white = white.copy(isActive = false)
                black = black.copy(isActive = true)
                awaitItem().should.beEqualTo(createState())

                advanceTimeBy(100)
                testDispatcher.scheduler.runCurrent()

                black = PlayerUiState(
                    "00:59".toDeferrableString(),
                    percentageLeft = 59_900.toFloat() / 60_000,
                    isActive = true
                )
                awaitItem().should.beEqualTo(createState())

                playerMoveFinished()

                black = PlayerUiState(
                    "00:59".toDeferrableString(),
                    percentageLeft = 59_950.toFloat() / 60_000,
                    isActive = true
                )
                awaitItem().should.beEqualTo(createState())

                movesTracked = listOf(100L, 100L)
                awaitItem().should.beEqualTo(createState())

                white = white.copy(isActive = true)
                black = black.copy(isActive = false)
                awaitItem().should.beEqualTo(createState())
            }
        }
    }

    @Test
    fun `test game ended`() = runGameTest(
        white = testWhite.copy(millis = 2_000),
        black = testBlack.copy(millis = 2_000),
    ) {
        game.run {
            state.test {
                white = white.copy(text = "00:02".toDeferrableString())
                black = black.copy(text = "00:02".toDeferrableString())
                awaitItem().should.beEqualTo(createState())

                start()

                white = white.copy(isActive= true)
                clockState = ClockState.Running
                awaitItem().should.beEqualTo(createState())

                repeat(20) {
                    val timePassed = it + 1

                    advanceTimeBy(interval)
                    testDispatcher.scheduler.runCurrent()

                    val text = when {
                        timePassed <= 10 -> "00:01".toDeferrableString()
                        timePassed == 20 -> ResDeferrableString(R.string.black_wins)
                        else -> "00:00".toDeferrableString()
                    }

                    white = PlayerUiState(
                        text = text,
                        percentageLeft = (2_000.toFloat() - interval * timePassed) / 2_000,
                        isActive = true
                    )
                    if (timePassed == 20) {
                        clockState = ClockState.Over
                    }
                    awaitItem().should.beEqualTo(createState())
                }
            }
        }
    }

    @Test
    fun `should restart game`() = runGameTest {
        game.run {
            state.test {
                white = white.copy(text = "01:00".toDeferrableString())
                black = black.copy(text = "01:00".toDeferrableString())
                awaitItem().should.beEqualTo(createState())

                start()

                clockState = ClockState.Running
                white = white.copy(isActive = true)
                awaitItem().should.beEqualTo(createState())

                advanceTimeBy(interval)
                testDispatcher.scheduler.runCurrent()

                white = PlayerUiState(
                    "00:59".toDeferrableString(),
                    percentageLeft = (59_900F) / 60_000,
                    isActive = true
                )
                awaitItem().should.beEqualTo(createState())

                game.playerMoveFinished()

                movesTracked = listOf(100L)
                awaitItem().should.beEqualTo(createState())

                white = white.copy(isActive = false)
                black = black.copy(isActive = true)

                awaitItem().should.beEqualTo(createState())

                game.restart()

                black = black.copy(isActive = false)
                clockState = ClockState.BeforeStarted

                awaitItem().should.beEqualTo(createState())

                white = white.copy(text = "01:00".toDeferrableString(), percentageLeft = 1.0f)
                black = black.copy(text = "01:00".toDeferrableString())

                awaitItem().should.beEqualTo(createState())

                movesTracked = emptyList()

                awaitItem().should.beEqualTo(createState())

                start()

                clockState = ClockState.Running
                white = white.copy(isActive = true)
                awaitItem().should.beEqualTo(createState())
            }
        }
    }

    private fun runGameTest(white: Player = testWhite, black: Player = testBlack, testBody: suspend GameTestScope.() -> Unit) =
        runBlocking {
            val gameTestScope = GameTestScope(createGame(white, black))

            testBody.invoke(gameTestScope)
        }

    private class GameTestScope(val game: Game) {
        var white = PlayerUiState("".toDeferrableString(), percentageLeft = 1.0f, isActive = false)
        var black = PlayerUiState("".toDeferrableString(), percentageLeft = 1.0f, isActive = false)
        var clockState = ClockState.BeforeStarted
        var movesTracked = emptyList<Long>()


        fun createState(): State = State(
            white = white,
            black = black,
            clockState = clockState,
            movesTracked = movesTracked
        )
    }

    private fun State.assertWhite(expected: PlayerUiState): State = apply {
        white.should.beEqualTo(expected)
    }

    private fun State.assertBlack(expected: PlayerUiState): State = apply {
        black.should.beEqualTo(expected)
    }

    private fun State.assertGameState(expected: ClockState): State = apply {
        clockState.should.beEqualTo(expected)
    }

    private fun State.assertMoves(expected: List<Long>): State = apply {
        movesTracked.should.beEqualTo(expected)
    }

    //order of execution matters details in Game class
    private fun advanceTimeBy(intervalMillis: Long) {
        millis += intervalMillis
        testDispatcher.scheduler.advanceTimeBy(intervalMillis)
    }

    companion object {
        val testWhite = Player(millis = 60 * 1_000)
        val testBlack = Player(millis = 60 * 1_000)
    }
}
