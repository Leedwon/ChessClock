package com.ledwon.jakub.chessclock.feature.choose_timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.model.ClockTime

data class Timer(
    val clockTime: ClockTime,
    val description: String
)

object Timers {
    val values = listOf(
        Timer(
            clockTime = ClockTime(minutes = 1),
            description = "Bullet 1"
        ),
        Timer(
            clockTime = ClockTime(minutes = 1, increment = 1),
            description = "Bullet 1 + 1"
        ),
        Timer(
            clockTime = ClockTime(minutes = 2, increment = 1),
            description = "Bullet 2 + 1"
        ),
        Timer(
            clockTime = ClockTime(minutes = 3),
            description = "Blitz 3"
        ),
        Timer(
            clockTime = ClockTime(minutes = 3, increment = 2),
            description = "Blitz 3 + 2"
        ),
        Timer(
            clockTime = ClockTime(minutes = 5),
            description = "Blitz 5"
        ),
        Timer(
            clockTime = ClockTime(minutes = 10),
            description = "Rapid 10"
        ),
        Timer(
            clockTime = ClockTime(minutes = 15),
            description = "Rapid 15"
        ),
        Timer(
            clockTime = ClockTime(minutes = 30),
            description = "Regular 30"
        ),
        Timer(
            clockTime = ClockTime(hours = 1),
            description = "Regular 60"
        )
    )
}

class ChooseTimerViewModel : ViewModel() {

    private val _timers: MutableLiveData<List<Timer>> = MutableLiveData(Timers.values)
    val timers: LiveData<List<Timer>> = _timers

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    fun onTimerClicked(timer: Timer) {
        _command.value = Command.NavigateToClock(timer)
        _command.value = null
    }

    fun onCreateTimerClicked() {
        _command.value = Command.NavigateToCreateTimer
        _command.value = null
    }

    sealed class Command {
        data class NavigateToClock(val timer: Timer) : Command()
        object NavigateToCreateTimer : Command()
    }
}