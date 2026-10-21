package com.ledwon.jakub.chessclock.di.settings

import com.ledwon.jakub.chessclock.feature.settings.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    viewModel {
        SettingsViewModel(
            settingsRepository = get(),
            clockTypesRepository = get(),
            analyticsManager = get()
        )
    }
}
