package com.ledwon.jakub.chessclock.feature.clock.util

class PauseClock {
    private var startTime: Long? = null
    private var stopTime: Long? = null

    val canBeConsumed: Boolean
        get() = startTime != null && stopTime != null

    val started: Boolean
        get() = startTime != null

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun stop() {
        stopTime = System.currentTimeMillis()
    }

    fun restart() {
        startTime = null
        stopTime = null
    }

    fun consume(): Long {
        val start = startTime
        val stop = stopTime
        if (start == null) error("Can't consume pause if start wasn't called")
        if (stop == null) error("Can't consume pause if stop wasn't called")

        startTime = null
        stopTime = null

        return stop - start
    }
}