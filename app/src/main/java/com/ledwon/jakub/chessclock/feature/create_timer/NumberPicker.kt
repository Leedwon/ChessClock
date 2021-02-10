package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.compose.animation.animatedFloat
import androidx.compose.animation.core.FloatExponentialDecaySpec
import androidx.compose.animation.core.TargetAnimation
import androidx.compose.animation.core.fling
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
import kotlin.math.roundToInt

//todo refactor listener mechanism - it spams events now
@Composable
fun NumberPicker(
    range: IntRange,
    modifier: Modifier = Modifier,
    initialValue: Int = range.first,
    onValueChangedListener: (Int) -> Unit = {}
) {
    val cellSize = 64.dp
    val cellSizePx = with(AmbientDensity.current) { cellSize.toPx() }

    val offset = animatedFloat(initVal = initialValue * cellSizePx).apply {
        setBounds(
            range.first.toFloat() * cellSizePx,
            range.last.toFloat() * cellSizePx
        )
    }

    fun currentValue(offset: Float): Int = (offset / cellSizePx).roundToInt()

    Column(
        modifier = modifier.draggable(
            orientation = Orientation.Vertical,
            onDrag = { dy ->
                offset.snapTo(offset.value - dy)
                onValueChangedListener(currentValue(offset.value))
            },
            onDragStopped = { velocity ->
                offset.fling(
                    -velocity,
                    decay = FloatExponentialDecaySpec(
                        frictionMultiplier = 5f
                    ),
                    adjustTarget = { target ->
                        val valueForTarget = currentValue(target)
                        val actualTarget = valueForTarget * cellSizePx
                        onValueChangedListener(currentValue(actualTarget))
                        TargetAnimation(actualTarget)
                    }
                )
            }
        )
    ) {
        val currentValue = currentValue(offset.value)
        val animOffsetPx = currentValue * cellSizePx - offset.value
        val animOffsetDp = with(AmbientDensity.current) { animOffsetPx.toDp() }

        val alpha = (animOffsetPx % cellSizePx) / cellSizePx
        Cell(
            textAlpha = alpha + 0.5f,
            size = cellSize,
            text = (currentValue - 1).takeIf { it in range }?.toString() ?: "",
            textOffset = animOffsetDp
        )
        Spacer(modifier = Modifier.height(0.5.dp).background(Color.Black).width(cellSize))
        Cell(size = cellSize, text = currentValue.toString(), textOffset = animOffsetDp)
        Spacer(modifier = Modifier.height(0.5.dp).background(Color.Black).width(cellSize))
        Cell(
            size = cellSize,
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