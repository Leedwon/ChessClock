package com.ledwon.jakub.chessclock.feature.stats

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statsModule = module {
    viewModel { parameters -> StatsViewModel(movesMillis = parameters[0]) }
}
