package com.ledwon.jakub.chessclock.feature.clock

import androidx.annotation.FloatRange
import androidx.lifecycle.*
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import java.lang.Math.random

sealed class Player(
    private val initialMillis: Float,
    private val incrementMillis: Int = 0
) {
    var millisLeft: Float = initialMillis

    private val seconds: Int
        get() = millisLeft.toInt() / 1000 % 60
    private val minutes: Int
        get() = millisLeft.toInt() / 1000 / 60 % 60
    private val hours: Int
        get() = millisLeft.toInt() / 1000 / 60 / 60

    val percentageLeft: Float
        get() = millisLeft / initialMillis

    open val text: String
        get() {
            val hoursText = if (hours > 0) {
                "${formatTime(hours)}:"
            } else {
                ""
            }
            return "$hoursText${formatTime(minutes)}:${formatTime(seconds)}"
        }


    fun incrementTime() {
        millisLeft += incrementMillis
    }

    fun resetTime() {
        millisLeft = initialMillis
    }

    val hasLost: Boolean
        get() = millisLeft <= 0

    private fun formatTime(time: Int): String =
        if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }

    override fun toString(): String {
        return "Player(millisLeft=$millisLeft, increment=$incrementMillis)"
    }

    class White(initialMillis: Float, increment: Int = 0) : Player(initialMillis, increment) {
        override val text: String
            get() = if (hasLost) "Black wins" else super.text
    }

    class Black(initialMillis: Float, increment: Int = 0) : Player(initialMillis, increment) {
        override val text: String
            get() = if (hasLost) "White wins" else super.text
    }
}

sealed class PlayerDisplay(
    val text: String,
    @FloatRange(from = 0.0, to = 1.0)
    val percentageLeft: Float,
    val isActive: Boolean
) {
    class White(
        text: String,
        percentageLeft: Float,
        isActive: Boolean
    ) : PlayerDisplay(text, percentageLeft, isActive)

    class Black(
        text: String,
        percentageLeft: Float,
        isActive: Boolean
    ) : PlayerDisplay(text, percentageLeft, isActive)

    fun isFor(player: Player): Boolean =
        this is White && player is Player.White || this is Black && player is Player.Black


    companion object {
        fun from(player: Player, isActive: Boolean): PlayerDisplay {
            return when (player) {
                is Player.White -> White(
                    player.text,
                    player.percentageLeft,
                    isActive
                )
                is Player.Black -> Black(
                    player.text,
                    player.percentageLeft,
                    isActive
                )
            }
        }
    }
}

data class State(
    val playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    val gameState: GameState
)

enum class GameState {
    RandomizingPositions,
    BeforeStarted,
    Running,
    Paused,
    Over
}

data class ClockInitialData(
    val whiteSeconds: Int,
    val whiteIncrementSeconds: Int = 0,
    val blackSeconds: Int,
    val blackIncrementSeconds: Int = 0
)

class ClockViewModel(
    clockInitialData: ClockInitialData,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    companion object {
        private const val INTERVAL_MILLIS = 50L
        private const val MIN_RANDOM_ROUNDS = 9
    }

    private val white = Player.White(
        initialMillis = clockInitialData.whiteSeconds * 1000f,
        increment = clockInitialData.whiteIncrementSeconds * 1000
    )
    private val black = Player.Black(
        initialMillis = clockInitialData.blackSeconds * 1000f,
        increment = clockInitialData.blackIncrementSeconds * 1000
    )

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

    val clockDisplay: StateFlow<ClockDisplay> = settingsRepository.clockDisplay

    val pulsationEnabled: StateFlow<Boolean> = settingsRepository.pulsationEnabled

    private val timer: ReceiveChannel<Unit> = ticker(
        delayMillis = INTERVAL_MILLIS,
        initialDelayMillis = 0,
        context = Dispatchers.Default
    )

    init {
        randomizePositions()
    }

    private var randomizingJob: Job? = null

    private fun randomizePositions() {
        randomizingJob = viewModelScope.launch(Dispatchers.Default) {
            settingsRepository.randomizePosition.take(1).collect { randomizeEnabled ->
                if (randomizeEnabled) {
                    val randomRounds = (random() * 100 % 2 + MIN_RANDOM_ROUNDS).toInt()
                    var currentRound = 1

                    repeat(randomRounds) {
                        delay(250L.takeIf { currentRound++ <= randomRounds - 3 }
                            ?: 75L * currentRound++)
                        swapSides()
                    }
                    gameState = GameState.BeforeStarted
                    _state.postValue(createState())
                } else {
                    gameState = GameState.BeforeStarted
                    _state.postValue(createState())
                }

            }
        }
    }


    fun swapSides() {
        playersInOrder = playersInOrder.second to playersInOrder.first
        _state.postValue(createState())
    }

    fun startTimer() {
        gameState = GameState.Running
        viewModelScope.launch(Dispatchers.Default) {
            for (tick in timer) {
                val currentMillis = System.currentTimeMillis()
                val player = currentPlayer
                if (player != null && gameState != GameState.Over && gameState != GameState.Paused) {
                    player.millisLeft -= (INTERVAL_MILLIS + (System.currentTimeMillis() - currentMillis))
                } else {
                    cancel()
                }
                _state.postValue(createState())
            }
        }
    }

    fun restartGame() {
        white.resetTime()
        black.resetTime()
        gameState = GameState.BeforeStarted.also { currentPlayer = null }
        _state.value = createState()
    }

    fun stopTimer() {
        gameState = GameState.Paused
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

        val nonMutableCurrentPlayer = currentPlayer
        if (gameState == GameState.BeforeStarted && nonMutableCurrentPlayer == null) {
            if (player.isFor(black)) {
                currentPlayer = white
                startTimer()
            }
        }


        if (nonMutableCurrentPlayer != null && player.isFor(nonMutableCurrentPlayer)) {
            nonMutableCurrentPlayer.incrementTime()
            currentPlayer = when (player) {
                is PlayerDisplay.White -> black
                is PlayerDisplay.Black -> white
            }
        }
    }
}