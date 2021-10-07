package com.ledwon.jakub.chessclock.feature.clock_preview

import androidx.compose.animation.core.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay
import com.ledwon.jakub.chessclock.feature.clock.widget.BothPlayersTimeClock
import com.ledwon.jakub.chessclock.feature.clock.widget.CircleAnimatedClock
import com.ledwon.jakub.chessclock.feature.clock.widget.OwnPlayerTimeClock
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.feature.common.exhaustive
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.util.getString
import kotlinx.coroutines.launch

@Composable
fun ClockPreviewScreen(navigationActions: NavigationActions, clockPreviewViewModel: ClockPreviewViewModel) {
    val clockType = clockPreviewViewModel.clockType

    val lifecycleObserver = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        clockPreviewViewModel.command.observe(lifecycleObserver) {
            when (it) {
                is ClockPreviewViewModel.Command.NavigateBack -> navigationActions.navigateBack()
            }.exhaustive
        }
    }

    val pulsationEnabled = clockPreviewViewModel.pulsationEnabled.collectAsState()

    val turnPulsationOnText = getString(R.string.turn_pulsation_on)
    val turnPulsationOffText = getString(R.string.turn_pulsation_off)

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(getString(R.string.clock_preview_title)) },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = clockPreviewViewModel::onBackClick) {
                        Icon(painter = backIcon, contentDescription = getString(R.string.navigate_back_content_description))
                    }
                }
            )
        }
    ) {
        if (clockType == null) {
            return@Scaffold
        }

        val durationMillis = remember { 1000 * 45 } //todo change to 60 when compose version is >= beta02
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
                text = getString(R.string.white_time),
                percentageLeft = testPercentageAnimation.value / durationMillis,
                isActive = true
            ) to PlayerDisplay.Black(
                text = getString(R.string.black_time),
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
                message = if (pulsationEnabled.value) turnPulsationOffText else turnPulsationOnText,
                duration = SnackbarDuration.Indefinite,
            )
        }
    }
}