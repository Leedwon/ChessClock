package com.ledwon.jakub.chessclock.feature.choose_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.data.model.Timer
import com.ledwon.jakub.chessclock.data.persistance.PrepopulateDataStore
import com.ledwon.jakub.chessclock.data.repository.TimerRepository
import com.ledwon.jakub.chessclock.util.PredefinedTimers
import com.ledwon.jakub.chessclock.util.postUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ChooseTimerState(
    val isSelectableModeOn: Boolean,
    val timersToSelected: Map<Timer, Boolean>
)

class ChooseTimerViewModel(
    private val timerRepository: TimerRepository,
    prepopulateDataStore: PrepopulateDataStore
) : ViewModel() {

    private val _chooseTimerState: MutableLiveData<ChooseTimerState> = MutableLiveData()
    val chooseTimerState: LiveData<ChooseTimerState> = _chooseTimerState

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    init {
        if (prepopulateDataStore.shouldPrepopulateDatabase) {
            viewModelScope.launch(Dispatchers.IO) {
                //reversed because while fetching timers they are sorted desc by id to show user timers as first
                timerRepository.addTimers(PredefinedTimers.timers.reversed())
            }
            prepopulateDataStore.shouldPrepopulateDatabase = false
        }

        viewModelScope.launch(Dispatchers.IO) {
            timerRepository.timers.collect { timers ->
                _chooseTimerState.postValue(
                    ChooseTimerState(
                        isSelectableModeOn = _chooseTimerState.value?.isSelectableModeOn ?: false,
                        timersToSelected = timers.associateWith { false }
                    )
                )
            }
        }
    }

    fun onTimerClicked(timer: Timer) {
        _command.value = Command.NavigateToClock(timer)
        _command.value = null
    }

    fun onCreateTimerClicked() {
        _command.value = Command.NavigateToCreateTimer
        _command.value = null
    }

    fun onRemoveTimers() {
        val state = _chooseTimerState.value!!
        viewModelScope.launch(Dispatchers.IO) {
            timerRepository.deleteTimers(state.timersToSelected.filter { it.value }.keys.toList())
        }
        _chooseTimerState.postValue(state.copy(isSelectableModeOn = false))
    }

    fun onOpenSettingsClicked() {
        _command.value = Command.NavigateToSettings
        _command.value = null
    }

    fun onTimerLongClicked() {
        _chooseTimerState.postUpdate { currentState ->
            currentState.copy(
                isSelectableModeOn = !currentState.isSelectableModeOn,
                timersToSelected = if (currentState.isSelectableModeOn) {
                    currentState.timersToSelected.toMutableMap().mapValues {
                        false
                    }
                } else {
                    currentState.timersToSelected
                }
            )
        }
    }

    fun onSelectTimerClick(timer: Timer) {
        _chooseTimerState.postUpdate { currentState ->
            currentState.copy(
                timersToSelected = currentState.timersToSelected.toMutableMap().apply {
                    this[timer] = !this[timer]!!
                }
            )
        }
    }

    fun onStarClicked(timer: Timer) {
        viewModelScope.launch(Dispatchers.IO) {
            timerRepository.updateFavouriteStatus(
                timer.copy(isFavourite = !timer.isFavourite)
            )
        }
    }

    sealed class Command {
        data class NavigateToClock(val timer: Timer) : Command()
        object NavigateToCreateTimer : Command()
        object NavigateToSettings : Command()
    }
}