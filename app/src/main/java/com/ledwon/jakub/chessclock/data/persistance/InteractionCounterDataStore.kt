package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InteractionCounterDataStore(private val context: Context) {

    private val dataStore
        get() = with(AppDataStore) {
            context.dataStore
        }

    private val countClockOpenedKey = intPreferencesKey("COUNT_CLOCK_OPENED_KEY")

    val clockOpenedCount: Flow<Int> = dataStore.data.map { preferences -> preferences[countClockOpenedKey] ?: 0 }

    suspend fun incrementClockOpenedCount() {
        dataStore.edit { mutablePreferences ->
            val currentValue = mutablePreferences[countClockOpenedKey] ?: 0
            mutablePreferences[countClockOpenedKey] = currentValue + 1
        }
    }
}
