package com.ledwon.jakub.chessclock.di.create_clock

import org.koin.androidx.viewmodel.dsl.viewModel
import com.ledwon.jakub.chessclock.feature.create_clock.CreateClockViewModel
import org.koin.dsl.module

val createClockModule = module {
    viewModel {
        CreateClockViewModel(
            clockRepository = get(),
            analyticsManager = get()
        )
    }
}