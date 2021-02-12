package com.ledwon.jakub.chessclock.model

data class ClockTime(
    val seconds: Int = 0,
    val minutes: Int = 0,
    val hours: Int = 0,
    val increment: Int = 0
) {
    private fun formatToTwoDigitsString(value: Int) = if (value < 10) "0$value" else value

    val secondsSum: Int
        get() = seconds + minutes * 60 + hours * 60 * 60

    override fun toString(): String {
        return if (hours == 0) {
            textWithoutHours
        } else {
            textWithHours
        }
    }

    private val textWithHours: String
        get() = "${formatToTwoDigitsString(hours)}:${formatToTwoDigitsString(minutes)}:${
            formatToTwoDigitsString(
                seconds
            )
        }"

    private val textWithoutHours: String
        get() = textWithHours.substring(3)
}