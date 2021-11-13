package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.database.DatabaseInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class ClockRepository(private val databaseInteractor: DatabaseInteractor) {

    val clocks: Flow<List<Clock>> =
        databaseInteractor
            .getAllClocks()
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    suspend fun addClock(clock: Clock) {
        addClocks(listOf(clock))
    }

    suspend fun addClocks(clocks: List<Clock>) {
        withContext(Dispatchers.Default) {
            databaseInteractor.insertClocks(clocks)
        }
    }

    suspend fun deleteClocks(clocks: List<Clock>) {
        withContext(Dispatchers.Default) {
            databaseInteractor.deleteClocks(clocks)
        }
    }

    suspend fun updateClockFavouriteStatus(clockId: Int, isFavourite: Boolean) {
        withContext(Dispatchers.Default) {
            databaseInteractor.updateClockFavouriteStatus(clockId, isFavourite)
        }
    }
}