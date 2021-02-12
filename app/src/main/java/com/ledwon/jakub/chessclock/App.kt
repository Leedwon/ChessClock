package com.ledwon.jakub.chessclock

import android.app.Application
import com.ledwon.jakub.chessclock.di.choose_timer.chooseTimerModule
import com.ledwon.jakub.chessclock.di.clock.clockModule
import com.ledwon.jakub.chessclock.di.create_timer.createTimerModule
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                chooseTimerModule,
                clockModule,
                createTimerModule
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}