package com.ledwon.jakub.chessclock.feature.clock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.analytics.AnalyticsEvent
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.clock.model.*
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ClockViewModel(
    private val settingsRepository: SettingsRepository,
    private val analyticsManager: AnalyticsManager,
    private val positionRandomizer: PositionRandomizer,
    private val game: Game
) : ViewModel() {

    data class State(
        val playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
        val clockState: ClockState
    )

    private val playersInOrder = MutableStateFlow(PlayerColor.White to PlayerColor.Black)
    private val randomizing = MutableStateFlow(false)

    val state: Flow<State> = combine(
        game.state.distinctUntilChangedBy { listOf(it.white, it.black, it.clockState) },
        playersInOrder,
        randomizing,
    ) { gameState, playersInOrder, randomizing ->
        createVmState(gameState, playersInOrder, randomizing)
    }

    private val _command: MutableSharedFlow<Command> = MutableSharedFlow()
    val command: Flow<Command> = _command

    val clockDisplay: Flow<ClockDisplay> = settingsRepository.clockDisplay
    val pulsationEnabled: Flow<Boolean> = settingsRepository.pulsationEnabled

    init {
        randomizePositions()
    }

    private var randomizingJob: Job? = null

    override fun onCleared() {
        game.clear()
        super.onCleared()
    }

    fun swapSides() {
        playersInOrder.update { it.second to it.first }
    }

    fun resumeClock() {
        game.resume()
    }

    fun showStats() {
        analyticsManager.logEvent(AnalyticsEvent.OpenStats)
        viewModelScope.launch {
            val moves = game.state.first().movesTracked
            _command.emit(Command.NavigateToStats(moves))
        }
    }

    fun restartGame() {
        game.restart()
    }

    fun pauseClock() {
        game.pause()
    }

    fun cancelRandomization() {
        randomizingJob?.cancel()
    }

    fun clockClicked(player: PlayerDisplay) {
        viewModelScope.launch {
            if (state.first().clockState == ClockState.Over) return@launch

            val activePlayerColor = findActivePlayerColorOrNull()

            //we can assume that this happens only before game starts
            if (activePlayerColor == null) {
                game.start()
            } else if (player.isFor(activePlayerColor)) {
                game.playerMoveFinished()
            }
        }
    }

    private fun PlayerDisplay.isFor(playerColor: PlayerColor) =
        when (this) {
            is PlayerDisplay.Black -> playerColor == PlayerColor.Black
            is PlayerDisplay.White -> playerColor == PlayerColor.White
        }

    private suspend fun findActivePlayerColorOrNull(): PlayerColor? {
        val state = game.state.first()
        return when {
            state.white.isActive -> PlayerColor.White
            state.black.isActive -> PlayerColor.Black
            else -> null
        }
    }

    private suspend fun isPositionRandomizationEnabled(): Boolean = settingsRepository.randomizePosition.take(1).first()

    private fun randomizePositions() {
        randomizingJob = viewModelScope.launch(Dispatchers.Default) {
            if (isPositionRandomizationEnabled()) {
                randomizing.value = true
                positionRandomizer.randomizePositions().collect { playersInOrder.value = it }
            }
        }.also {
            it.invokeOnCompletion {
                randomizing.value = false
            }
        }
    }

    private fun createVmState(gameState: Game.State, playersInOrder: Pair<PlayerColor, PlayerColor>, randomizing: Boolean): State {
        val white = PlayerDisplay.White(
            text = gameState.white.text,
            percentageLeft = gameState.white.percentageLeft,
            isActive = gameState.white.isActive,
        )

        val black = PlayerDisplay.Black(
            text = gameState.black.text,
            percentageLeft = gameState.black.percentageLeft,
            isActive = gameState.black.isActive
        )

        return State(
            playersDisplay = if (playersInOrder == PlayerColor.White to PlayerColor.Black) white to black else black to white,
            clockState = if (randomizing) ClockState.RandomizingPositions else gameState.clockState
        )
    }

    sealed class Command {
        data class NavigateToStats(val moves: List<Long>) : Command()
    }
}
