package com.ledwon.jakub.chessclock.data.repository

import org.koin.dsl.module

val repositoryModule = module {
    single { ClockRepository(get()) }
    single { SettingsRepository(get()) }
    single { ClockTypesRepository() }
}