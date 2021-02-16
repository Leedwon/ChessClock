package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.navigation.Actions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.ui.widgets.OutlinePrimaryButton
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class NumberPickerStates(
    val whiteHoursState: NumberPickerState,
    val whiteMinutesState: NumberPickerState,
    val whiteSecondsState: NumberPickerState,
    val whiteIncrementState: NumberPickerState,
    val blackHoursState: NumberPickerState,
    val blackMinutesState: NumberPickerState,
    val blackSecondsState: NumberPickerState,
    val blackIncrementState: NumberPickerState,
)

@Composable
fun CreateTimerScreen(actions: Actions, createTimerViewModel: CreateTimerViewModel) {

    val hoursRange = 0..23
    val minutesRange = 0..59
    val secondsRange = 0..59
    val incrementRange = 0..600

    createTimerViewModel.command.observe(AmbientLifecycleOwner.current, {
        when (it) {
            is CreateTimerViewModel.Command.NavigateToClock -> {
                actions.openClock(
                    OpenClockPayload(
                        whiteClock = it.state.whiteClock,
                        blackCLock = it.state.blackClock
                    )
                )
            }
            is CreateTimerViewModel.Command.NavigateBack -> {
                actions.navigateBack()
            }
            is CreateTimerViewModel.Command.Noop -> {
                //noop
            }
        }
    })

    val clock = AmbientAnimationClock.current

    val state = remember {
        NumberPickerStates(
            whiteHoursState = NumberPickerState(
                clock = clock,
                range = hoursRange
            ),
            whiteMinutesState = NumberPickerState(
                clock = clock,
                range = minutesRange
            ),
            whiteSecondsState = NumberPickerState(
                clock = clock,
                range = minutesRange
            ),
            whiteIncrementState = NumberPickerState(
                clock = clock,
                range = incrementRange
            ),
            blackHoursState = NumberPickerState(
                clock = clock,
                range = hoursRange
            ),
            blackMinutesState = NumberPickerState(
                clock = clock,
                range = minutesRange
            ),
            blackSecondsState = NumberPickerState(
                clock = clock,
                range = secondsRange
            ),
            blackIncrementState = NumberPickerState(
                clock = clock,
                range = incrementRange
            ),
        )
    }

    AmbientLifecycleOwner.current.lifecycleScope.launch {
        createTimerViewModel.state.collect {
            state.blackHoursState.currentOffset = it.blackClock.hours.toFloat()
            state.blackMinutesState.currentOffset = it.blackClock.minutes.toFloat()
            state.blackSecondsState.currentOffset = it.blackClock.seconds.toFloat()
            state.blackIncrementState.currentOffset = it.blackClock.increment.toFloat()
            state.whiteHoursState.currentOffset = it.whiteClock.hours.toFloat()
            state.whiteMinutesState.currentOffset = it.whiteClock.minutes.toFloat()
            state.whiteSecondsState.currentOffset = it.whiteClock.seconds.toFloat()
            state.whiteIncrementState.currentOffset = it.whiteClock.increment.toFloat()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create timer") },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = createTimerViewModel::onBackClick) {
                        Icon(painter = backIcon, contentDescription = "navigate back")
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Text(text = "White's timer: ", fontSize = 24.sp) }
            item {
                ClockPicker(
                    clockPickerData = ClockPickerData(
                        hoursPickerState = state.whiteHoursState,
                        minutesPickerState = state.whiteMinutesState,
                        secondsPickerState = state.whiteSecondsState,
                        incrementPickerState = state.whiteIncrementState
                    ),
                    onHoursChanged = createTimerViewModel::onWhiteHoursChanged,
                    onMinutesChanged = createTimerViewModel::onWhiteMinutesChanged,
                    onSecondsChanged = createTimerViewModel::onWhiteSecondsChanged,
                    onIncrementChanged = createTimerViewModel::onWhiteIncrementChanged
                )
            }
            item { Text(text = "Black's timer: ", fontSize = 24.sp) }
            item {
                ClockPicker(
                    clockPickerData = ClockPickerData(
                        hoursPickerState = state.blackHoursState,
                        minutesPickerState = state.blackMinutesState,
                        secondsPickerState = state.blackSecondsState,
                        incrementPickerState = state.blackIncrementState
                    ),
                    onHoursChanged = createTimerViewModel::onBlackHoursChanged,
                    onMinutesChanged = createTimerViewModel::onBlackMinutesChanged,
                    onSecondsChanged = createTimerViewModel::onBlackSecondsChanged,
                    onIncrementChanged = createTimerViewModel::onBlackIncrementChanged

                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinePrimaryButton(onClick = createTimerViewModel::onStartGameClick) {
                        Text("Start", fontSize = 19.sp)
                    }
                    OutlinePrimaryButton(
                        onClick = createTimerViewModel::onStartGameAndSaveTimerClick
                    ) {
                        Text("Start & save", fontSize = 19.sp)
                    }
                    OutlinePrimaryButton(onClick = createTimerViewModel::onSaveTimerClick) {
                        Text("Save", fontSize = 19.sp)
                    }
                }
            }
        }
    }

}

data class ClockPickerData(
    val hoursPickerState: NumberPickerState,
    val minutesPickerState: NumberPickerState,
    val secondsPickerState: NumberPickerState,
    val incrementPickerState: NumberPickerState
)

@Composable
fun ClockPicker(
    clockPickerData: ClockPickerData,
    onHoursChanged: ((Int) -> Unit)? = null,
    onMinutesChanged: ((Int) -> Unit)? = null,
    onSecondsChanged: ((Int) -> Unit)? = null,
    onIncrementChanged: ((Int) -> Unit)? = null
) {
    Row {
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.hoursPickerState,
            text = "Hours",
            onValueChangedListener = onHoursChanged
        )
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.minutesPickerState,
            text = "Minutes",
            onValueChangedListener = onMinutesChanged
        )
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.secondsPickerState,
            text = "Seconds",
            onValueChangedListener = onSecondsChanged
        )
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.incrementPickerState,
            text = "Increment (s)",
            onValueChangedListener = onIncrementChanged
        )
    }

}

@Composable
fun TimerPickerWithDescription(
    text: String,
    numberPickerState: NumberPickerState,
    onValueChangedListener: ((Int) -> Unit)? = null,
    backgroundColor: Color? = null
) {
    val modifier = if (backgroundColor != null) {
        Modifier.padding(8.dp).background(color = backgroundColor)
    } else {
        Modifier.padding(8.dp)
    }

    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text)
        NumberPicker(
            modifier = modifier,
            state = numberPickerState,
            onValueChangedListener = onValueChangedListener
        )
    }
}

