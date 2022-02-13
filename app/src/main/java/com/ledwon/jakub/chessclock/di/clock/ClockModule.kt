package com.ledwon.jakub.chessclock.di.clock

import com.ledwon.jakub.chessclock.feature.clock.ClockViewModel
import com.ledwon.jakub.chessclock.feature.clock.PositionRandomizer
import com.ledwon.jakub.chessclock.feature.clock.util.PauseClock
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val clockModule = module {
    factory { PauseClock() }

    factory { PositionRandomizer() }

    viewModel { parameters ->
        ClockViewModel(
            clockInitialData = parameters[0],
            settingsRepository = get(),
            pauseClock = get(),
            analyticsManager = get(),
            positionRandomizer = get()
        )
    }
}