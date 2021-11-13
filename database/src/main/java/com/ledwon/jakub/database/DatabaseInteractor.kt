package com.ledwon.jakub.database

import com.ledwon.jakub.chessclock.model.Clock
import kotlinx.coroutines.flow.Flow

interface DatabaseInteractor {
    fun getAllClocks(): Flow<List<Clock>>
    suspend fun getClockById(clockId: Int): Clock
    suspend fun insertClocks(clocks: List<Clock>)
    suspend fun deleteClock(clock: Clock)
    suspend fun deleteClocks(clocks: List<Clock>)
    suspend fun updateClockFavouriteStatus(clockId: Int, isFavourite: Boolean)
}