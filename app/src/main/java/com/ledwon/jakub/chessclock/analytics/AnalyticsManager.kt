package com.ledwon.jakub.chessclock.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.ledwon.jakub.chessclock.BuildConfig
import timber.log.Timber

class AnalyticsManager(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logEvent(analyticsEvent: AnalyticsEvent) {
        if (BuildConfig.DEBUG) {
            Timber.d(
                """
                    Analytics: Logging event:
                        name: ${analyticsEvent.eventName}
                        params: ${analyticsEvent.params.toString()}
                """.trimIndent()
            )
        } else {
            firebaseAnalytics.logEvent(
                analyticsEvent.eventName,
                analyticsEvent.params
            )
        }
    }
}