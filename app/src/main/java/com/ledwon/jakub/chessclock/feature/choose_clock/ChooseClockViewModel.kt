package com.ledwon.jakub.chessclock.feature.choose_clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledwon.jakub.chessclock.analytics.AnalyticsEvent
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.data.persistance.PrepopulateDataStore
import com.ledwon.jakub.chessclock.data.repository.ClockRepository
import com.ledwon.jakub.chessclock.util.PredefinedClocks
import com.ledwon.jakub.chessclock.util.postUpdate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ChooseClockState(
    val isSelectableModeOn: Boolean,
    val clocksToSelected: Map<Clock, Boolean>
)

class ChooseClockViewModel(
    private val clockRepository: ClockRepository,
    private val analyticsManager: AnalyticsManager,
    prepopulateDataStore: PrepopulateDataStore
) : ViewModel() {

    private val _chooseClockState: MutableLiveData<ChooseClockState> = MutableLiveData()
    val chooseClockState: LiveData<ChooseClockState> = _chooseClockState

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    init {
        viewModelScope.launch {
            prepopulateDataStore.shouldPrepopulateDatabase
                .collect { shouldPrepopulateDb ->
                    if (shouldPrepopulateDb) {
                        //reversed because while fetching timers they are sorted desc by id to show user timers as first
                        clockRepository.addClocks(PredefinedClocks.clocks.reversed())
                        prepopulateDataStore.updateShouldPrepopulateDatabase(false)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            clockRepository.clocks.collect { timers ->
                _chooseClockState.postValue(
                    ChooseClockState(
                        isSelectableModeOn = _chooseClockState.value?.isSelectableModeOn ?: false,
                        clocksToSelected = timers.associateWith { false }
                    )
                )
            }
        }
    }

    fun onClockClicked(clock: Clock) {
        _command.value = Command.NavigateToClock(clock).also { analyticsManager.logEvent(AnalyticsEvent.OpenClockFromChooseClock(clock)) }
        _command.value = null
    }

    fun onCreateClockClicked() {
        _command.value = Command.NavigateToCreateTimer.also { analyticsManager.logEvent(AnalyticsEvent.OpenCreateClock) }
        _command.value = null
    }

    fun onRemoveClocks() {
        val state = _chooseClockState.value!!
        viewModelScope.launch(Dispatchers.IO) {
            val timersToRemove = state.clocksToSelected.filter { it.value }.keys.toList()
            clockRepository.deleteClocks(timersToRemove).also {
                timersToRemove.forEach { analyticsManager.logEvent(AnalyticsEvent.RemoveClock(it)) }
            }
        }
        _chooseClockState.postValue(state.copy(isSelectableModeOn = false))
    }

    fun onOpenSettingsClicked() {
        _command.value = Command.NavigateToSettings.also { analyticsManager.logEvent(AnalyticsEvent.OpenSettings) }
        _command.value = null
    }

    fun onTimerLongClicked() {
        _chooseClockState.postUpdate { currentState ->
            currentState.copy(
                isSelectableModeOn = !currentState.isSelectableModeOn,
                clocksToSelected = if (currentState.isSelectableModeOn) {
                    currentState.clocksToSelected.toMutableMap().mapValues {
                        false
                    }
                } else {
                    currentState.clocksToSelected
                }
            )
        }
    }

    fun onSelectClockClick(clock: Clock) {
        _chooseClockState.postUpdate { currentState ->
            currentState.copy(
                clocksToSelected = currentState.clocksToSelected.toMutableMap().apply {
                    this[clock] = !this[clock]!!
                }
            )
        }
    }

    fun onStarClicked(clock: Clock) {
        viewModelScope.launch(Dispatchers.IO) {
            clockRepository.updateClockFavouriteStatus(
                clockId = clock.id,
                isFavourite = clock.isFavourite
            )
        }
    }

    sealed class Command {
        data class NavigateToClock(val clock: Clock) : Command()
        object NavigateToCreateTimer : Command()
        object NavigateToSettings : Command()
    }
}