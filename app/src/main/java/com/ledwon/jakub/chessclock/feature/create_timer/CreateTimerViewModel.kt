package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.model.*
import com.ledwon.jakub.chessclock.util.tryUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class State(
    val whiteClock: ClockTime = ClockTime(minutes = 5),
    val blackClock: ClockTime = ClockTime(minutes = 5)
)

class CreateTimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _command = MutableLiveData<Command>(Command.Noop)
    val command: LiveData<Command> = _command

    fun onWhiteHoursChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(hours = value)
            )
        }
    }

    fun onWhiteMinutesChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(minutes = value)
            )
        }
    }

    fun onWhiteSecondsChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(seconds = value)
            )
        }
    }

    fun onWhiteIncrementChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(increment = value)
            )
        }
    }

    fun onBlackHoursChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                blackClock = it.blackClock.copy(hours = value)
            )
        }
    }

    fun onBlackMinutesChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                blackClock = it.blackClock.copy(minutes = value)
            )
        }
    }

    fun onBlackSecondsChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                blackClock = it.blackClock.copy(seconds = value)
            )
        }
    }

    fun onBlackIncrementChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                blackClock = it.blackClock.copy(increment = value)
            )
        }
    }

    fun startGame() {
        _command.value = Command.NavigateToClock(_state.value)
        _command.value = Command.Noop
    }

    sealed class Command {
        data class NavigateToClock(val state: State) : Command()
        object Noop : Command()
    }
}