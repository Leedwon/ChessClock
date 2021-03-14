package com.ledwon.jakub.chessclock

import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository

class MainViewModel(settingsRepository: SettingsRepository) : ViewModel() {
    val appDarkTheme = settingsRepository.appDarkTheme
    val appColorTheme = settingsRepository.appColorTheme
}