package com.ledwon.jakub.chessclock

import android.app.Application
import com.ledwon.jakub.chessclock.analytics.analyticsModule
import com.ledwon.jakub.chessclock.data.persistance.persistenceModule
import com.ledwon.jakub.chessclock.data.repository.repositoryModule
import com.ledwon.jakub.chessclock.di.choose_clock.chooseClockModule
import com.ledwon.jakub.chessclock.di.clock.clockModule
import com.ledwon.jakub.chessclock.di.clock_preview.clockPreviewModule
import com.ledwon.jakub.chessclock.di.create_clock.createClockModule
import com.ledwon.jakub.chessclock.di.main.mainModule
import com.ledwon.jakub.chessclock.di.settings.settingsModule
import com.ledwon.jakub.chessclock.feature.stats.statsModule
import com.ledwon.jakub.database.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                chooseClockModule,
                clockModule,
                createClockModule,
                databaseModule,
                persistenceModule,
                repositoryModule,
                mainModule,
                settingsModule,
                clockPreviewModule,
                analyticsModule,
                statsModule
            )
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}