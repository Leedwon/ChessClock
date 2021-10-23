package com.ledwon.jakub.chessclock.feature.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.analytics.AnalyticsEvent
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.data.repository.ClockTypesRepository
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val analyticsManager: AnalyticsManager,
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

    val appDarkTheme: Flow<AppDarkTheme> = settingsRepository.appDarkTheme
    val appColorTheme: Flow<AppColorThemeType> = settingsRepository.appColorTheme
    val randomizePosition: Flow<Boolean> = settingsRepository.randomizePosition
    val clockType: Flow<ClockDisplay> = settingsRepository.clockDisplay
    val pulsationEnabled: Flow<Boolean> = settingsRepository.pulsationEnabled

    fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) = settingsRepository.updateAppDarkTheme(appDarkTheme)

    fun updateAppColorTheme(appColorThemeType: AppColorThemeType) = settingsRepository.updateAppColorTheme(appColorThemeType)
        .also { analyticsManager.logEvent(AnalyticsEvent.UpdateAppColorTheme(appColorThemeType)) }

    fun updateRandomizePosition(randomizePosition: Boolean) = settingsRepository.updateRandomizePosition(randomizePosition)
        .also { analyticsManager.logEvent(AnalyticsEvent.UpdateRandomizePlayersPositions(randomizePosition)) }

    fun updateClockType(namedClockDisplay: ClockTypesRepository.NamedClockDisplayType) =
        settingsRepository.updateClockType(clockDisplay = namedClockDisplay.display)
            .also { analyticsManager.logEvent(AnalyticsEvent.UpdateClockType(namedClockDisplay)) }

    fun updatePulsationEnabled(pulsationEnabled: Boolean) = settingsRepository.updatePulsationEnabled(pulsationEnabled)
        .also { analyticsManager.logEvent(AnalyticsEvent.UpdatePulsationEnabled(pulsationEnabled)) }

    fun onClockTypePreviewClick(namedClockDisplay: ClockTypesRepository.NamedClockDisplayType) {
        _command.value = Command.OpenClockPreview(namedClockDisplay.id)
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
        data class OpenClockPreview(val clockDisplayTypeId: String) : Command()
        object Noop : Command()
    }
}