package com.ledwon.jakub.chessclock.di.clock

import com.ledwon.jakub.chessclock.feature.clock.ClockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clockModule = module {
    viewModel { parameters ->
        ClockViewModel(
            initialData = parameters[0],
            settingsRepository = get()
        )
    }
}