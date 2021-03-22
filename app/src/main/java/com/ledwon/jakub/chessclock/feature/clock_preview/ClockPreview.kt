package com.ledwon.jakub.chessclock.feature.clock_preview

import androidx.compose.animation.core.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay
import com.ledwon.jakub.chessclock.feature.clock.widget.BothPlayersTimeClock
import com.ledwon.jakub.chessclock.feature.clock.widget.CircleAnimatedClock
import com.ledwon.jakub.chessclock.feature.clock.widget.OwnPlayerTimeClock
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.feature.common.exhaustive
import com.ledwon.jakub.chessclock.navigation.Actions
import kotlinx.coroutines.launch

@Composable
fun ClockPreviewScreen(actions: Actions, clockPreviewViewModel: ClockPreviewViewModel) {
    val clockType = clockPreviewViewModel.clockType

    clockPreviewViewModel.command.observe(LocalLifecycleOwner.current) {
        when (it) {
            is ClockPreviewViewModel.Command.NavigateBack -> actions.navigateBack()
        }.exhaustive
    }

    val pulsationEnabled = clockPreviewViewModel.pulsationEnabled.collectAsState()
    val pulsateOnOrOff = if (pulsationEnabled.value) "off" else "on"

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Clock preview") },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = clockPreviewViewModel::onBackClick) {
                        Icon(painter = backIcon, contentDescription = "navigate back")
                    }
                }
            )
        }
    ) {
        if (clockType == null) {
            return@Scaffold
        }

        val durationMillis = remember { 1000 * 60 }
        val infiniteTransition = rememberInfiniteTransition()

        val testPercentageAnimation = infiniteTransition.animateFloat(
            initialValue = durationMillis.toFloat(),
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )


        val playersDisplay =
            PlayerDisplay.White(
                text = "White's time",
                percentageLeft = testPercentageAnimation.value / durationMillis,
                isActive = true
            ) to PlayerDisplay.Black(
                text = "Black's time",
                percentageLeft = 1f,
                isActive = false
            )

        when (clockType.display) {
            is ClockDisplay.OwnPlayerTimeClock -> {
                OwnPlayerTimeClock(
                    playersDisplay = playersDisplay,
                    rotations = clockType.display.rotations,
                    onClockButtonClick = { /* no-op */ },
                    pulsationEnabled = pulsationEnabled.value
                )
            }
            is ClockDisplay.BothPlayersTimeClock -> {
                BothPlayersTimeClock(
                    playersDisplay = playersDisplay,
                    onClockButtonClick = { /* no-op */ },
                    pulsationEnabled = pulsationEnabled.value
                )
            }
            is ClockDisplay.CircleAnimatedClock -> {
                CircleAnimatedClock(
                    playersDisplay = playersDisplay,
                    rotations = clockType.display.rotations,
                    onClockButtonClick = { /* no-op */ },
                    pulsationEnabled = pulsationEnabled.value
                )
            }
        }.exhaustive

        LocalLifecycleOwner.current.lifecycleScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "You can turn $pulsateOnOrOff active player's pulsation in settings",
                duration = SnackbarDuration.Long,
            )
        }
    }
}