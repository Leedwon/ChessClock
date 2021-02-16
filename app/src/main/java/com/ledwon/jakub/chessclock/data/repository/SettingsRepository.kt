package com.ledwon.jakub.chessclock.data.repository

import androidx.compose.runtime.MutableState
import com.ledwon.jakub.chessclock.data.persistance.SettingsDataStore
import com.ledwon.jakub.chessclock.ui.ColorTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class AppDarkTheme {
    Light,
    Dark,
    SystemDefault
}

enum class AppColorThemeType {
    Green,
    DarkGreen,
    Purple,
    Blue,
    DarkBlue,
    Red,
    Brown,
    Pink,
    Orange
}

class SettingsRepository(private val settingsDataStore: SettingsDataStore) {

    private val _appDarkThemeFlow = MutableStateFlow(settingsDataStore.appDarkTheme)
    val appDarkTheme: StateFlow<AppDarkTheme> = _appDarkThemeFlow

    private val _appColorThemeFlow =
        MutableStateFlow(settingsDataStore.appColorThemeType.toColorTheme())
    val appColorTheme: StateFlow<ColorTheme> = _appColorThemeFlow

    private val _randomizePositionFlow = MutableStateFlow(settingsDataStore.randomizePosition)
    val randomizePosition: StateFlow<Boolean> = _randomizePositionFlow

    fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) {
        settingsDataStore.appDarkTheme = appDarkTheme
        _appDarkThemeFlow.tryEmit(settingsDataStore.appDarkTheme)
    }

    fun updateAppColorTheme(appColorThemeType: ColorTheme) {
        settingsDataStore.appColorThemeType = appColorThemeType.toAppColorThemeType()
        _appColorThemeFlow.tryEmit(appColorThemeType)
    }

    fun updateRandomizePosition(randomizePosition: Boolean) {
        settingsDataStore.randomizePosition = randomizePosition
        _randomizePositionFlow.tryEmit(settingsDataStore.randomizePosition)
    }

    private fun AppColorThemeType.toColorTheme(): ColorTheme {
        return when (this) {
            AppColorThemeType.Green -> ColorTheme.Green
            AppColorThemeType.DarkGreen -> ColorTheme.DarkGreen
            AppColorThemeType.Purple -> ColorTheme.Purple
            AppColorThemeType.Blue -> ColorTheme.Blue
            AppColorThemeType.DarkBlue -> ColorTheme.DarkBlue
            AppColorThemeType.Red -> ColorTheme.Red
            AppColorThemeType.Pink -> ColorTheme.Pink
            AppColorThemeType.Brown -> ColorTheme.Brown
            AppColorThemeType.Orange -> ColorTheme.Orange
        }
    }

    private fun ColorTheme.toAppColorThemeType(): AppColorThemeType {
        return when (this) {
            is ColorTheme.Green -> AppColorThemeType.Green
            is ColorTheme.DarkGreen -> AppColorThemeType.DarkGreen
            is ColorTheme.Purple -> AppColorThemeType.Purple
            is ColorTheme.Blue -> AppColorThemeType.Blue
            is ColorTheme.DarkBlue -> AppColorThemeType.DarkBlue
            is ColorTheme.Red -> AppColorThemeType.Red
            is ColorTheme.Pink -> AppColorThemeType.Pink
            is ColorTheme.Brown -> AppColorThemeType.Brown
            is ColorTheme.Orange -> AppColorThemeType.Orange
        }
    }
}