package com.ledwon.jakub.chessclock.feature.clock

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicLong

class ChessClock(
    private val initialMillis: Long,
    private val incrementMillis: Long = 0,
    private val intervalMillis: Long = 50,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val currentTimeMillisProvider: () -> Long = { System.currentTimeMillis() }
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

    //-1 stand for not initialized
    private val endMillis: AtomicLong = AtomicLong(NOT_INITIALIZED)
    private var stopMillis: Long? = null

    /**
     * Starts countdown clock, after calling start make sure to call stop when you don't need clock anymore
     */
    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        endMillis.compareAndSet(NOT_INITIALIZED, currentTimeMillisProvider() + initialMillis)

        stopMillis?.let {
            stopMillis = null
            endMillis.getAndAdd(currentTimeMillisProvider() - it)
        }

        clockJob?.cancel()
        clockJob = GlobalScope.launch(defaultDispatcher) {
            gameClock.collect {
                calculateTimeAndSet()
            }
        }
    }

    /**
     * Stops countdown clock, must be called when clock is not needed anymore
     */
    fun stop() {
        stopMillis = currentTimeMillisProvider()
        clockJob?.cancel()
        clockJob = null
    }

    /**
     * Increments clock by [incrementMillis]
     */
    fun increment() {
        check(endMillis.get() != NOT_INITIALIZED) { "You must start clock before calling increment" }
        endMillis.getAndAdd(incrementMillis)
        calculateTimeAndSet()

    }

    /**
     * Resets clock. [millisLeft] is set to [initialMillis]
     * @param startImmediately - determines if clock should be immediately restarted
     * if [startImmediately] is set to false you must explicitly call [start] to start the clock again
     */
    fun restart(startImmediately: Boolean = false) {
        stop()
        _millisLeft.value = initialMillis
        stopMillis = null
        endMillis.set(NOT_INITIALIZED)
        if (startImmediately) {
            start()
        }
    }

    private fun calculateTimeAndSet() {
        _millisLeft.update {
            val newMillis = endMillis.get() - currentTimeMillisProvider()
            if (newMillis >= 0) newMillis else 0
        }
    }

    companion object {
        private const val NOT_INITIALIZED = -1L
    }
}
