package com.ledwon.jakub.chessclock.feature.clock_preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.data.repository.ClockTypesRepository
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class ClockPreviewViewModel(
    private val clockId: String,
    clockTypesRepository: ClockTypesRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    val pulsationEnabled: Flow<Boolean> = settingsRepository.pulsationEnabled

    val clockType: ClockTypesRepository.NamedClockDisplayType? =
        clockTypesRepository.clockTypes.firstOrNull { it.id == clockId }.also {
            if (it == null) {
                _command.value = Command.NavigateBack
            }
        }

    fun onBackClick() {
        _command.value = Command.NavigateBack
    }

    sealed class Command {
        object NavigateBack : Command()
    }
}