package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.data.persistance.SettingsDataStore
import com.ledwon.jakub.chessclock.ui.ColorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppDarkTheme {
    Light,
    Dark,
    SystemDefault
}

enum class AppColorThemeType(val value: ColorTheme) {
    Green(ColorTheme.Green),
    DarkGreen(ColorTheme.DarkGreen),
    Purple(ColorTheme.Purple),
    Blue(ColorTheme.Blue),
    DarkBlue(ColorTheme.DarkBlue),
    Red(ColorTheme.Red),
    Brown(ColorTheme.Brown),
    Pink(ColorTheme.Pink),
    Orange(ColorTheme.Orange)
}

class SettingsRepository(private val settingsDataStore: SettingsDataStore) {

    private val _appDarkThemeFlow = MutableStateFlow(settingsDataStore.appDarkTheme)
    val appDarkTheme: StateFlow<AppDarkTheme> = _appDarkThemeFlow

    private val _appColorThemeFlow =
        MutableStateFlow(settingsDataStore.appColorThemeType)
    val appColorTheme: StateFlow<AppColorThemeType> = _appColorThemeFlow

    private val _randomizePositionFlow = MutableStateFlow(settingsDataStore.randomizePosition)
    val randomizePosition: StateFlow<Boolean> = _randomizePositionFlow

    fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) {
        settingsDataStore.appDarkTheme = appDarkTheme
        _appDarkThemeFlow.tryEmit(settingsDataStore.appDarkTheme)
    }

    fun updateAppColorTheme(appColorThemeType: AppColorThemeType) {
        settingsDataStore.appColorThemeType = appColorThemeType
        _appColorThemeFlow.tryEmit(appColorThemeType)
    }

    fun updateRandomizePosition(randomizePosition: Boolean) {
        settingsDataStore.randomizePosition = randomizePosition
        _randomizePositionFlow.tryEmit(settingsDataStore.randomizePosition)
    }
}