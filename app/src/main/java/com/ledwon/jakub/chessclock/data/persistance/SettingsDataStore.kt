package com.ledwon.jakub.chessclock.data.persistance

import android.content.SharedPreferences
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.feature.clock.ClockType
import com.ledwon.jakub.chessclock.util.BooleanPreferencesDelegate
import com.ledwon.jakub.chessclock.util.EnumPreferencesDelegate

class SettingsDataStore(private val preferences: SharedPreferences) {

    companion object {
        const val APP_DARK_THEME_KEY = "APP_DARK_THEME_KEY"
        const val APP_COLOR_THEME_TYPE_KEY = "APP_COLOR_THEME_TYPE_KEY"
        const val RANDOMIZE_POSITION_KEY = "RANDOMIZE_POS_KEY"
        const val CLOCK_TYPE_KEY = "CLOCK_TYPE_KEY"
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

    //todo find better way?
    var clockType: ClockType
        set(value) {
            when (value) {
                is ClockType.OwnPlayerTimeClock -> {
                    preferences.edit().putStringSet(
                        CLOCK_TYPE_KEY,
                        setOf(
                            value.javaClass.name,
                            value.rotations.first.toString(),
                            value.rotations.second.toString()
                        )
                    ).apply()
                }
                is ClockType.BothPlayersTimeClock -> {
                    preferences.edit().putStringSet(CLOCK_TYPE_KEY, setOf(value.javaClass.name))
                        .apply()
                }
                is ClockType.CircleAnimatedClock -> {
                    preferences.edit().putStringSet(
                        CLOCK_TYPE_KEY,
                        setOf(
                            value.javaClass.name,
                            value.rotations.first.toString(),
                            value.rotations.second.toString()
                        )
                    ).apply()
                }
            }
        }
        get() {
            val set: Set<String> = preferences.getStringSet(
                CLOCK_TYPE_KEY,
                setOf(ClockType.OwnPlayerTimeClock::javaClass.name, 180f.toString(), 0f.toString())
            )!!

            return when (set.elementAt(0)) {
                ClockType.OwnPlayerTimeClock::javaClass.name -> {
                    ClockType.OwnPlayerTimeClock(
                        set.elementAt(1).toFloat() to set.elementAt(2).toFloat()
                    )
                }
                ClockType.BothPlayersTimeClock::javaClass.name -> {
                    ClockType.BothPlayersTimeClock
                }
                ClockType.CircleAnimatedClock::javaClass.name -> {
                    ClockType.CircleAnimatedClock(
                        set.elementAt(1).toFloat() to set.elementAt(2).toFloat()
                    )
                }
                else -> error("Unknown clock type")
            }
        }
}