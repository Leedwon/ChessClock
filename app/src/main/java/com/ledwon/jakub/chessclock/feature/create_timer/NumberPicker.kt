package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class NumberPickerState(
    val range: IntRange,
    initValue: Int = range.first
) {

    private val _currentOffset = Animatable(initValue.coerceIn(range).toFloat()).apply {
        updateBounds(range.first.toFloat(), range.last.toFloat())
    }

    val currentOffset: Float
        get() = _currentOffset.value

    val currentValue
        get() = _currentOffset.value.roundToInt()

    suspend fun updateCurrentOffset(newValue: Float) {
        _currentOffset.snapTo(newValue.coerceIn(range.first.toFloat(), range.last.toFloat()))
    }

    suspend fun fling(velocity: Float, onAnimationFinished: ((Int) -> Unit)? = null) {
        _currentOffset.animateDecay(
            initialVelocity = velocity,
            animationSpec = FloatExponentialDecaySpec(frictionMultiplier = 15f).generateDecayAnimationSpec()
        )

        _currentOffset.animateTo(currentValue.toFloat())

        onAnimationFinished?.invoke(currentValue)
    }

}

@Composable
fun NumberPicker(
    state: NumberPickerState,
    modifier: Modifier = Modifier,
    onValueChangedListener: ((Int) -> Unit)? = null
) {
    val cellSize = 64.dp
    val cellSizePx = with(LocalDensity.current) { cellSize.toPx() }

    val scope = LocalLifecycleOwner.current.lifecycleScope

    Column(
        modifier = modifier
            .wrapContentSize(unbounded = true)
            .draggable(
                state = rememberDraggableState { dy ->
                    scope.launch {
                        state.updateCurrentOffset(state.currentOffset + (-dy / cellSizePx))
                    }
                },
                orientation = Orientation.Vertical,
                startDragImmediately = true,
                onDragStopped = { velocity ->
                    state.fling(
                        velocity = -velocity / cellSizePx,
                        onAnimationFinished = onValueChangedListener
                    )
                }
            )
    ) {
        val currentValue = state.currentValue
        val animOffsetPx = currentValue * cellSizePx - state.currentOffset * cellSizePx
        val animOffsetDp = with(LocalDensity.current) { animOffsetPx.toDp() }

        val alpha = (animOffsetPx % cellSizePx) / cellSizePx
        Cell(
            textAlpha = alpha + 0.5f,
            size = cellSize,
            text = (currentValue - 1).takeIf { it in state.range }?.toString() ?: "",
            textOffset = animOffsetDp
        )
        Spacer(
            modifier = Modifier.height(0.5.dp).background(MaterialTheme.colors.onSurface)
                .width(cellSize)
        )
        Cell(size = cellSize, text = currentValue.toString(), textOffset = animOffsetDp)
        Spacer(
            modifier = Modifier.height(0.5.dp).background(MaterialTheme.colors.onSurface)
                .width(cellSize)
        )
        Cell(
            size = cellSize,
            text = (currentValue + 1).takeIf { it in state.range }?.toString() ?: "",
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