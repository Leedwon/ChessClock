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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

//todo don't pass scope, instead make suspend?
class NumberPickerState(
    val coroutineScope: CoroutineScope,
    val range: IntRange,
    private val initValue: Int = range.first
) {

    private val _currentOffset = Animatable(initValue.coerceIn(range).toFloat()).apply {
        updateBounds(range.first.toFloat(), range.last.toFloat())
    }

    var currentOffset: Float
        get() = _currentOffset.value
        set(value) {
            coroutineScope.launch {
                //todo seems hacky - switch to updating bounds of _currentOffset?
                _currentOffset.snapTo(value.coerceIn(range.first.toFloat(), range.last.toFloat()))
            }
        }

    val currentValue
        get() = _currentOffset.value.roundToInt()

    fun fling(velocity: Float, onAnimationFinished: ((Int) -> Unit)? = null) {
        //todo seems hacky?
        coroutineScope.launch {
            _currentOffset.animateDecay(
                initialVelocity = velocity,
                animationSpec = FloatExponentialDecaySpec(frictionMultiplier = 15f).generateDecayAnimationSpec()
            )

            _currentOffset.animateTo(currentValue.toFloat())

            onAnimationFinished?.invoke(currentValue)
        }
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

    Column(
        modifier = modifier
            .wrapContentSize(unbounded = true)
            .draggable(
                state = rememberDraggableState { dy ->
                    state.currentOffset += -dy / cellSizePx
                },
                orientation = Orientation.Vertical,
                //onDrag = { dy -> state.currentOffset += -dy / cellSizePx },
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