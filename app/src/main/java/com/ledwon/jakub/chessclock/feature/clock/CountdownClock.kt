package com.ledwon.jakub.chessclock.feature.clock

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CountdownClock(
    private val initialMillis: Long,
    private val intervalMillis: Long = 50,
    private val defaultDispatcher: CoroutineDispatcher,
) {
    private val gameClock = flow {
        delay(intervalMillis)
        while (true) {
            emit(Unit)
            delay(intervalMillis)
        }
    }.flowOn(defaultDispatcher)

    private var clockJob: Job? = null

    private var _millisLeft = MutableStateFlow(initialMillis)
    val millisLeft: Flow<Long> = _millisLeft

    /**
     * Starts countdown clock, after calling start make sure to call stop when you don't need clock anymore
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        clockJob?.cancel()
        clockJob = GlobalScope.launch(defaultDispatcher) {
            gameClock.collect {
                val newMillis = _millisLeft.value - intervalMillis
                if (newMillis >= 0) {
                    _millisLeft.emit(newMillis)
                }
            }
        }
    }

    /**
     * Stops countdown clock, must be called when clock is not needed anymore
     */
    fun stop() {
        clockJob?.cancel()
        clockJob = null
    }

    /**
     * Resets clock. [millisLeft] is set to [initialMillis]
     * @param startImmediately - determines if clock should be immediately restarted
     * if [startImmediately] is set to false you must explicitly call [start] to start the clock again
     */
    fun reset(startImmediately: Boolean = false) {
        stop()
        _millisLeft.value = initialMillis
        if (startImmediately) {
            start()
        }
    }
}
