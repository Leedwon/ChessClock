package com.ledwon.jakub.chessclock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber

class CreateTimerViewModel : ViewModel() {

    private val _text : MutableLiveData<String> = MutableLiveData("Hello from vm")
    val text: LiveData<String> = _text

    override fun onCleared() {
        Timber.i("onCleared")
        super.onCleared()
    }
}