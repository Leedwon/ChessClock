package com.ledwon.jakub.chessclock.feature.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.ui.ColorTheme

class SettingsViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    val themes = listOf(
        ColorTheme.Green,
        ColorTheme.DarkGreen,
        ColorTheme.Blue,
        ColorTheme.DarkBlue,
        ColorTheme.Purple,
        ColorTheme.Red,
        ColorTheme.Orange,
        ColorTheme.Brown,
        ColorTheme.Pink
    )

    val appDarkThemeFlow = settingsRepository.appDarkTheme
    val appColorThemeFlow = settingsRepository.appColorTheme
    val randomizePosition = settingsRepository.randomizePosition

    fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) {
        settingsRepository.updateAppDarkTheme(appDarkTheme)
    }

    fun updateAppColorTheme(appColorThemeType: ColorTheme) {
        settingsRepository.updateAppColorTheme(appColorThemeType)
    }

    fun updateRandomizePosition(randomizePosition: Boolean) {
        settingsRepository.updateRandomizePosition(randomizePosition)
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