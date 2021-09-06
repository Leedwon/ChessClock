package com.ledwon.jakub.chessclock.di.choose_timer

import com.ledwon.jakub.chessclock.feature.choose_timer.ChooseTimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chooseTimerModule = module {
    viewModel {
        ChooseTimerViewModel(
            timerRepository = get(),
            analyticsManager = get(),
            prepopulateDataStore = get()
        )
    }
}