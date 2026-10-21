package com.ledwon.jakub.chessclock.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.dsl.module

val analyticsModule = module {
    single<FirebaseAnalytics> {
        FirebaseAnalytics.getInstance(get())
    }

    single<AnalyticsManager> {
        AnalyticsManager(get())
    }
}
