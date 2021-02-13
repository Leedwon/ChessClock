package com.ledwon.jakub.chessclock.data.persistance

import android.content.SharedPreferences

//todo remove it and introduce pre population asset
class PrepopulateDataStore(private val preferences: SharedPreferences) {

    companion object {
        private const val SHOULD_PREPOPULATE_DB = "PREPOPULATE_KEY"
    }

    var shouldPrepopulateDatabase: Boolean
        get() = preferences.getBoolean(SHOULD_PREPOPULATE_DB, true)
        set(value) {
            preferences.edit().putBoolean(SHOULD_PREPOPULATE_DB, value).apply()
        }
}