package com.example.chessclock

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chessclock.model.ClockTime
import com.example.chessclock.model.hour
import com.example.chessclock.model.minute
import com.example.chessclock.model.second
import kotlinx.coroutines.flow.flowOf

data class Timer(
    val clockTime: ClockTime,
    val timeAdditionPerMove: ClockTime? = null,
    val description: String
)

object Timers {
    val values = listOf(
        Timer(
            clockTime = ClockTime(minute = 1.minute),
            description = "Bullet 1"
        ),
        Timer(
            clockTime = ClockTime(minute = 1.minute),
            timeAdditionPerMove = ClockTime(second = 1.second),
            description = "Bullet 1 + 1"
        ),
        Timer(
            clockTime = ClockTime(minute = 2.minute),
            timeAdditionPerMove = ClockTime(second = 1.second),
            description = "Bullet 2 + 1"
        ),
        Timer(
            clockTime = ClockTime(minute = 3.minute),
            description = "Blitz 3"
        ),
        Timer(
            clockTime = ClockTime(minute = 5.minute),
            description = "Blitz 5"
        ),
        Timer(
            clockTime = ClockTime(minute = 10.minute),
            description = "Rapid 10"
        ),
        Timer(
            clockTime = ClockTime(minute = 15.minute),
            description = "Rapid 15"
        ),
        Timer(
            clockTime = ClockTime(minute = 30.minute),
            description = "Regular 30"
        ),
        Timer(
            clockTime = ClockTime(hour = 1.hour),
            description = "Regular 60"
        )
    )
}

class TimerViewModel @ViewModelInject constructor() : ViewModel() {

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