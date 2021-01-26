package com.ledwon.jakub.chessclock

import android.app.Application
import com.ledwon.jakub.chessclock.feature.clock.ClockViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import timber.log.Timber

val vmModule = module {
    viewModel { CreateTimerViewModel() }
    viewModel { TimerViewModel() }
    viewModel { parameters -> ClockViewModel(initialData = parameters[0]) }
}

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(vmModule)
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}