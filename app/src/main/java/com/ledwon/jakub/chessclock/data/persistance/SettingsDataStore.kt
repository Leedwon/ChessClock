package com.ledwon.jakub.chessclock.data.persistance

import android.content.SharedPreferences
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.util.BooleanPreferencesDelegate
import com.ledwon.jakub.chessclock.util.EnumPreferencesDelegate

class SettingsDataStore(preferences: SharedPreferences) {

    companion object {
        const val APP_DARK_THEME_KEY = "APP_DARK_THEME_KEY"
        const val APP_COLOR_THEME_TYPE_KEY = "APP_COLOR_THEME_TYPE_KEY"
        const val RANDOMIZE_POSITION_KEY = "RANDOMIZE_POS_KEY"
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
}