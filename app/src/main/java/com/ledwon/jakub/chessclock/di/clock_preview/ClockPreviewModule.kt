package com.ledwon.jakub.chessclock.di.clock_preview

import com.ledwon.jakub.chessclock.feature.clock_preview.ClockPreviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clockPreviewModule = module {
    viewModel { parameters ->
        ClockPreviewViewModel(
            clockId = parameters[0],
            clockTypesRepository = get(),
            settingsRepository = get()
        )
    }
}