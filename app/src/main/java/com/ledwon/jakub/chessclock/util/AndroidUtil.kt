package com.ledwon.jakub.chessclock.util

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory

fun Context.findActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * This function tries to showInAppReview.
 * It won't always succeed as in app review api doesn't guarantee to show in app review moreover for it to succeed given context must be connected to some activity
 */
suspend fun Context.showInAppReviewIfPossible(reviewInfo: ReviewInfo) {
    findActivity()?.let {
        val manager = ReviewManagerFactory.create(it)
        manager.launchReviewFlow(it, reviewInfo)

    }
}
