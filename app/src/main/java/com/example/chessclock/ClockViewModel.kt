package com.example.chessclock

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class Player{
    White, Black
}
data class State(
    val whiteText: String,
    val blackText: String
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
        const val MILLIS_TO_MINUTES_DIFF = 1000 * 60
        const val SAVED_STATE_HANDLE_KEY = "KEY"
    }

    private val initialData = savedStateHandle.get<InitialData>(SAVED_STATE_HANDLE_KEY)!!

    private var currentPlayer: Player = Player.White

    private var whiteMillis = initialData.whiteSeconds * 1000
    private val whiteMinutes
        get() = whiteMillis / MILLIS_TO_MINUTES_DIFF
    private val whiteSeconds
        get() = whiteMillis / 1000 % 60
    private val whiteIncrementMillis = initialData.whiteIncrementSeconds * 1000

    private var blackMillis = initialData.blackSeconds * 1000
    private val blackMinutes
        get() = blackMillis / MILLIS_TO_MINUTES_DIFF
    private val blackSeconds
        get() = blackMillis / 1000 % 60
    private val blackIncrementMillis = initialData.blackIncrementSeconds * 1000

    private val gameOver: Boolean
        get() = whiteMillis == 0 || blackMillis == 0

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

    private fun startTimer() {
        viewModelScope.launch {
            timer.collect {
                if (!gameOver) {
                    when (currentPlayer) {
                        Player.White -> whiteMillis--
                        Player.Black -> blackMillis--
                    }
                }
                _state.value = createState()
            }
        }
    }

    private fun createState(): State {
        return when {
            whiteMillis == 0 -> {
                State(
                    whiteText = "black wins",
                    blackText = "black wins"
                )
            }
            blackMillis == 0 -> {
                State(
                    whiteText = "white wins",
                    blackText = "white wins"
                )
            }
            else -> {
                State(
                    whiteText = "${formatTime(whiteMinutes)}:${formatTime(whiteSeconds)}",
                    blackText = "${formatTime(blackMinutes)}:${formatTime(blackSeconds)}"
                )
            }
        }
    }

    private fun formatTime(time: Int): String =
        if(time < 10) {
            "0$time"
        } else {
            time.toString()
        }

    fun clockClicked(player: Player) {
        if (!timerStarted) {
            timerStarted = true
            currentPlayer = Player.White
            startTimer()
            return
        }

        if (player == currentPlayer) {
            currentPlayer = when (player) {
                Player.White -> Player.Black.also { whiteMillis += whiteIncrementMillis}
                Player.Black -> Player.White.also { blackMillis += blackIncrementMillis }
            }
        }
    }
}