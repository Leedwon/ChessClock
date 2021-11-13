package com.ledwon.jakub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ledwon.jakub.database.model.Timer
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TimerDao {
    @Query("SELECT * from timer ORDER BY isFavourite desc, id desc")
    fun getAllTimers(): Flow<List<Timer>>

    @Query("SELECT * from timer where id = :timerId")
    suspend fun getById(timerId: Int): Timer

    @Insert
    suspend fun insertTimers(timers: List<Timer>)

    @Delete
    suspend fun deleteTimer(timer: Timer)

    @Delete
    suspend fun deleteTimers(timers: List<Timer>)

    @Query("UPDATE timer set isFavourite=:isFavourite where id=:id")
    suspend fun updateFavouriteStatus(id: Int, isFavourite: Boolean)
}