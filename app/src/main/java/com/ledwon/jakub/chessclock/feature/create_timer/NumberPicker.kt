package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.animation.core.fling
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun NumberPicker(
    range: IntRange,
    modifier: Modifier = Modifier,
    onValueChangeListener: (Int) -> Unit = {}
) {
    val cellHeight = 64.dp
    val cellHeightPx = with(AmbientDensity.current) { cellHeight.toPx() }

    val offset = animatedFloat(initVal = 0f).apply {
        setBounds(
            range.first.toFloat() * cellHeightPx,
            range.last.toFloat() * cellHeightPx
        )
    }

    fun currentValue(offset: Float): Int = (offset / cellHeightPx).roundToInt()

    Column(
        modifier = modifier.draggable(
            orientation = Orientation.Vertical,
            onDrag = { dy ->
                offset.snapTo(offset.value - dy)
                onValueChangeListener(currentValue(offset.value))
            },
            onDragStopped = { velocity ->
                offset.fling(
                    -velocity,
                    decay = FloatExponentialDecaySpec(
                        frictionMultiplier = 5f
                    ),
                    adjustTarget = { target ->
                        val valueForTarget = currentValue(target)
                        val actualTarget = valueForTarget * cellHeightPx
                        onValueChangeListener(currentValue(actualTarget))
                        TargetAnimation(actualTarget)
                    }
                )
            }
        )
    ) {
        val currentValue = currentValue(offset.value)
        val animOffsetPx = currentValue * cellHeightPx - offset.value
        val animOffsetDp = with(AmbientDensity.current) { animOffsetPx.toDp() }

        val alpha = (animOffsetPx % cellHeightPx) / cellHeightPx
        Cell(
            textAlpha = alpha + 0.5f,
            size = cellHeight,
            text = (currentValue - 1).takeIf { it in range }?.toString() ?: "",
            textOffset = animOffsetDp
        )
        Cell(size = cellHeight, text = currentValue.toString(), textOffset = animOffsetDp)
        Cell(
            size = cellHeight,
            text = (currentValue + 1).takeIf { it in range }?.toString() ?: "",
            textOffset = animOffsetDp,
            textAlpha = 0.5f - alpha
        )
    }
}

@Composable
fun Cell(
    size: Dp,
    text: String,
    modifier: Modifier = Modifier,
    textOffset: Dp = 0.dp,
    textAlpha: Float = 1f
) {
    Box(
        modifier = modifier.height(size).width(size),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.offset(y = textOffset).alpha(textAlpha),
            text = text,
            fontSize = 18.sp
        )
    }
}