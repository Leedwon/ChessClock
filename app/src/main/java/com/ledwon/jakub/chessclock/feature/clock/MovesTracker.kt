package com.ledwon.jakub.chessclock.feature.clock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MovesTracker(
    private val currentTimeMillisProvider: () -> Long = { System.currentTimeMillis() }
) {

    private val _movesMillis = MutableStateFlow<List<Long>>(emptyList())
    val movesMillis: Flow<List<Long>> = _movesMillis

    private var moveStartTime: Long? = null

    private val pausesForMove = mutableListOf<Pause>()

    private val List<Pause>.isPaused: Boolean
        get() = lastOrNull() != null && last().end == null

    fun moveStarted() {
        moveStartTime = currentTimeMillisProvider()
        pausesForMove.clear()
    }

    fun moveEnded() {
        val moveStart = moveStartTime ?: error("You have to call moveStarted before calling moveEnded")
        moveStartTime = null

        val pauses: List<Pause> = pausesForMove

        val now = currentTimeMillisProvider()
        val pauseTime = pauses.fold(0L, { acc, pause ->
            val end = pause.end ?: error("You can't end move which is still paused, call moveResumed first")
            val pauseTime = end - pause.start
            acc + pauseTime
        })
        val moveTimeWithPause = now - moveStart
        val moveTime = moveTimeWithPause - pauseTime
        _movesMillis.update { it + moveTime }
    }

    fun movePaused() {
        moveStartTime ?: error("You can't pause move which is not started, call moveStarted first")
        if (pausesForMove.isPaused) error("Pause already started and not resumed, to start another pause call moveResumed first")
        pausesForMove.add(Pause(start = currentTimeMillisProvider(), end = null))
    }

    fun moveResumed() {
        if (!pausesForMove.isPaused) error("You have to call movePaused before calling moveResumed")
        pausesForMove.last().end = currentTimeMillisProvider()
    }

    fun restart() {
        moveStartTime = null
        pausesForMove.clear()
        _movesMillis.update { emptyList() }
    }

    private data class Pause(
        val start: Long,
        var end: Long?
    )
}