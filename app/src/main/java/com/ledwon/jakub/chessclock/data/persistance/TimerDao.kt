package com.ledwon.jakub.chessclock.data.persistance

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ledwon.jakub.chessclock.data.model.Timer
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {
    @Query("SELECT * from timer ORDER BY isFavourite asc, id desc")
    fun getAllTimers(): Flow<List<Timer>>

    @Query("SELECT * from timer where id = :timerId")
    suspend fun getById(timerId: Int): Timer

    @Insert
    suspend fun insertTimers(timers: List<Timer>)

    @Delete
    suspend fun deleteTimer(timer: Timer)

    @Query("UPDATE timer set isFavourite=:isFavourite where id=:id")
    suspend fun updateFavouriteStatus(id: Int, isFavourite: Boolean)
}