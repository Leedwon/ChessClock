package com.ledwon.jakub.chessclock.feature.clock.widget

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ledwon.jakub.chessclock.R

@SuppressLint("Range")
@Composable
fun RotatingDice() {
    val infiniteTransition = rememberInfiniteTransition()

    val diceIndex = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )

    val images = listOf(
        painterResource(R.drawable.ic_dice_one_48),
        painterResource(R.drawable.ic_dice_two_48),
        painterResource(R.drawable.ic_dice_three_48),
        painterResource(R.drawable.ic_dice_four_48),
        painterResource(R.drawable.ic_dice_five_48),
        painterResource(R.drawable.ic_dice_six_48),
    )

    Row(
        modifier = Modifier.height(96.dp).width(96.dp).clip(CircleShape).background(Color.Black)
            .border(width = 2.dp, color = Color.White, shape = CircleShape),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.rotate(rotation.value),
            contentDescription = null,
            painter = images[diceIndex.value.toInt()],
            tint = Color.Green
        )
    }
}