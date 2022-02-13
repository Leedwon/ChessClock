package com.ledwon.jakub.chessclock.feature.clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.analytics.AnalyticsEvent
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.clock.model.ClockInitialData
import com.ledwon.jakub.chessclock.feature.clock.model.GameState
import com.ledwon.jakub.chessclock.feature.clock.model.Player
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay
import com.ledwon.jakub.chessclock.feature.clock.util.PauseClock
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take

class ClockViewModel(
    clockInitialData: ClockInitialData,
    private val settingsRepository: SettingsRepository,
    private val pauseClock: PauseClock,
    private val analyticsManager: AnalyticsManager,
    private val positionRandomizer: PositionRandomizer,
) : ViewModel() {

    data class State(
        val playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
        val gameState: GameState
    )

    companion object {
        private const val INTERVAL_MILLIS = 50L
    }

    private val white = Player.White(
        initialMillis = clockInitialData.whiteSeconds * 1000f,
        increment = clockInitialData.whiteIncrementSeconds * 1000
    )
    private val black = Player.Black(
        initialMillis = clockInitialData.blackSeconds * 1000f,
        increment = clockInitialData.blackIncrementSeconds * 1000
    )

    private val movesMillis: MutableList<Long> = mutableListOf()
    private var currentMoveStartTimeMillis: Long? = null

    private var playersInOrder: Pair<Player, Player> = white to black

    private var currentPlayer: Player? = null

    private var gameState: GameState = GameState.RandomizingPositions
        get() =
            if (white.hasLost || black.hasLost) {
                GameState.Over.also { currentPlayer = null }
            } else {
                field
            }

    private val _state: MutableLiveData<State> = MutableLiveData(createState())
    val state: LiveData<State> = _state

    private val _command: MutableLiveData<Command?> = MutableLiveData(null)
    val command: LiveData<Command?> = _command

    val clockDisplay: Flow<ClockDisplay> = settingsRepository.clockDisplay

    val pulsationEnabled: Flow<Boolean> = settingsRepository.pulsationEnabled

    private val gameClock: ReceiveChannel<Unit> = ticker(
        delayMillis = INTERVAL_MILLIS,
        initialDelayMillis = 0,
        context = Dispatchers.Default
    )

    init {
        randomizePositions()
    }

    private var randomizingJob: Job? = null

    private suspend fun isPositionRandomizationEnabled(): Boolean = settingsRepository.randomizePosition.take(1).first()

    private fun randomizePositions() {
        randomizingJob = viewModelScope.launch(Dispatchers.Default) {
            if (isPositionRandomizationEnabled()) {
                positionRandomizer.randomizePositions(white = white, black = black).collect {
                    playersInOrder = it
                    _state.postValue(createState())
                }
            }
            gameState = GameState.BeforeStarted
            _state.postValue(createState())
        }
    }


    fun swapSides() {
        playersInOrder = playersInOrder.second to playersInOrder.first
        _state.postValue(createState())
    }

    fun resumeClock() {
        if (pauseClock.started) {
            pauseClock.stop()
        }
        gameState = GameState.Running
        viewModelScope.launch(Dispatchers.Default) {
            for (tick in gameClock) {
                val executionStartMillis = System.currentTimeMillis()
                val player = currentPlayer
                if (player != null && gameState != GameState.Over && gameState != GameState.Paused) {
                    player.millisLeft -= (INTERVAL_MILLIS + (System.currentTimeMillis() - executionStartMillis))
                } else {
                    cancel()
                }
                _state.postValue(createState())
            }
        }
    }

    fun showStats() {
        analyticsManager.logEvent(AnalyticsEvent.OpenStats)
        _command.value = Command.NavigateToStats(movesMillis)
        _command.value = null
    }

    fun restartGame() {
        white.resetTime()
        black.resetTime()
        gameState = GameState.BeforeStarted.also { currentPlayer = null }
        _state.value = createState()
        movesMillis.clear().also {
            currentMoveStartTimeMillis = null
            pauseClock.restart()
        }
    }

    fun pauseClock() {
        gameState = GameState.Paused
        pauseClock.start()
    }

    fun cancelRandomization() {
        randomizingJob?.cancel()
        gameState = GameState.BeforeStarted.also { currentPlayer = null }
        _state.postValue(createState())
    }

    private fun createState(): State = State(
        playersDisplay = PlayerDisplay.from(
            player = playersInOrder.first,
            isActive = currentPlayer == playersInOrder.first
        ) to PlayerDisplay.from(
            player = playersInOrder.second,
            isActive = currentPlayer == playersInOrder.second
        ),
        gameState = gameState
    )

    fun clockClicked(player: PlayerDisplay) {
        if (gameState == GameState.Paused || gameState == GameState.Over) {
            return
        }

        val now = System.currentTimeMillis()
        currentMoveStartTimeMillis?.let { currentMoveStartTime ->
            val pauseTime = if (pauseClock.canBeConsumed) pauseClock.consume() else 0
            movesMillis.add(now - currentMoveStartTime - pauseTime)
        }
        currentMoveStartTimeMillis = now
        val currPlayer = currentPlayer
        if (gameState == GameState.BeforeStarted && currPlayer == null) {
            if (player.isFor(black)) {
                currentPlayer = white
                resumeClock()
            }
        }


        if (currPlayer != null && player.isFor(currPlayer)) {
            currPlayer.incrementTime()
            currentPlayer = when (player) {
                is PlayerDisplay.White -> black
                is PlayerDisplay.Black -> white
            }
        }
    }

    sealed class Command {
        data class NavigateToStats(val moves: List<Long>) : Command()
    }
}