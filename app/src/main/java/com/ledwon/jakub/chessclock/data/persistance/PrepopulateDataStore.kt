package com.ledwon.jakub.chessclock.data.persistance

import android.content.SharedPreferences
import com.ledwon.jakub.chessclock.util.BooleanPreferencesDelegate

//todo remove it and introduce pre population asset
class PrepopulateDataStore(preferences: SharedPreferences) {

    companion object {
        private const val SHOULD_PREPOPULATE_DB = "PREPOPULATE_KEY"
    }

    var shouldPrepopulateDatabase: Boolean by BooleanPreferencesDelegate(
        preferences = preferences,
        key = SHOULD_PREPOPULATE_DB,
        defaultValue = true
    )
}