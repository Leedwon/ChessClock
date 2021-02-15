package com.ledwon.jakub.chessclock.di.settings

import com.ledwon.jakub.chessclock.feature.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel {
        SettingsViewModel(settingsRepository = get())
    }
}