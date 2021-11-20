package com.ledwon.jakub.chessclock.feature.create_clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.analytics.AnalyticsEvent
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.data.repository.ClockRepository
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.util.tryUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateClockState(
    val whiteClock: PlayerTime = PlayerTime(minutes = 5),
    val blackClock: PlayerTime = PlayerTime(minutes = 5)
)

class CreateClockViewModel(
    private val clockRepository: ClockRepository,
    private val analyticsManager: AnalyticsManager
) : ViewModel() {
    private val _state = MutableStateFlow(CreateClockState())
    val state: StateFlow<CreateClockState> = _state

    private val _command = MutableLiveData<Command>(Command.Noop)
    val command: LiveData<Command> = _command

    private val _clocksMerged = MutableStateFlow<Boolean>(true)
    val clocksMerged: StateFlow<Boolean> = _clocksMerged

    fun onClocksMergeClick(clocksMerged: Boolean) {
        _clocksMerged.tryEmit(clocksMerged)
        if (clocksMerged) {
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
                blackClock = if (clocksMerged.value) it.blackClock.copy(hours = value) else it.blackClock
            )
        }
    }

    fun onWhiteMinutesChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(minutes = value),
                blackClock = if (clocksMerged.value) it.blackClock.copy(minutes = value) else it.blackClock
            )
        }
    }

    fun onWhiteSecondsChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(seconds = value),
                blackClock = if (clocksMerged.value) it.blackClock.copy(seconds = value) else it.blackClock
            )
        }
    }

    fun onWhiteIncrementChanged(value: Int) {
        _state.tryUpdate {
            it.copy(
                whiteClock = it.whiteClock.copy(increment = value),
                blackClock = if (clocksMerged.value) it.blackClock.copy(increment = value) else it.blackClock
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

    private fun navigateToClock() {
        _command.value = Command.NavigateToClock(_state.value)
        _command.value = Command.Noop
    }

    fun onStartGameClick() {
        navigateToClock()
        analyticsManager.logEvent(AnalyticsEvent.OpenClockFromCreateClock(clock = buildClockFromState()))
    }

    private fun buildClockFromState(): Clock {
        val state = _state.value
        return Clock(
            whitePlayerTime = state.whiteClock,
            blackPlayerTime = state.blackClock,
            isFavourite = false
        )
    }

    fun onSaveClockClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val clock = buildClockFromState()
            clockRepository.addClock(clock)
            analyticsManager.logEvent(AnalyticsEvent.AddClock(clock))

            _command.postValue(Command.NavigateBack)
        }
    }

    fun onStartGameAndSaveClockClick() {
        val clock = buildClockFromState()
        viewModelScope.launch(Dispatchers.IO) {
            clockRepository.addClock(clock)
            analyticsManager.logEvent(AnalyticsEvent.OpenAndAddClock(clock))
        }
        navigateToClock()
    }

    fun onBackClick() {
        _command.value = Command.NavigateBack
    }

    sealed class Command {
        data class NavigateToClock(val state: CreateClockState) : Command()
        object NavigateBack : Command()
        object Noop : Command()
    }
}