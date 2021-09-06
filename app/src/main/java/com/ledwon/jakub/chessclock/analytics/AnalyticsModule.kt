package com.ledwon.jakub.chessclock.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.koin.dsl.module

val analyticsModule = module {
    single<FirebaseAnalytics> {
        Firebase.analytics
    }

    single<AnalyticsManager> {
        AnalyticsManager(get())
    }
}