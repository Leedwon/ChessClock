package com.ledwon.jakub.database.di

import android.content.Context
import androidx.room.Room
import com.ledwon.jakub.database.*
import com.ledwon.jakub.database.TimerDao
import com.ledwon.jakub.database.TimerDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<TimerDatabase> {
        val context: Context = get()

        Room.databaseBuilder(
            context,
            TimerDatabase::class.java,
            "timers_db"
        )
            .addMigrations(Migrations.Migration_1_2)
            .build()
    }

    single<TimerDao> { get<TimerDatabase>().timerDao() }

    single<DatabaseInteractor> {
        DatabaseInteractorImpl(timerDao = get())
    }
}