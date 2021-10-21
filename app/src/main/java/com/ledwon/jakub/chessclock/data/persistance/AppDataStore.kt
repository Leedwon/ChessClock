package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore

object AppDataStore {

    val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
        name = "settings",
        produceMigrations = { context ->
            listOf(
                SharedPreferencesMigration(context, context.packageName + "_preferences")
            )
        }
    )
}