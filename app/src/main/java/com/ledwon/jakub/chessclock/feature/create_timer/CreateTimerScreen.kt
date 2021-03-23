package com.ledwon.jakub.chessclock.feature.create_timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
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

    createTimerViewModel.command.observe(LocalLifecycleOwner.current, {
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

    val state = remember {
        NumberPickerStates(
            whiteHoursState = NumberPickerState(range = hoursRange),
            whiteMinutesState = NumberPickerState(range = minutesRange),
            whiteSecondsState = NumberPickerState(range = minutesRange),
            whiteIncrementState = NumberPickerState(range = incrementRange),
            blackHoursState = NumberPickerState(range = hoursRange),
            blackMinutesState = NumberPickerState(range = minutesRange),
            blackSecondsState = NumberPickerState(range = secondsRange),
            blackIncrementState = NumberPickerState(range = incrementRange),
        )
    }

    LocalLifecycleOwner.current.lifecycleScope.launch {
        createTimerViewModel.state.collect {
            state.blackHoursState.updateCurrentOffset(it.blackClock.hours.toFloat())
            state.blackMinutesState.updateCurrentOffset(it.blackClock.minutes.toFloat())
            state.blackSecondsState.updateCurrentOffset(it.blackClock.seconds.toFloat())
            state.blackIncrementState.updateCurrentOffset(it.blackClock.increment.toFloat())
            state.whiteHoursState.updateCurrentOffset(it.whiteClock.hours.toFloat())
            state.whiteMinutesState.updateCurrentOffset(it.whiteClock.minutes.toFloat())
            state.whiteSecondsState.updateCurrentOffset(it.whiteClock.seconds.toFloat())
            state.whiteIncrementState.updateCurrentOffset(it.whiteClock.increment.toFloat())
        }
    }

    val timersMerged = createTimerViewModel.timersMerged.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create clock") },
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
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "Same clock for white and black"
                    )
                    Switch(
                        modifier = Modifier.width(32.dp).height(32.dp),
                        checked = timersMerged.value,
                        onCheckedChange = createTimerViewModel::onTimersMergeClick
                    )
                }
            }
            item { Text(text = if (timersMerged.value) "Clock: " else "White's clock: ", fontSize = 24.sp) }
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
            if (!timersMerged.value) {
                item { Text(text = "Black's clock: ", fontSize = 24.sp) }
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

