package com.ledwon.jakub.chessclock.di.clock

import com.ledwon.jakub.chessclock.feature.clock.*
import com.ledwon.jakub.chessclock.feature.clock.model.ClockInitialData
import com.ledwon.jakub.chessclock.feature.clock.util.PauseClock
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val clockModule = module {
    factory { PauseClock() }

    factory { PositionRandomizer() }
    factory { MovesTracker() }
    factory { parameters ->
        val clockInitialData: ClockInitialData = parameters[0]

        Game(
            white = Player(
                millis = clockInitialData.whiteSeconds * 1_000L,
                incrementMillis = clockInitialData.whiteIncrementSeconds * 1_000L
            ),
            black = Player(
                millis = clockInitialData.blackSeconds * 1_000L,
                incrementMillis = clockInitialData.blackIncrementSeconds * 1_000L
            ),
            movesTracker = get(),
            defaultDispatcher = Dispatchers.Default
        )
    }

    viewModel { parameters ->
        val clockInitialData: ClockInitialData = parameters[0]
        ClockViewModel(
            settingsRepository = get(),
            analyticsManager = get(),
            positionRandomizer = get(),
            game = get(parameters = { parametersOf(clockInitialData) })
        )
    }
}