package com.ledwon.jakub.chessclock.di.choose_timer

import com.ledwon.jakub.chessclock.feature.choose_clock.ChooseClockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chooseTimerModule = module {
    viewModel {
        ChooseClockViewModel(
            clockRepository = get(),
            analyticsManager = get(),
            prepopulateDataStore = get()
        )
    }
}