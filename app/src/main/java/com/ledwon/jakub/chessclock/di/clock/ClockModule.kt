package com.ledwon.jakub.chessclock.di.clock

import com.ledwon.jakub.chessclock.feature.clock.ClockViewModel
import com.ledwon.jakub.chessclock.feature.clock.util.PauseTimer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clockModule = module {
    factory { PauseTimer() }

    viewModel { parameters ->
        ClockViewModel(
            clockInitialData = parameters[0],
            settingsRepository = get(),
            pauseTimer = get()
        )
    }
}