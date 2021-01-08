package com.example.chessclock

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TimerViewModel @ViewModelInject constructor() : ViewModel() {
    data class State(
        val whiteTime: Float,
        val blackTime: Float
    )

    private val _state: MutableLiveData<State> = MutableLiveData(
        State(
            whiteTime = 10f,
            blackTime = 10f
        )
    )
    val state: LiveData<State> = _state

    private val _command: MutableLiveData<Command> = MutableLiveData()
    val command: LiveData<Command> = _command

    fun onWhiteTimeChanged(value: Float) {
        _state.value = _state.value!!.copy(
            whiteTime = value
        )
    }

    fun onBlackTimeChanged(value: Float) {
        _state.value = _state.value!!.copy(
            blackTime = value
        )
    }

    fun onPlayClicked() {
        _command.value = Command.NavigateToClock(_state.value!!)
        _command.value = null
    }

    sealed class Command {
        data class NavigateToClock(val state: State) : Command()
    }

}