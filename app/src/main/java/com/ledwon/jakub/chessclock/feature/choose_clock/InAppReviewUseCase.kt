package com.ledwon.jakub.chessclock.feature.choose_clock

import com.google.android.play.core.ktx.requestReview
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.ledwon.jakub.chessclock.data.persistance.InAppReviewDataStore
import com.ledwon.jakub.chessclock.data.persistance.InteractionCounterDataStore
import kotlinx.coroutines.flow.first

//needed for tests, somehow mockk won't work with ktx.requestReview
interface ReviewManagerWrapper {
    suspend fun getReviewInfo(): ReviewInfo
}

class ReviewManagerWrapperImpl(private val reviewManager: ReviewManager) : ReviewManagerWrapper {
    override suspend fun getReviewInfo(): ReviewInfo = reviewManager.requestReview()
}

class InAppReviewUseCase(
    private val interactionCounterDataStore: InteractionCounterDataStore,
    private val inAppReviewDataStore: InAppReviewDataStore,
    private val reviewManager: ReviewManagerWrapper,
    private val isDebug: Boolean = false,
    private val currentTimeMillisProvider: () -> Long = { System.currentTimeMillis() }
) {

    suspend fun getReviewInfoOrNull(): ReviewInfo? {
        if (isDebug) return reviewManager.getReviewInfo().also { updateInAppReviewShowTime() }

        val clockOpenedTimes = interactionCounterDataStore.clockOpenedCount.first()
        val lastInAppReviewShowTime = inAppReviewDataStore.lastInAppReviewShowMillis.first()

        val isTimeRequirementMet =
            lastInAppReviewShowTime == null || (currentTimeMillisProvider() - lastInAppReviewShowTime) > inAppReviewLastShowTimeThresholdMillis
        val isClockOpenedRequirementMet = clockOpenedTimes > clockOpenedThreshold

        return if (isTimeRequirementMet && isClockOpenedRequirementMet) {
            reviewManager.getReviewInfo().also { updateInAppReviewShowTime() }
        } else {
            null
        }
    }

    private suspend fun updateInAppReviewShowTime() {
        inAppReviewDataStore.setInAppReviewShowMillis(currentTimeMillisProvider())
    }

    private companion object {
        const val clockOpenedThreshold = 3
        const val inAppReviewLastShowTimeThresholdMillis = 1_000 * 60 * 60 * 24 * 10 //10 days
    }
}
