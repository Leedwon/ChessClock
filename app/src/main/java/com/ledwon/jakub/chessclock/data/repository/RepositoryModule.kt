package com.ledwon.jakub.chessclock.data.repository

import org.koin.dsl.module

val repositoryModule = module {
    single {
        TimerRepository(get())
    }
}