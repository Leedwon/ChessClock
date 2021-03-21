package com.ledwon.jakub.chessclock.feature.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.data.repository.ClockTypesRepository
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    clockTypesRepository: ClockTypesRepository
) : ViewModel() {

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    val themes = listOf(
        AppColorThemeType.Green,
        AppColorThemeType.DarkGreen,
        AppColorThemeType.Blue,
        AppColorThemeType.DarkBlue,
        AppColorThemeType.Purple,
        AppColorThemeType.Red,
        AppColorThemeType.Orange,
        AppColorThemeType.Brown,
        AppColorThemeType.Pink
    )

    val clockTypes = clockTypesRepository.clockTypes

    val appDarkTheme: StateFlow<AppDarkTheme> = settingsRepository.appDarkTheme
    val appColorTheme: StateFlow<AppColorThemeType> = settingsRepository.appColorTheme
    val randomizePosition: StateFlow<Boolean> = settingsRepository.randomizePosition
    val clockType: StateFlow<ClockDisplay> = settingsRepository.clockDisplay
    val pulsationEnabled: StateFlow<Boolean> = settingsRepository.pulsationEnabled

    fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) = settingsRepository.updateAppDarkTheme(appDarkTheme)

    fun updateAppColorTheme(appColorThemeType: AppColorThemeType) = settingsRepository.updateAppColorTheme(appColorThemeType)

    fun updateRandomizePosition(randomizePosition: Boolean) = settingsRepository.updateRandomizePosition(randomizePosition)

    fun updateClockType(namedClockDisplay: ClockTypesRepository.NamedClockDisplayType) =
        settingsRepository.updateClockType(clockDisplay = namedClockDisplay.display)

    fun updatePulsationEnabled(pulsationEnabled: Boolean) = settingsRepository.updatePulsationEnabled(pulsationEnabled)

    fun onClockTypePreviewClick(namedClockDisplay: ClockTypesRepository.NamedClockDisplayType) {
        _command.value = Command.OpenClockPreview(namedClockDisplay.name)
        _command.value = Command.Noop
    }

    fun onBackClick() = _command.postValue(Command.NavigateBack)

    fun onBuyMeACoffeeClick() {
        _command.value = Command.OpenBuyMeACoffee
        _command.value = Command.Noop
    }

    fun onRateAppClick() {
        _command.value = Command.RateApp
        _command.value = Command.Noop
    }

    sealed class Command {
        object NavigateBack : Command()
        object OpenBuyMeACoffee : Command()
        object RateApp : Command()
        data class OpenClockPreview(val clockDisplayTypeName: String) : Command()
        object Noop : Command()
    }
}