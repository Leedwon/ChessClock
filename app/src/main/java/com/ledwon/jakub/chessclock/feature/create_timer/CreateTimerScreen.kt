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
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.ui.widgets.OutlinePrimaryButton
import com.ledwon.jakub.chessclock.util.getString
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
fun CreateTimerScreen(navigationActions: NavigationActions, createTimerViewModel: CreateTimerViewModel) {

    val hoursRange = 0..23
    val minutesRange = 0..59
    val secondsRange = 0..59
    val incrementRange = 0..600

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

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
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
    }

    val lifecycleObserver = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        createTimerViewModel.command.observe(lifecycleObserver, {
            when (it) {
                is CreateTimerViewModel.Command.NavigateToClock -> {
                    navigationActions.openClock(
                        OpenClockPayload(
                            whiteClock = it.state.whiteClock,
                            blackCLock = it.state.blackClock
                        )
                    )
                }
                is CreateTimerViewModel.Command.NavigateBack -> {
                    navigationActions.navigateBack()
                }
                is CreateTimerViewModel.Command.Noop -> {
                    //noop
                }
            }
        })
    }

    val timersMerged = createTimerViewModel.timersMerged.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = getString(R.string.create_clock_title)) },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = createTimerViewModel::onBackClick) {
                        Icon(painter = backIcon, contentDescription = getString(R.string.navigate_back_content_description))
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = getString(R.string.create_clock_same_clock)
                    )
                    Switch(
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp),
                        checked = timersMerged.value,
                        onCheckedChange = createTimerViewModel::onTimersMergeClick
                    )
                }
            }
            item {
                Text(
                    text = if (timersMerged.value) getString(R.string.create_clock_for_both_players) else getString(R.string.create_clock_white),
                    fontSize = 24.sp
                )
            }
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
                item { Text(text = getString(R.string.create_clock_black), fontSize = 24.sp) }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinePrimaryButton(onClick = createTimerViewModel::onStartGameClick) {
                        Text(getString(R.string.start), fontSize = 19.sp)
                    }
                    OutlinePrimaryButton(
                        onClick = createTimerViewModel::onStartGameAndSaveTimerClick
                    ) {
                        Text(getString(R.string.start_and_save), fontSize = 19.sp)
                    }
                    OutlinePrimaryButton(onClick = createTimerViewModel::onSaveTimerClick) {
                        Text(getString(R.string.save), fontSize = 19.sp)
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
            text = getString(R.string.hours),
            onValueChangedListener = onHoursChanged
        )
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.minutesPickerState,
            text = getString(R.string.minutes),
            onValueChangedListener = onMinutesChanged
        )
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.secondsPickerState,
            text = getString(R.string.seconds),
            onValueChangedListener = onSecondsChanged
        )
        TimerPickerWithDescription(
            numberPickerState = clockPickerData.incrementPickerState,
            text = getString(R.string.increment),
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
        Modifier
            .padding(8.dp)
            .background(color = backgroundColor)
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

