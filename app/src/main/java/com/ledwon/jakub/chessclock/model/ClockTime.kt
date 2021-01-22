package com.ledwon.jakub.chessclock.model

class Second(
    seconds: Int
) {
    val value: Int = seconds.coerceIn(0..60)
}

class Minute(
    minutes: Int
) {
    val value: Int = minutes.coerceIn(0..60)
}

class Hour(
    hours: Int
) {
    val value: Int = hours.coerceIn(0..24)
}

val Int.second: Second
    get() = Second(this)

val Int.minute: Minute
    get() = Minute(this)

val Int.hour: Hour
    get() = Hour(this)

data class ClockTime(
    private val second: Second = 0.second,
    private val minute: Minute = 0.minute,
    private val hour: Hour = 0.hour,
) {
    private fun formatToTwoDigitsString(value: Int) = if (value < 10) "0$value" else value

    val seconds: Int
    get() = second.value + minute.value * 60 + hour.value * 60 * 60

    override fun toString(): String {
        return if (hour.value == 0) {
            textWithoutHours
        } else {
            textWithHours
        }
    }

    private val textWithHours: String
        get() = "${formatToTwoDigitsString(hour.value)}:${formatToTwoDigitsString(minute.value)}:${
            formatToTwoDigitsString(
                second.value
            )
        }"

    private val textWithoutHours: String
        get() = textWithHours.substring(3)
}