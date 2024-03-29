package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//todo remove it and introduce pre population asset
class PrepopulateDataStore(private val context: Context) {

    private val dataStore
        get() = with(AppDataStore) {
            context.dataStore
        }

    private val shouldPrepopulateDbKey = booleanPreferencesKey("PREPOPULATE_KEY")

    val shouldPrepopulateDatabase: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[shouldPrepopulateDbKey] ?: true
    }

    suspend fun updateShouldPrepopulateDatabase(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[shouldPrepopulateDbKey] = value
        }
    }
}