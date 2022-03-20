package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InAppReviewDataStore(private val context: Context) {

    private val dataStore
        get() = with(AppDataStore) {
            context.dataStore
        }

    private val inAppReviewTimeKey = longPreferencesKey("INN_APP_REVIEW_TIME_KEY")

    val lastInAppReviewShowMillis: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[inAppReviewTimeKey]
    }

    suspend fun setInAppReviewShowMillis(millis: Long) {
        dataStore.edit { mutablePreferences ->
            mutablePreferences[inAppReviewTimeKey] = millis
        }
    }
}

