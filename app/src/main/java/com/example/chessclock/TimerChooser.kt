package com.example.chessclock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.unit.dp
import com.example.chessclock.navigation.Actions

@Composable
fun TimerChooser(actions: Actions, timerViewModel: TimerViewModel) {

    val state: TimerViewModel.State by timerViewModel.state.observeAsState(
        initial = TimerViewModel.State(
            10f,
            10f
        )
    )

    timerViewModel.command.observe(AmbientLifecycleOwner.current, {
        when (it) {
            is TimerViewModel.Command.NavigateToClock -> actions.openClock(it.state)
            else -> {}
        }
    })

    Column {
        SliderWithDescription(
            modifier = Modifier.padding(all = 16.dp),
            description = "White's time",
            value = state.whiteTime,
            valueRange = 1f..60f,
            onValueChange = timerViewModel::onWhiteTimeChanged
        )
        SliderWithDescription(
            modifier = Modifier.padding(all = 16.dp),
            description = "Black's time",
            value = state.blackTime,
            valueRange = 1f..60f,
            onValueChange = timerViewModel::onBlackTimeChanged
        )
        Button(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            onClick = timerViewModel::onPlayClicked
        ) {
            Text("Start game")
        }
    }
}

@Composable
fun SliderWithDescription(
    modifier: Modifier = Modifier,
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    steps: Int = 0
) {
    WithConstraints {
        Column(modifier) {
            val textPadding = (value / valueRange.endInclusive) * constraints.maxWidth
            val textPaddingDp = with(AmbientDensity.current) { textPadding.toDp() }

            Text(modifier = Modifier.padding(horizontal = 8.dp), text = description)
            Slider(
                value = value,
                valueRange = valueRange,
                onValueChange = onValueChange,
                steps = steps
            )
            Text(
                modifier = Modifier.padding(start = textPaddingDp),
                text = value.toInt().toString()
            )
        }
    }


}