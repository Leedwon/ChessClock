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

sealed class ChooseClockState {
    data class Loaded(
        val isSelectableModeOn: Boolean,
        val clocksToSelected: Map<Clock, Boolean>
    ) : ChooseClockState()

    object Loading : ChooseClockState()
}

class ChooseClockViewModel(
    private val clockRepository: ClockRepository,
    private val analyticsManager: AnalyticsManager,
    prepopulateDataStore: PrepopulateDataStore
) : ViewModel() {

    private val _chooseClockState: MutableLiveData<ChooseClockState> = MutableLiveData(ChooseClockState.Loading)
    val chooseClockState: LiveData<ChooseClockState> = _chooseClockState

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    init {
        viewModelScope.launch {
            prepopulateDataStore.shouldPrepopulateDatabase
                .collect { shouldPrepopulateDb ->
                    if (shouldPrepopulateDb) {
                        //reversed because while fetching clocks they are sorted desc by id to show user clocks as first
                        clockRepository.addClocks(PredefinedClocks.clocks.reversed())
                        prepopulateDataStore.updateShouldPrepopulateDatabase(false)
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            clockRepository.clocks.collect { clocks ->
                _chooseClockState.postValue(
                    ChooseClockState.Loaded(
                        isSelectableModeOn = _chooseClockState.value?.isSelectableModeOn ?: false,
                        clocksToSelected = clocks.associateWith { false }
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
        _command.value = Command.NavigateToCreateClock.also { analyticsManager.logEvent(AnalyticsEvent.OpenCreateClock) }
        _command.value = null
    }

    fun onRemoveClocks() {
        _chooseClockState.value!!.runIfLoaded { state ->
            viewModelScope.launch(Dispatchers.IO) {
                val clocksToRemove = state.clocksToSelected.filter { it.value }.keys.toList()
                clockRepository.deleteClocks(clocksToRemove).also {
                    clocksToRemove.forEach { analyticsManager.logEvent(AnalyticsEvent.RemoveClock(it)) }
                }
            }
            _chooseClockState.postValue(state.copy(isSelectableModeOn = false))
        }
    }

    fun onOpenSettingsClicked() {
        _command.value = Command.NavigateToSettings.also { analyticsManager.logEvent(AnalyticsEvent.OpenSettings) }
        _command.value = null
    }

    fun onClockLongClicked() {
        _chooseClockState.value!!.runIfLoaded { currentState ->
            val newState = currentState.copy(
                isSelectableModeOn = !currentState.isSelectableModeOn,
                clocksToSelected = if (currentState.isSelectableModeOn) {
                    currentState.clocksToSelected.toMutableMap().mapValues {
                        false
                    }
                } else {
                    currentState.clocksToSelected
                }
            )
            _chooseClockState.postValue(newState)
        }
    }

    fun onSelectClockClick(clock: Clock) {
        _chooseClockState.value!!.runIfLoaded { currentState ->
            val newState = currentState.copy(
                clocksToSelected = currentState.clocksToSelected.toMutableMap().apply {
                    this[clock] = !this[clock]!!
                }
            )
            _chooseClockState.postValue(newState)
        }
    }

    fun onStarClicked(clock: Clock) {
        viewModelScope.launch(Dispatchers.IO) {
            clockRepository.updateClockFavouriteStatus(
                clockId = clock.id,
                isFavourite = !clock.isFavourite
            )
        }
    }

    private fun ChooseClockState.runIfLoaded(block: (ChooseClockState.Loaded) -> Unit) {
        if (this is ChooseClockState.Loaded) block(this)
    }

    private val ChooseClockState.isSelectableModeOn: Boolean
        get() = when (this) {
            is ChooseClockState.Loaded -> this.isSelectableModeOn
            ChooseClockState.Loading -> false
        }

    sealed class Command {
        data class NavigateToClock(val clock: Clock) : Command()
        object NavigateToCreateClock : Command()
        object NavigateToSettings : Command()
    }
}
