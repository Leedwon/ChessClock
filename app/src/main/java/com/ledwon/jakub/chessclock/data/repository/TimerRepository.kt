package com.ledwon.jakub.chessclock.data.repository

import com.ledwon.jakub.chessclock.data.persistance.TimerDao
import com.ledwon.jakub.chessclock.data.model.Timer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach

class TimerRepository(private val timerDao: TimerDao) {

    private val timersCache: MutableMap<Int, Timer> = mutableMapOf()

    val timers: Flow<List<Timer>> = timerDao.getAllTimers().distinctUntilChanged().onEach { timers ->
        timersCache.putAll(timers.associateBy { it.id })
    }

    suspend fun getTimerById(id: Int, allowCache: Boolean = true): Timer {
        return if (allowCache && timersCache[id] != null) {
            timersCache[id]!!
        } else {
            val timer = timerDao.getById(id)
            timersCache[id] = timer
            timer
        }
    }

    suspend fun addTimer(timer: Timer) {
        timerDao.insertTimers(listOf(timer))
        timersCache[timer.id] = timer
    }

    suspend fun addTimers(timers: List<Timer>) {
        timerDao.insertTimers(timers)
        timersCache.putAll(timers.associateBy { it.id })
    }

    suspend fun deleteTimer(timer: Timer) {
        timerDao.deleteTimer(timer)
        timersCache.remove(timer.id)
    }

}