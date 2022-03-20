package com.ledwon.jakub.chessclock.di.choose_clock

import com.google.android.play.core.review.ReviewManagerFactory
import com.ledwon.jakub.chessclock.feature.choose_clock.ChooseClockViewModel
import com.ledwon.jakub.chessclock.feature.choose_clock.InAppReviewUseCase
import com.ledwon.jakub.chessclock.feature.choose_clock.ReviewManagerWrapper
import com.ledwon.jakub.chessclock.feature.choose_clock.ReviewManagerWrapperImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chooseClockModule = module {

    factory {
        ReviewManagerFactory.create(get())
    }

    factory {
        InAppReviewUseCase(
            interactionCounterDataStore = get(),
            inAppReviewDataStore = get(),
            reviewManager = get(),
            isDebug = false
        )
    }

    factory<ReviewManagerWrapper> {
        ReviewManagerWrapperImpl(get())
    }

    viewModel {

        ChooseClockViewModel(
            clockRepository = get(),
            analyticsManager = get(),
            inAppReviewUseCase = get(),
            interactionCounterDataStore = get(),
            prepopulateDataStore = get(),
        )
    }
}