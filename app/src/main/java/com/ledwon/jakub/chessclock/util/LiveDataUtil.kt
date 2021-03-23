package com.ledwon.jakub.chessclock.util

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.postUpdate(mapper: (T) -> T) {
    this.postValue(mapper.invoke(this.value!!))
}