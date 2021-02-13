package com.ledwon.jakub.chessclock.data.persistance

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ledwon.jakub.chessclock.data.model.Timer

@Database(entities = [Timer::class], version = 1)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao
}