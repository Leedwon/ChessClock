package com.ledwon.jakub.chessclock.feature.create_clock

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.ui.widgets.NumberPicker
import com.ledwon.jakub.chessclock.ui.widgets.NumberPickerState
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
fun CreateClockScreen(navigationActions: NavigationActions, createClockViewModel: CreateClockViewModel) {

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
            createClockViewModel.state.collect {
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
        createClockViewModel.command.observe(lifecycleObserver, {
            when (it) {
                is CreateClockViewModel.Command.NavigateToClock -> {
                    navigationActions.openClock(
                        OpenClockPayload(
                            whiteClock = it.state.whiteClock,
                            blackCLock = it.state.blackClock
                        )
                    )
                }

                is CreateClockViewModel.Command.NavigateBack -> {
                    navigationActions.navigateBack()
                }

                is CreateClockViewModel.Command.Noop -> {
                    //noop
                }
            }
        })
    }

    val clocksMerged = createClockViewModel.clocksMerged.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.create_clock_title)) },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = createClockViewModel::onBackClick) {
                        Icon(painter = backIcon, contentDescription = stringResource(R.string.navigate_back_content_description))
                    }
                }
            )
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
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
                        text = stringResource(R.string.create_clock_same_clock)
                    )
                    Switch(
                        modifier = Modifier
                            .width(32.dp)
                            .height(32.dp),
                        checked = clocksMerged.value,
                        onCheckedChange = createClockViewModel::onClocksMergeClick
                    )
                }
            }
            item {
                Text(
                    text = if (clocksMerged.value) stringResource(R.string.create_clock_for_both_players) else stringResource(R.string.create_clock_white),
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
                    onHoursChanged = createClockViewModel::onWhiteHoursChanged,
                    onMinutesChanged = createClockViewModel::onWhiteMinutesChanged,
                    onSecondsChanged = createClockViewModel::onWhiteSecondsChanged,
                    onIncrementChanged = createClockViewModel::onWhiteIncrementChanged
                )
            }
            if (!clocksMerged.value) {
                item { Text(text = stringResource(R.string.create_clock_black), fontSize = 24.sp) }
                item {
                    ClockPicker(
                        clockPickerData = ClockPickerData(
                            hoursPickerState = state.blackHoursState,
                            minutesPickerState = state.blackMinutesState,
                            secondsPickerState = state.blackSecondsState,
                            incrementPickerState = state.blackIncrementState
                        ),
                        onHoursChanged = createClockViewModel::onBlackHoursChanged,
                        onMinutesChanged = createClockViewModel::onBlackMinutesChanged,
                        onSecondsChanged = createClockViewModel::onBlackSecondsChanged,
                        onIncrementChanged = createClockViewModel::onBlackIncrementChanged

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
                    OutlinePrimaryButton(onClick = createClockViewModel::onStartGameClick) {
                        Text(stringResource(R.string.start), fontSize = 19.sp)
                    }
                    OutlinePrimaryButton(
                        onClick = createClockViewModel::onStartGameAndSaveClockClick
                    ) {
                        Text(stringResource(R.string.start_and_save), fontSize = 19.sp)
                    }
                    OutlinePrimaryButton(onClick = createClockViewModel::onSaveClockClick) {
                        Text(stringResource(R.string.save), fontSize = 19.sp)
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
        TimePickerWithDescription(
            numberPickerState = clockPickerData.hoursPickerState,
            text = stringResource(R.string.hours),
            onValueChangedListener = onHoursChanged
        )
        TimePickerWithDescription(
            numberPickerState = clockPickerData.minutesPickerState,
            text = stringResource(R.string.minutes),
            onValueChangedListener = onMinutesChanged
        )
        TimePickerWithDescription(
            numberPickerState = clockPickerData.secondsPickerState,
            text = stringResource(R.string.seconds),
            onValueChangedListener = onSecondsChanged
        )
        TimePickerWithDescription(
            numberPickerState = clockPickerData.incrementPickerState,
            text = stringResource(R.string.increment),
            onValueChangedListener = onIncrementChanged
        )
    }

}

@Composable
fun TimePickerWithDescription(
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

