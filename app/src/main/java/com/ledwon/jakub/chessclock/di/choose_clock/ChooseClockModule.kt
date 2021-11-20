package com.ledwon.jakub.chessclock.di.choose_clock

import com.ledwon.jakub.chessclock.feature.choose_clock.ChooseClockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chooseClockModule = module {
    viewModel {
        ChooseClockViewModel(
            clockRepository = get(),
            analyticsManager = get(),
            prepopulateDataStore = get()
        )
    }
}