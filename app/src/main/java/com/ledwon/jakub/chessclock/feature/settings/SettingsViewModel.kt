package com.ledwon.jakub.chessclock.feature.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.clock.ClockType
import com.ledwon.jakub.chessclock.ui.ColorTheme

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

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

    //todo with names
    val clockTypes = listOf(
        ClockType.OwnPlayerTimeClock(180f to 0f),
        ClockType.OwnPlayerTimeClock(90f to 90f),
        ClockType.OwnPlayerTimeClock(270f to 270f),
        ClockType.BothPlayersTimeClock,
        ClockType.CircleAnimatedClock(180f to 0f),
        ClockType.CircleAnimatedClock(90f to 90f),
        ClockType.CircleAnimatedClock(270f to 270f),
    )

    val appDarkThemeFlow = settingsRepository.appDarkTheme
    val appColorThemeFlow = settingsRepository.appColorTheme
    val randomizePosition = settingsRepository.randomizePosition
    val clockType = settingsRepository.clockType

    fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) {
        settingsRepository.updateAppDarkTheme(appDarkTheme)
    }

    fun updateAppColorTheme(appColorThemeType: AppColorThemeType) {
        settingsRepository.updateAppColorTheme(appColorThemeType)
    }

    fun updateRandomizePosition(randomizePosition: Boolean) {
        settingsRepository.updateRandomizePosition(randomizePosition)
    }

    fun updateClockType(clockType: ClockType) {
        settingsRepository.updateClockType(clockType = clockType)
    }

    fun onBackClick() {
        _command.postValue(Command.NavigateBack)
    }

    fun onBuyMeACoffeeClick() {
        _command.value = Command.OpenBuyMeACoffee
        _command.value = null
    }

    fun onRateAppClick() {
        _command.value = Command.RateApp
        _command.value = null
    }

    sealed class Command {
        object NavigateBack : Command()
        object OpenBuyMeACoffee : Command()
        object RateApp : Command()
    }
}