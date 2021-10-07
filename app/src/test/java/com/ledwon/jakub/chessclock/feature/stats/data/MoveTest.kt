package com.ledwon.jakub.chessclock.feature.stats.data

import org.junit.Assert.assertEquals
import org.junit.Test

class MoveTest {

    @Test
    fun `move should have correctly formatted duration string`() {
        val move = Move(5234, PlayerColor.White)
        assertEquals("5.23s", move.duration)
    }
}