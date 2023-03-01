package com.ledwon.jakub.chessclock.feature.clock

import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.abs
import kotlin.random.Random

class PositionRandomizer(
    private val randomNumberProvider: () -> Int = { Random.nextInt() },
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    fun randomizePositions(minRandomRounds: Int = 9): Flow<Pair<PlayerColor, PlayerColor>> = flow {
        var value = PlayerColor.White to PlayerColor.Black
        val rand = randomNumberProvider()
        val rounds = (abs(rand % 2) + minRandomRounds)

        emit(value)
        var currentRound = 0
        repeat(rounds) {
            currentRound++

            val delayValue = if (currentRound <= rounds - 3) 250L else currentRound * 75L
            delay(delayValue)

            value = value.swap()
            emit(value)
        }
    }.flowOn(defaultDispatcher)

    private fun <F, S> Pair<F, S>.swap(): Pair<S, F> = this.second to this.first
}
