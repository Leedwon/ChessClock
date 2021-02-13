package com.ledwon.jakub.chessclock.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    @Embedded(prefix = "white_")
    val whiteClockTime: ClockTime,
    @Embedded(prefix = "black_")
    val blackClockTime: ClockTime = whiteClockTime,
    val isFavourite: Boolean = false
)