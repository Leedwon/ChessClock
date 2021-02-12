package com.ledwon.jakub.chessclock.di.create_timer

import org.koin.androidx.viewmodel.dsl.viewModel
import com.ledwon.jakub.chessclock.feature.create_timer.CreateTimerViewModel
import org.koin.dsl.module

val createTimerModule = module {
    viewModel {
        CreateTimerViewModel()
    }
}