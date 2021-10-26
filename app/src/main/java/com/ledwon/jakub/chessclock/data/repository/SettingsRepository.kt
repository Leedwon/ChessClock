package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.data.persistance.SettingsDataStore
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.ui.ColorTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

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

class SettingsRepository(
    private val settingsDataStore: SettingsDataStore,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    val appDarkTheme: Flow<AppDarkTheme> = settingsDataStore.appDarkTheme
    val appColorTheme: Flow<AppColorThemeType> = settingsDataStore.appColorThemeType
    val randomizePosition: Flow<Boolean> = settingsDataStore.randomizePosition
    val clockDisplay: Flow<ClockDisplay> = settingsDataStore.clockDisplay
    val pulsationEnabled: Flow<Boolean> = settingsDataStore.pulsationEnabled

    suspend fun updateAppDarkTheme(appDarkTheme: AppDarkTheme) {
        withContext(defaultDispatcher) {
            settingsDataStore.updateAppDarkTheme(appDarkTheme)
        }
    }

    suspend fun updateAppColorTheme(appColorThemeType: AppColorThemeType) {
        withContext(defaultDispatcher) {
            settingsDataStore.updateAppColorThemeType(appColorThemeType)
        }
    }

    suspend fun updateRandomizePosition(randomizePosition: Boolean) {
        withContext(defaultDispatcher) {
            settingsDataStore.updateRandomizePosition(randomizePosition)
        }
    }

    suspend fun updateClockType(clockDisplay: ClockDisplay) {
        withContext(defaultDispatcher) {
            settingsDataStore.updateClockDisplay(clockDisplay)
        }
    }

    suspend fun updatePulsationEnabled(pulsationEnabled: Boolean) {
        withContext(defaultDispatcher) {
            settingsDataStore.updatePulsationEnabled(pulsationEnabled)
        }
    }
}