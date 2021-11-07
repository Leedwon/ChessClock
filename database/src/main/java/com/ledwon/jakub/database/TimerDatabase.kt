package com.ledwon.jakub.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ledwon.jakub.database.model.Timer

@Database(entities = [Timer::class], version = 2)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
}

object Migrations {
    val Migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //Drop column isn't supported by SQLite, so the data must manually be moved
            with(database) {
                execSQL("CREATE TABLE Timers_Backup (id INTEGER NOT NULL, white_seconds INTEGER NOT NULL, white_minutes INTEGER NOT NULL, white_hours INTEGER NOT NULL, white_increment INTEGER NOT NULL, black_seconds INTEGER NOT NULL, black_minutes INTEGER NOT NULL, black_hours INTEGER NOT NULL, black_increment INTEGER NOT NULL, isFavourite INTEGER NOT NULL, PRIMARY KEY (id))")
                execSQL("INSERT INTO Timers_Backup SELECT id, white_seconds, white_minutes, white_hours, white_increment, black_seconds, black_minutes, black_hours, black_increment, isFavourite FROM Timer")
                execSQL("DROP TABLE Timer")
                execSQL("ALTER TABLE Timers_Backup RENAME to Timer")
            }
        }
    }
}