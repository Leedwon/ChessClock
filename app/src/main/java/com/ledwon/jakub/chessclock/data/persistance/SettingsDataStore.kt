package com.ledwon.jakub.chessclock.data.persistance

import android.content.SharedPreferences
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.util.*

class SettingsDataStore(preferences: SharedPreferences) {

    companion object {
        const val APP_DARK_THEME_KEY = "APP_DARK_THEME_KEY"
        const val APP_COLOR_THEME_TYPE_KEY = "APP_COLOR_THEME_TYPE_KEY"
        const val RANDOMIZE_POSITION_KEY = "RANDOMIZE_POS_KEY"
        const val CLOCK_TYPE_KEY = "CLOCK_TYPE_KEY"
        const val ROTATION_FIRST_KEY = "ROTATIONS_FIRST_KEY"
        const val ROTATION_SECOND_KEY = "ROTATIONS_SECOND_KEY"
        const val PULSATION_ENABLED_KEY = "PULSATION_ENABLED_KEY"
    }

    private enum class ClockTypeKey {
        OwnPlayerClock,
        BothPlayersClock,
        CircleAnimatedClock
    }

    var appDarkTheme: AppDarkTheme by EnumPreferencesDelegate(
        preferences = preferences,
        key = APP_DARK_THEME_KEY,
        enumClass = AppDarkTheme::class.java,
        defaultValue = AppDarkTheme.SystemDefault
    )

    var appColorThemeType: AppColorThemeType by EnumPreferencesDelegate(
        preferences = preferences,
        key = APP_COLOR_THEME_TYPE_KEY,
        enumClass = AppColorThemeType::class.java,
        defaultValue = AppColorThemeType.DarkGreen
    )

    var randomizePosition: Boolean by BooleanPreferencesDelegate(
        preferences = preferences,
        key = RANDOMIZE_POSITION_KEY,
        defaultValue = true
    )

    var pulsationEnabled: Boolean by BooleanPreferencesDelegate(
        preferences = preferences,
        key = PULSATION_ENABLED_KEY,
        defaultValue = true
    )

    private var clockTypeKey: ClockTypeKey by EnumPreferencesDelegate(
        preferences = preferences,
        key = CLOCK_TYPE_KEY,
        enumClass = ClockTypeKey::class.java,
        defaultValue = ClockTypeKey.OwnPlayerClock
    )

    private var rotationFirst: Float by FloatPreferencesDelegate(
        preferences = preferences,
        key = ROTATION_FIRST_KEY,
        defaultValue = 180f
    )

    private var rotationSecond: Float by FloatPreferencesDelegate(
        preferences = preferences,
        key = ROTATION_SECOND_KEY,
        defaultValue = 0f
    )

    private var rotations: Pair<Float, Float>
        set(value) {
            rotationFirst = value.first
            rotationSecond = value.second
        }
        get() = rotationFirst to rotationSecond


    var clockDisplay: ClockDisplay
        set(value) {
            when (value) {
                is ClockDisplay.OwnPlayerTimeClock -> {
                    clockTypeKey = ClockTypeKey.OwnPlayerClock
                    rotations = value.rotations
                }
                is ClockDisplay.BothPlayersTimeClock -> {
                    clockTypeKey = ClockTypeKey.BothPlayersClock
                }
                is ClockDisplay.CircleAnimatedClock -> {
                    clockTypeKey = ClockTypeKey.CircleAnimatedClock
                    rotations = value.rotations
                }
            }
        }
        get() = when (clockTypeKey) {
            ClockTypeKey.OwnPlayerClock -> ClockDisplay.OwnPlayerTimeClock(rotations = rotations)
            ClockTypeKey.BothPlayersClock -> ClockDisplay.BothPlayersTimeClock
            ClockTypeKey.CircleAnimatedClock -> ClockDisplay.CircleAnimatedClock(rotations = rotations)
        }
}