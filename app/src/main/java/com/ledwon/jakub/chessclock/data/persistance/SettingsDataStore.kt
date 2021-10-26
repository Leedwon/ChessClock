package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import androidx.datastore.preferences.core.*
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SettingsDataStore(private val context: Context) {

    private enum class ClockTypeKey {
        OwnPlayerClock,
        BothPlayersClock,
        CircleAnimatedClock
    }

    private val dataStore
        get() = with(AppDataStore) { context.dataStore }

    private val appDarkThemeKey = stringPreferencesKey("APP_DARK_THEME_KEY")
    private val appColorThemeKey = stringPreferencesKey("APP_COLOR_THEME_TYPE_KEY")
    private val randomizePositionKey = booleanPreferencesKey("RANDOMIZE_POS_KEY")
    private val pulsationEnabledKey = booleanPreferencesKey("PULSATION_ENABLED_KEY")
    private val clockTypePrefsKey = stringPreferencesKey("CLOCK_TYPE_KEY")
    private val rotationFirstKey = floatPreferencesKey("ROTATIONS_FIRST_KEY")
    private val rotationSecondKey = floatPreferencesKey("ROTATIONS_SECOND_KEY")

    val appDarkTheme: Flow<AppDarkTheme> =
        dataStore.data
            .map { preferences -> preferences[appDarkThemeKey] ?: AppDarkTheme.SystemDefault.name }
            .map { themeName -> AppDarkTheme.valueOf(themeName) }

    val appColorThemeType: Flow<AppColorThemeType> =
        dataStore.data
            .map { preferences -> preferences[appColorThemeKey] ?: AppColorThemeType.DarkGreen.name }
            .map { AppColorThemeType.valueOf(it) }

    val randomizePosition: Flow<Boolean> = dataStore.data.map { preferences -> preferences[randomizePositionKey] ?: true }

    val pulsationEnabled: Flow<Boolean> = dataStore.data.map { preferences -> preferences[pulsationEnabledKey] ?: true }

    private val rotationFirst: Flow<Float> = dataStore.data.map { preferences -> preferences[rotationFirstKey] ?: 180f }

    private val rotationSecond: Flow<Float> = dataStore.data.map { preferences -> preferences[rotationSecondKey] ?: 0f }

    private val clockTypeKey: Flow<ClockTypeKey> =
        dataStore.data
            .map { preferences -> preferences[clockTypePrefsKey] ?: ClockTypeKey.OwnPlayerClock.name }
            .map { ClockTypeKey.valueOf(it) }

    val clockDisplay: Flow<ClockDisplay> =
        combine(
            clockTypeKey,
            rotationFirst,
            rotationSecond
        ) { clockTypeKey, rotationFirst, rotationSecond ->
            when (clockTypeKey) {
                ClockTypeKey.OwnPlayerClock -> ClockDisplay.OwnPlayerTimeClock(rotations = rotationFirst to rotationSecond)
                ClockTypeKey.BothPlayersClock -> ClockDisplay.BothPlayersTimeClock
                ClockTypeKey.CircleAnimatedClock -> ClockDisplay.CircleAnimatedClock(rotations = rotationFirst to rotationSecond)
            }
        }

    suspend fun updateAppDarkTheme(value: AppDarkTheme) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[appDarkThemeKey] = value.name
        }
    }

    suspend fun updateAppColorThemeType(value: AppColorThemeType) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[appColorThemeKey] = value.name
        }
    }


    suspend fun updateRandomizePosition(value: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[randomizePositionKey] = value
        }
    }

    suspend fun updatePulsationEnabled(value: Boolean) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[pulsationEnabledKey] = value
        }
    }

    private suspend fun updateClockTypeKey(value: ClockTypeKey) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[clockTypePrefsKey] = value.name
        }
    }

    private suspend fun updateRotationFirst(value: Float) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[rotationFirstKey] = value
        }
    }

    private suspend fun updateRotationSecond(value: Float) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[rotationSecondKey] = value
        }
    }

    private suspend fun updateClockDisplay(clockTypeKey: ClockTypeKey, rotations: Pair<Float, Float>? = null) {
        updateClockTypeKey(clockTypeKey)
        rotations?.let {
            updateRotationFirst(it.first)
            updateRotationSecond(it.second)
        }
    }

    suspend fun updateClockDisplay(value: ClockDisplay) {
        when (value) {
            ClockDisplay.BothPlayersTimeClock -> {
                updateClockDisplay(ClockTypeKey.BothPlayersClock)
            }
            is ClockDisplay.CircleAnimatedClock -> {
                updateClockDisplay(ClockTypeKey.CircleAnimatedClock, value.rotations)
            }
            is ClockDisplay.OwnPlayerTimeClock -> {
                updateClockDisplay(ClockTypeKey.OwnPlayerClock, value.rotations)
            }
        }
    }
}