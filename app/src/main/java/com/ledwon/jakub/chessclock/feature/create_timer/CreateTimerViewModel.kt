package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.data.model.*
import com.ledwon.jakub.chessclock.data.repository.TimerRepository
import com.ledwon.jakub.chessclock.util.TimerNameProvider
import com.ledwon.jakub.chessclock.util.tryUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class State(
    val whiteClock: ClockTime = ClockTime(minutes = 5),
    val blackClock: ClockTime = ClockTime(minutes = 5)
)

class CreateTimerViewModel(
    private val timerRepository: TimerRepository
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val _command = MutableLiveData<Command>(Command.Noop)
    val command: LiveData<Command> = _command

    private val _timersMerged = MutableStateFlow<Boolean>(true)
    val timersMerged: StateFlow<Boolean> = _timersMerged

    fun onTimersMergeClick(timersMerged: Boolean) {
        _timersMerged.tryEmit(timersMerged)
        if (timersMerged) {
            _state.tryUpdate {
                it.copy(
                    blackClock = it.whiteClock
                )
            }
        }
    }

    fun onWhiteHoursChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(hours = value),
                blackClock = if (timersMerged.value) it.blackClock.copy(hours = value) else it.blackClock
            )
        }
    }

    fun onWhiteMinutesChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(minutes = value),
                blackClock = if (timersMerged.value) it.blackClock.copy(minutes = value) else it.blackClock
            )
        }
    }

    fun onWhiteSecondsChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(seconds = value),
                blackClock = if (timersMerged.value) it.blackClock.copy(seconds = value) else it.blackClock
            )
        }
    }

    fun onWhiteIncrementChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(increment = value),
                blackClock = if (timersMerged.value) it.blackClock.copy(increment = value) else it.blackClock
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

    fun onStartGameClick() {
        _command.value = Command.NavigateToClock(_state.value)
        _command.value = Command.Noop
    }

    private suspend fun saveTimer() {
        val state = _state.value
        timerRepository.addTimer(
            Timer(
                description = TimerNameProvider.createTimerName(
                    whiteClockTime = state.whiteClock,
                    blackClockTime = state.blackClock
                ),
                whiteClockTime = state.whiteClock,
                blackClockTime = state.blackClock,
                isFavourite = false
            )
        )
    }

    fun onSaveTimerClick() {
        viewModelScope.launch(Dispatchers.IO) {
            saveTimer()
            _command.postValue(Command.NavigateBack)
        }
    }

    fun onStartGameAndSaveTimerClick() {
        viewModelScope.launch(Dispatchers.IO) {
            saveTimer()
        }
        onStartGameClick()
    }

    fun onBackClick() {
        _command.value = Command.NavigateBack
    }

    sealed class Command {
        data class NavigateToClock(val state: State) : Command()
        object NavigateBack : Command()
        object Noop : Command()
    }
}