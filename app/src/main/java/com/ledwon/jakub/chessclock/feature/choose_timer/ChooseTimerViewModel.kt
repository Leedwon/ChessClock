package com.ledwon.jakub.chessclock.feature.choose_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.data.model.Timer
import com.ledwon.jakub.chessclock.data.persistance.PrepopulateDataStore
import com.ledwon.jakub.chessclock.data.repository.TimerRepository
import com.ledwon.jakub.chessclock.util.PredefinedTimers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChooseTimerViewModel(
    private val timerRepository: TimerRepository,
    prepopulateDataStore: PrepopulateDataStore
) : ViewModel() {

    private val _timers: MutableLiveData<List<Timer>> = MutableLiveData()
    val timers: LiveData<List<Timer>> = _timers

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
            timerRepository.timers.collect {
                _timers.postValue(it)
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

    fun onRemoveTimer(timer: Timer) {
        viewModelScope.launch(Dispatchers.IO) {
            timerRepository.deleteTimer(timer)
        }
    }

    fun onOpenSettingsClicked() {
        _command.value = Command.NavigateToSettings
        _command.value = null
    }

    sealed class Command {
        data class NavigateToClock(val timer: Timer) : Command()
        object NavigateToCreateTimer : Command()
        object NavigateToSettings : Command()
    }
}