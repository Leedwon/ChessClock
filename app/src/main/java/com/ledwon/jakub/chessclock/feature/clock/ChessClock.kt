package com.ledwon.jakub.chessclock.feature.clock

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class ChessClock(
    private val initialMillis: Long,
    private val intervalMillis: Long = 50,
    private val incrementMillis: Long = 0,
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
     * Increments clock by [incrementMillis]
     */
    fun increment() {
        _millisLeft.update { it + incrementMillis }
    }

    /**
     * Starts countdown clock, after calling start make sure to call stop when you don't need clock anymore
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        clockJob?.cancel()
        clockJob = GlobalScope.launch(defaultDispatcher) {
            gameClock.collect {
                _millisLeft.update {
                    val newMillis = it - intervalMillis
                    if (newMillis >= 0) newMillis else it
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
