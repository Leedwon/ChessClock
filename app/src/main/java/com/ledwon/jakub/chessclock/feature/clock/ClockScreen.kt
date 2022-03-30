package com.ledwon.jakub.chessclock.feature.clock

import android.view.WindowManager
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.clock.model.ClockState
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay
import com.ledwon.jakub.chessclock.feature.clock.widget.*
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.util.LocalWindowProvider
import com.ledwon.jakub.chessclock.util.toDeferrableString
import kotlinx.coroutines.flow.collect

@Composable
fun ClockScreen(actions: NavigationActions, clockViewModel: ClockViewModel) {
    val centerButtonSize = remember { 96.dp }

    val state: ClockViewModel.State by clockViewModel.state.collectAsState(
        ClockViewModel.State(
            playersDisplay = PlayerDisplay.White(
                text = "".toDeferrableString(),
                percentageLeft = 1.0f,
                isActive = false
            ) to PlayerDisplay.Black(
                text = "".toDeferrableString(),
                percentageLeft = 1.0f,
                isActive = false
            ),
            clockState = ClockState.BeforeStarted
        )
    )

    val window = LocalWindowProvider.current

    DisposableEffect(key1 = Unit, effect = {
        window.addFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))
        onDispose {
            window.clearFlags((WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON))
        }
    })

    LaunchedEffect(Unit) {
        clockViewModel.command.collect { command ->
            when (command) {
                is ClockViewModel.Command.NavigateToStats -> actions.openStats(command.moves)
            }
        }
    }

    val clockType = clockViewModel.clockDisplay.collectAsState(ClockDisplay.OwnPlayerTimeClock(180f to 0f))
    val pulsationEnabled = clockViewModel.pulsationEnabled.collectAsState(true)

    val rotations = when (val clockTypeValue = clockType.value) {
        is ClockDisplay.CircleAnimatedClock -> clockTypeValue.rotations
        is ClockDisplay.OwnPlayerTimeClock -> clockTypeValue.rotations
        is ClockDisplay.BothPlayersTimeClock -> 0f to 0f
    }

    Box(contentAlignment = Alignment.Center) {
        val clockEnabled =
            state.clockState != ClockState.Paused && state.clockState != ClockState.RandomizingPositions
        when (clockType.value) {
            is ClockDisplay.OwnPlayerTimeClock -> OwnPlayerTimeClock(
                playersDisplay = state.playersDisplay,
                onClockButtonClick = clockViewModel::clockClicked,
                rotations = rotations,
                pulsationEnabled = pulsationEnabled.value,
                enabled = clockEnabled,
                paddingFromCenter = centerButtonSize / 2
            )
            is ClockDisplay.BothPlayersTimeClock -> BothPlayersTimeClock(
                playersDisplay = state.playersDisplay,
                onClockButtonClick = clockViewModel::clockClicked,
                pulsationEnabled = pulsationEnabled.value,
                enabled = clockEnabled
            )
            is ClockDisplay.CircleAnimatedClock -> CircleAnimatedClock(
                playersDisplay = state.playersDisplay,
                rotations = rotations,
                onClockButtonClick = clockViewModel::clockClicked,
                pulsationEnabled = pulsationEnabled.value,
                enabled = clockEnabled,
                circlePaddingFromCenter = centerButtonSize / 2
            )
        }

        val centerButtonRotations = if (rotations.first == rotations.second) rotations.first else 0f
        when (state.clockState) {
            ClockState.RandomizingPositions -> {
                RotatingDice(
                    modifier = Modifier.size(centerButtonSize),
                    onDiceClick = clockViewModel::cancelRandomization
                )
            }
            ClockState.BeforeStarted -> {
                ClockCenterButton(
                    modifier = Modifier.size(centerButtonSize),
                    onClick = { clockViewModel.swapSides() },
                    icon = painterResource(id = R.drawable.ic_swap_48),
                    iconTint = Color.Green,
                    iconContentDescription = stringResource(id = R.string.swap_sides_content_description)
                )
            }
            ClockState.Running -> {
                ClockCenterButton(
                    modifier = Modifier
                        .size(centerButtonSize)
                        .rotate(centerButtonRotations),
                    onClick = { clockViewModel.pauseClock() },
                    icon = painterResource(id = R.drawable.ic_pause_48),
                    iconTint = Color.Red,
                    iconContentDescription = stringResource(id = R.string.pause_clock_content_description)
                )
            }
            ClockState.Paused -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.resumeClock() },
                        icon = painterResource(id = R.drawable.ic_play_48),
                        iconTint = Color.Green,
                        iconContentDescription = stringResource(id = R.string.resume_clock_content_description)
                    )
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.showStats() },
                        icon = painterResource(id = R.drawable.ic_stats_32),
                        iconTint = Color.Green,
                        iconContentDescription = stringResource(id = R.string.stats_content_description)
                    )
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.restartGame() },
                        icon = painterResource(id = R.drawable.ic_replay_48),
                        iconTint = Color.Green,
                        iconContentDescription = stringResource(id = R.string.restart_clock_content_description)
                    )
                }
            }
            ClockState.Over -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.showStats() },
                        icon = painterResource(id = R.drawable.ic_stats_32),
                        iconTint = Color.Green,
                        iconContentDescription = stringResource(id = R.string.stats_content_description)
                    )
                    ClockCenterButton(
                        modifier = Modifier
                            .size(centerButtonSize)
                            .rotate(centerButtonRotations),
                        onClick = { clockViewModel.restartGame() },
                        icon = painterResource(id = R.drawable.ic_replay_48),
                        iconTint = Color.Green,
                        iconContentDescription = stringResource(id = R.string.restart_clock_content_description)
                    )
                }
            }
        }
    }
}






