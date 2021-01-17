package com.example.chessclock

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Player(var millisLeft: Int, private val increment: Int = 0) {
    private val seconds: Int
        get() = millisLeft / 1000 % 60
    private val minutes: Int
        get() = millisLeft / 1000 / 60
    private val hours: Int
        get() = millisLeft / 1000 / 60 / 60

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
        millisLeft += increment
    }

    val hasLost: Boolean
        get() = millisLeft == 0

    private fun formatTime(time: Int): String =
        if (time < 10) {
            "0$time"
        } else {
            time.toString()
        }

    override fun toString(): String {
        return "Player(millisLeft=$millisLeft, increment=$increment)"
    }

    class White(millisLeft: Int, increment: Int = 0) : Player(millisLeft, increment) {
        override val text: String
            get() = if (hasLost) "Black wins" else super.text
    }

    class Black(millisLeft: Int, increment: Int = 0) : Player(millisLeft, increment) {
        override val text: String
            get() = if (hasLost) "White wins" else super.text
    }
}

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
    val gameStarted: Boolean
)

data class InitialData(
    val whiteSeconds: Int,
    val whiteIncrementSeconds: Int = 0,
    val blackSeconds: Int,
    val blackIncrementSeconds: Int = 0
)

class ClockViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val SAVED_STATE_HANDLE_KEY = "KEY"
    }

    private val initialData = savedStateHandle.get<InitialData>(SAVED_STATE_HANDLE_KEY)!!

    private val white = Player.White(
        millisLeft = initialData.whiteSeconds * 1000,
        increment = initialData.whiteIncrementSeconds
    )
    private val black = Player.Black(
        millisLeft = initialData.blackSeconds * 1000,
        increment = initialData.blackIncrementSeconds
    )

    private var playersInOrder: Pair<Player, Player> = white to black

    private var currentPlayer: Player = white

    private val gameOver: Boolean
        get() = white.hasLost || black.hasLost

    private var timerStarted = false

    private val _state: MutableLiveData<State> = MutableLiveData(createState())
    val state: LiveData<State> = _state

    private val timer: Flow<Unit> = flow {
        emit(Unit)
        while (!gameOver) {
            delay(1)
            emit(Unit)
        }
    }.flowOn(Dispatchers.Default)

    fun swapSides() {
        playersInOrder = playersInOrder.second to playersInOrder.first
        _state.value = createState()
    }

    private fun startTimer() {
        viewModelScope.launch {
            timer.collect {
                if (!gameOver) {
                    currentPlayer.millisLeft--
                } else {
                    cancel("game is over")
                }
                _state.value = createState()
            }
        }
    }

    private fun createState(): State = State(
        first = PlayerDisplay.from(playersInOrder.first),
        second = PlayerDisplay.from(playersInOrder.second),
        gameStarted = timerStarted
    )

    fun clockClicked(player: PlayerDisplay) {
        if (!timerStarted && player.isFor(black)) {
            timerStarted = true
            currentPlayer = white
            startTimer()
            return
        }

        if (player.isFor(currentPlayer)) {
            currentPlayer = when (player) {
                is PlayerDisplay.White -> black.also { black.incrementTime() }
                is PlayerDisplay.Black -> white.also { white.incrementTime() }
            }
        }
    }
}