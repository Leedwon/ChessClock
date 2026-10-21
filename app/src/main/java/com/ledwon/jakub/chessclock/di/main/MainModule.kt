package com.ledwon.jakub.chessclock.di.main

import com.ledwon.jakub.chessclock.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    viewModel {
        MainViewModel(settingsRepository = get())
    }
}
