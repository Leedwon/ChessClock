package com.ledwon.jakub.chessclock.feature.choose_clock

import com.google.android.play.core.review.ReviewInfo
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.data.persistance.InAppReviewDataStore
import com.ledwon.jakub.chessclock.data.persistance.InteractionCounterDataStore
import com.ledwon.jakub.chessclock.should
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class InAppReviewUseCaseTest {

    private val interactionCounterDataStore = mockk<InteractionCounterDataStore>(relaxUnitFun = true)
    private val inAppReviewDataStore = mockk<InAppReviewDataStore>(relaxUnitFun = true)

    private val reviewInfo = mockk<ReviewInfo>()
    private val reviewManager = mockk<ReviewManagerWrapper>() {
        coEvery { getReviewInfo() } returns reviewInfo
    }

    private var currentTimeMillis = 0L

    @Before
    fun setUp() {
        currentTimeMillis = 0L
    }

    private fun createUseCase(isDebug: Boolean = false) = InAppReviewUseCase(
            interactionCounterDataStore = interactionCounterDataStore,
            inAppReviewDataStore = inAppReviewDataStore,
            reviewManager = reviewManager,
            isDebug = isDebug,
            currentTimeMillisProvider = { currentTimeMillis }
        )

    @Test
    fun `should get in app review info when in debug mode`() = runBlocking {
        val useCase = createUseCase(isDebug = true)
        mockClockInteractionCounter(0)
        mockLastInAppReviewShowMillis(0)
        currentTimeMillis = 1
        useCase.getReviewInfoOrNull().should.beEqualTo(reviewInfo)
    }

    @Test
    fun `should get null when clock interaction is less than threshold`() = runBlocking {
        val useCase = createUseCase()

        mockClockInteractionCounter(0) // 8 is a threshold

        currentTimeMillis = 1_000L * 60 * 60 * 24 * 10 + 1 //10 days + 1s, 10 days is a threshold
        mockLastInAppReviewShowMillis(0)

        useCase.getReviewInfoOrNull().should.beEqualTo(null)
    }

    @Test
    fun `should get null when last in app review show time is less than threshold`() = runBlocking {
        val useCase = createUseCase()

        mockClockInteractionCounter(8) //8 is a threshold

        currentTimeMillis = 1_000L * 60 * 60 * 24 * 10 - 1 //10 days - 1s, 10 days is a threshold
        mockLastInAppReviewShowMillis(0)

        useCase.getReviewInfoOrNull().should.beEqualTo(null)
    }

    @Test
    fun `should get in app review info`() = runBlocking {
        val useCase = createUseCase()

        mockClockInteractionCounter(8) // 8 is a threshold

        currentTimeMillis = 1_000L * 60 * 60 * 24 * 10 + 1 //10 days + 1s, 10 days is a threshold
        mockLastInAppReviewShowMillis(0)

        useCase.getReviewInfoOrNull().should.beEqualTo(reviewInfo)
    }

    @Test
    fun `should get in app review info for the first time`() = runBlocking {
        val useCase = createUseCase()

        mockClockInteractionCounter(8) //8 is a threshold

        currentTimeMillis = 1
        mockLastInAppReviewShowMillis(null)

        useCase.getReviewInfoOrNull().should.beEqualTo(reviewInfo)
        coVerify { inAppReviewDataStore.setInAppReviewShowMillis(1L) }
    }

    private fun mockClockInteractionCounter(value: Int) {
        every { interactionCounterDataStore.clockOpenedCount } returns flowOf(value)
    }

    private fun mockLastInAppReviewShowMillis(value: Long?) {
        every { inAppReviewDataStore.lastInAppReviewShowMillis } returns flowOf(value)
    }
}
