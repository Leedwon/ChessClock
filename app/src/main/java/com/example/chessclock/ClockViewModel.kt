package com.example.chessclock

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat

enum class Player {
    White, Black
}

data class State(
    val whiteText: String,
    val blackText: String
)

class ClockViewModel @ViewModelInject constructor(): ViewModel() {

    companion object {
        const val MILLIS_TO_MINUTES_DIFF = 1000*60
    }

    private var currentPlayer: Player = Player.White

    private var whiteMillis = 600000
    private val whiteMinutes
    get() = whiteMillis / MILLIS_TO_MINUTES_DIFF
    private val whiteSeconds
    get() = whiteMillis / 1000 % 60
    private var blackMillis = 600000
    private val blackMinutes
    get() = blackMillis / MILLIS_TO_MINUTES_DIFF
    private val blackSeconds
    get() = blackMillis / 1000 % 60
    private val gameOver : Boolean
    get() = whiteMillis == 0 || blackMillis == 0

    private var timerStarted = false

    private val _state : MutableLiveData<State> = MutableLiveData(createState())
    val state : LiveData<State> = _state

    private val timer: Flow<Unit> = flow {
        emit(Unit)
        while(!gameOver) {
            delay(1)
            emit(Unit)
        }
    }.flowOn(Dispatchers.Default)

    private fun startTimer() {
        viewModelScope.launch {
            timer.collect {
                if(!gameOver) {
                    when (currentPlayer) {
                        Player.White -> whiteMillis--
                        Player.Black -> blackMillis--
                    }
                }
                _state.value = createState()
            }
        }
    }

    private fun createState() : State {
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
                    whiteText = "${createMinutesText(whiteMinutes)}:${createSecondsText(whiteSeconds)}",
                    blackText = "${createMinutesText(blackMinutes)}:${createSecondsText(blackSeconds)}"
                )
            }
        }
    }

    private fun createMinutesText(minutes: Int) : String =
        if(minutes.toString().length == 1) {
            "0$minutes"
        } else {
            minutes.toString()
        }

    private fun createSecondsText(seconds: Int) : String =
        if(seconds.toString().length == 1) {
            "${seconds}0"
        } else {
            seconds.toString()
        }

    fun clockClicked(player: Player) {
        if(!timerStarted) {
            timerStarted = true
            currentPlayer = Player.White
            startTimer()
            return
        }

        if(player == currentPlayer) {
            currentPlayer = when(player) {
                Player.White -> Player.Black
                Player.Black -> Player.White
            }
        }
    }
}