package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import android.content.ContextWrapper
import androidx.room.Room
import org.koin.dsl.module

val persistenceModule = module {
    single {
        val context: Context = get()

        Room.databaseBuilder(
            context,
            TimerDatabase::class.java,
            "timers_db"
        )
            .build()
    }

    single { get<TimerDatabase>().timerDao() }

    single {
        val context: Context = get()
        val name = context.packageName + "_preferences"
        context.getSharedPreferences(name, ContextWrapper.MODE_PRIVATE)
    }

    single {
        PrepopulateDataStore(preferences = get())
    }
}