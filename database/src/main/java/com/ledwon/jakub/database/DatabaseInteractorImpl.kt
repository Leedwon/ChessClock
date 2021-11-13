package com.ledwon.jakub.database

import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.database.model.Mappers.toClock
import com.ledwon.jakub.database.model.Mappers.toTimer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class DatabaseInteractorImpl constructor(
    private val timerDao: TimerDao
) : DatabaseInteractor {

    override fun getAllClocks(): Flow<List<Clock>> {
        return timerDao.getAllTimers().map { timers -> timers.map { it.toClock() } }
    }

    override suspend fun getClockById(clockId: Int): Clock {
        return timerDao.getById(clockId).toClock()
    }

    override suspend fun insertClocks(clocks: List<Clock>) {
        timerDao.insertTimers(clocks.map { it.toTimer() })
    }

    override suspend fun deleteClock(clock: Clock) {
        timerDao.deleteTimer(clock.toTimer())
    }

    override suspend fun deleteClocks(clocks: List<Clock>) {
        timerDao.deleteTimers(clocks.map { it.toTimer() })
    }

    override suspend fun updateClockFavouriteStatus(clockId: Int, isFavourite: Boolean) {
        timerDao.updateFavouriteStatus(
            id = clockId,
            isFavourite = isFavourite
        )
    }
}