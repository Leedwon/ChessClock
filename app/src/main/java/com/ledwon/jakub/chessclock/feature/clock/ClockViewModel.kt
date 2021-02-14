package com.ledwon.jakub.chessclock.feature.clock

import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.ticker
import timber.log.Timber
import java.lang.Math.random

sealed class Player(var millisLeft: Float, private val incrementMillis: Int = 0) {
    private val seconds: Int
        get() = millisLeft.toInt() / 1000 % 60
    private val minutes: Int
        get() = millisLeft.toInt() / 1000 / 60 % 60
    private val hours: Int
        get() = millisLeft.toInt() / 1000 / 60 / 60

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

    class White(millisLeft: Float, increment: Int = 0) : Player(millisLeft, increment) {
        override val text: String
            get() = if (hasLost) "Black wins" else super.text
    }

    class Black(millisLeft: Float, increment: Int = 0) : Player(millisLeft, increment) {
        override val text: String
            get() = if (hasLost) "White wins" else super.text
    }
}

//PlayerDisplay allows for ui changes to happen in 1s intervals, too fast intervals caused ui to freeze
sealed class PlayerDisplay(
    val text: String
) {
    class White(text: String) : PlayerDisplay(text)
    class Black(text: String) : PlayerDisplay(text)

    fun isFor(player: Player): Boolean =
        this is White && player is Player.White || this is Black && player is Player.Black


    companion object {
        fun from(player: Player): PlayerDisplay {
            return when (player) {
                is Player.White -> White(player.text)
                is Player.Black -> Black(player.text)
            }
        }
    }
}

data class State(
    val first: PlayerDisplay,
    val second: PlayerDisplay,
    val gameState: GameState
)

enum class GameState {
    RandomizingPositions,
    BeforeStarted,
    Running,
    Paused,
    Over
}

data class InitialData(
    val whiteSeconds: Int,
    val whiteIncrementSeconds: Int = 0,
    val blackSeconds: Int,
    val blackIncrementSeconds: Int = 0
)

class ClockViewModel(
    private val initialData: InitialData
) : ViewModel() {

    companion object {
        private const val INTERVAL_MILLIS = 100L
        private const val MIN_RANDOM_ROUNDS = 9
    }

    private val white = Player.White(
        millisLeft = initialData.whiteSeconds * 1000f,
        increment = initialData.whiteIncrementSeconds * 1000
    )
    private val black = Player.Black(
        millisLeft = initialData.blackSeconds * 1000f,
        increment = initialData.blackIncrementSeconds * 1000
    )

    private var playersInOrder: Pair<Player, Player> = white to black

    private var currentPlayer: Player = white

    private var gameState: GameState = GameState.RandomizingPositions
        get() =
            if (white.hasLost || black.hasLost) {
                GameState.Over
            } else {
                field
            }

    private val _state: MutableLiveData<State> = MutableLiveData(createState())
    val state: LiveData<State> = _state

    private val timer: ReceiveChannel<Unit> = ticker(
        delayMillis = INTERVAL_MILLIS,
        initialDelayMillis = 0,
        context = viewModelScope.coroutineContext
    )

    init {
        randomizePositions()
    }

    private var randomizingJob: Job? = null

    private fun randomizePositions() {
        val randomRounds = (random() * 100 % 2 + MIN_RANDOM_ROUNDS).toInt()
        var currentRound = 1
        randomizingJob = viewModelScope.launch {
            repeat(randomRounds) {
                delay(250L.takeIf { currentRound++ <= randomRounds - 3 } ?: 75L * currentRound++)
                swapSides()
            }
            gameState = GameState.BeforeStarted
            _state.postValue(createState())
        }
    }


    fun swapSides() {
        playersInOrder = playersInOrder.second to playersInOrder.first
        _state.postValue(createState())
    }

    fun startTimer() {
        gameState = GameState.Running
        viewModelScope.launch {
            for (tick in timer) {
                val currentMillis = System.currentTimeMillis()
                if (gameState != GameState.Over && gameState != GameState.Paused) {
                    currentPlayer.millisLeft -= (INTERVAL_MILLIS + (System.currentTimeMillis() - currentMillis))
                } else {
                    cancel()
                }
                _state.postValue(createState())
            }
        }
    }

    fun restartGame() {
        white.millisLeft = initialData.whiteSeconds * 1000f
        black.millisLeft = initialData.blackSeconds * 1000f
        gameState = GameState.BeforeStarted
        _state.value = createState()
    }

    fun stopTimer() {
        gameState = GameState.Paused
    }

    fun cancelRandomization() {
        randomizingJob?.cancel()
        gameState = GameState.BeforeStarted
        _state.postValue(createState())
    }

    private fun createState(): State = State(
        first = PlayerDisplay.from(playersInOrder.first),
        second = PlayerDisplay.from(playersInOrder.second),
        gameState = gameState
    )

    fun clockClicked(player: PlayerDisplay) {
        if (gameState == GameState.BeforeStarted && player.isFor(black)) {
            currentPlayer = white
            startTimer()
            return
        }

        if (gameState == GameState.Paused) {
            return
        }

        if (player.isFor(currentPlayer)) {
            currentPlayer.incrementTime()
            currentPlayer = when (player) {
                is PlayerDisplay.White -> black
                is PlayerDisplay.Black -> white
            }
        }
    }
}