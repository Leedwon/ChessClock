package com.example.chessclock.feature.clock.widget

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.animation.transition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.chessclock.R

val rotation = FloatPropKey(label = "randomizing")
val diceNumber = IntPropKey(label = "dice")

enum class RollingState {
    START, END
}


@SuppressLint("Range")
@Composable
fun RotatingDice() {
    val definition = transitionDefinition<RollingState> {
        state(RollingState.START) {
            this[rotation] = 0f
            this[diceNumber] = 0
        }
        state(RollingState.END) {
            this[rotation] = 360f
            this[diceNumber] = 0
        }


        transition(fromState = RollingState.START, toState = RollingState.END) {
            rotation using infiniteRepeatable(tween(durationMillis = 1000))
            diceNumber using infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 3000
                    for (i in 0..6) {
                        i at i * durationMillis / 6
                    }
                }
            )
        }
    }

    val state = transition(
        definition = definition,
        initState = RollingState.START,
        toState = RollingState.END
    )
    Dice(state = state)
}

@Composable
fun Dice(state: TransitionState) {
    val images = listOf(
        androidx.compose.ui.res.loadVectorResource(R.drawable.ic_dice_one_48),
        androidx.compose.ui.res.loadVectorResource(R.drawable.ic_dice_two_48),
        androidx.compose.ui.res.loadVectorResource(R.drawable.ic_dice_three_48),
        androidx.compose.ui.res.loadVectorResource(R.drawable.ic_dice_four_48),
        androidx.compose.ui.res.loadVectorResource(R.drawable.ic_dice_five_48),
        androidx.compose.ui.res.loadVectorResource(R.drawable.ic_dice_six_48),
    )

    Row(
        modifier = Modifier.height(96.dp).width(96.dp).clip(CircleShape).background(Color.Black)
            .border(width = 2.dp, color = Color.White, shape = CircleShape),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        images[state[diceNumber]].resource.resource?.let {
            Icon(
                modifier = Modifier.rotate(state[rotation]),
                imageVector = it,
                tint = Color.Green
            )
        }
    }
}