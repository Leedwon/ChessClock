package com.ledwon.jakub.chessclock.feature.clock

import com.ledwon.jakub.chessclock.feature.clock.GameUtil.percentageLeft
import com.ledwon.jakub.chessclock.feature.clock.model.ClockState
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor
import com.ledwon.jakub.chessclock.util.DeferrableString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*

data class Player(
    val millis: Long,
    val incrementMillis: Long = 0L
)

class Game(
    private val white: Player,
    private val black: Player,
    private val movesTracker: MovesTracker,
    defaultDispatcher: CoroutineDispatcher,
    intervalMillis: Long = 50,
    currentTimeMillisProvider: () -> Long = { System.currentTimeMillis() }
) {

    private val currentPlayerColorFlow = MutableStateFlow(PlayerColor.White)
    private val gameStateFlow = MutableStateFlow(InternalGameState.BeforeStarted)

    private val currentPlayerClock
        get() = when (currentPlayerColorFlow.value) {
            PlayerColor.White -> whiteClock
            PlayerColor.Black -> blackClock
        }

    private val whiteClock = ChessClock(
        initialMillis = white.millis,
        incrementMillis = white.incrementMillis,
        defaultDispatcher = defaultDispatcher,
        intervalMillis = intervalMillis,
        currentTimeMillisProvider = currentTimeMillisProvider,
    )

    private val blackClock = ChessClock(
        initialMillis = black.millis,
        incrementMillis = black.incrementMillis,
        defaultDispatcher = defaultDispatcher,
        intervalMillis = intervalMillis,
        currentTimeMillisProvider = currentTimeMillisProvider,
    )

    val state: Flow<State> = combine(
        whiteClock.millisLeft,
        blackClock.millisLeft,
        currentPlayerColorFlow,
        gameStateFlow,
        movesTracker.movesMillis
    ) { whiteMillis, blackMillis, currentPlayerColor, gameState, movesTracked ->
        val gameOver = whiteMillis <= 0 || blackMillis <= 0
        State(
            white = PlayerUiState(
                text = GameUtil.textFromMillis(millisLeft = whiteMillis, PlayerColor.White),
                percentageLeft = white.percentageLeft(whiteMillis),
                isActive = gameState != InternalGameState.BeforeStarted && currentPlayerColor == PlayerColor.White
            ),
            black = PlayerUiState(
                text = GameUtil.textFromMillis(millisLeft = blackMillis, PlayerColor.Black),
                percentageLeft = black.percentageLeft(blackMillis),
                isActive = gameState != InternalGameState.BeforeStarted && currentPlayerColor == PlayerColor.Black
            ),
            clockState = if (gameOver) ClockState.Over else gameState.toClockState(),
            movesTracked = movesTracked,
        )
    }.distinctUntilChanged()

    fun playerMoveFinished() {
        check(gameStateFlow.value == InternalGameState.Running) { "Can't finish player's move when game is not running" }
        stopPlayerMove()
        switchPlayers()
        startPlayerMove()
    }

    fun start() {
        check(gameStateFlow.value == InternalGameState.BeforeStarted) { "Can't call start() when game is running" }
        currentPlayerClock.start()
        movesTracker.moveStarted()
        gameStateFlow.update { InternalGameState.Running }
    }

    fun pause() {
        check(gameStateFlow.value == InternalGameState.Running) { "Can't call pause if game is not running" }
        currentPlayerClock.stop()
        movesTracker.movePaused()
        gameStateFlow.update { InternalGameState.Paused }
    }

    fun resume() {
        check(gameStateFlow.value == InternalGameState.Paused) { "Can't call resume if game is not paused" }
        currentPlayerClock.start()
        movesTracker.moveResumed()
        gameStateFlow.update { InternalGameState.Running }
    }

    fun restart() {
        gameStateFlow.value = InternalGameState.BeforeStarted
        currentPlayerColorFlow.value = PlayerColor.White
        whiteClock.restart()
        blackClock.restart()
        movesTracker.restart()
    }

    fun clear() {
        whiteClock.stop()
        blackClock.stop()
    }

    private fun startPlayerMove() {
        currentPlayerClock.start()
        movesTracker.moveStarted()
    }

    private fun stopPlayerMove() {
        currentPlayerClock.stopAndIncrement()
        movesTracker.moveEnded()
    }

    private fun switchPlayers() {
        currentPlayerColorFlow.update { it.opposite() }
    }

    private fun ChessClock.stopAndIncrement() {
        stop()
        increment()
    }

    data class State(
        val white: PlayerUiState,
        val black: PlayerUiState,
        val clockState: ClockState,
        val movesTracked: List<Long>,
    )

    data class PlayerUiState(
        val text: DeferrableString,
        val percentageLeft: Float,
        val isActive: Boolean,
    )
    
    private enum class InternalGameState {
        BeforeStarted,
        Running,
        Paused,
    }

    private fun InternalGameState.toClockState() = when(this) {
        InternalGameState.BeforeStarted -> ClockState.BeforeStarted
        InternalGameState.Running -> ClockState.Running
        InternalGameState.Paused -> ClockState.Paused
    }
}
