package com.ledwon.jakub.chessclock.feature.choose_timer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.ui.widgets.OutlinePrimaryButton

@ExperimentalFoundationApi
@Composable
fun ChooseTimerScreen(navigationActions: NavigationActions, chooseTimerViewModel: ChooseTimerViewModel) {

    val chooseTimerState: ChooseTimerState by chooseTimerViewModel.chooseTimerState.observeAsState(
        initial = ChooseTimerState(
            isSelectableModeOn = false,
            timersToSelected = emptyMap()
        )
    )

    chooseTimerViewModel.command.observe(LocalLifecycleOwner.current, {
        if (it == null) {
            return@observe
        }
        when (it) {
            is ChooseTimerViewModel.Command.NavigateToClock -> navigationActions.openClock(
                OpenClockPayload(
                    whiteClock = it.timer.whiteClockTime,
                    blackCLock = it.timer.blackClockTime,
                )
            )
            is ChooseTimerViewModel.Command.NavigateToCreateTimer -> navigationActions.openCreateTimer()
            is ChooseTimerViewModel.Command.NavigateToSettings -> navigationActions.openSettings()
        }
    })

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chess clock") },
                actions = {
                    val icon = painterResource(id = R.drawable.ic_settings_24)
                    IconButton(onClick = chooseTimerViewModel::onOpenSettingsClicked) {
                        Icon(painter = icon, contentDescription = "settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        ConstraintLayout(modifier = Modifier
            .fillMaxHeight()
            .padding(paddingValues)) {

            val (column, removeSelectedButton) = createRefs()
            //this padding is a hack for bottom.linkTo(removeSelectedButton.top) causing Create new timer button to move up and be below TopAppBar
            LazyColumn(
                modifier = Modifier
                    .padding(
                        bottom = if (chooseTimerState.isSelectableModeOn) 48.dp else
                            0.dp
                    )
                    .constrainAs(column) {
                        top.linkTo(parent.top)
                    }, content = {
                    item {
                        OutlinePrimaryButton(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            onClick = chooseTimerViewModel::onCreateTimerClicked
                        ) {
                            Text("Create new clock", fontSize = 21.sp)
                        }
                    }

                    items(chooseTimerState.timersToSelected.toList()) { (timer, selected) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (chooseTimerState.isSelectableModeOn) {
                                RadioButton(
                                    modifier = Modifier.padding(start = 8.dp),
                                    selected = selected,
                                    onClick = { chooseTimerViewModel.onSelectTimerClick(timer) }
                                )
                            }
                            TimeCard(
                                modifier = Modifier
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            if (chooseTimerState.isSelectableModeOn) {
                                                chooseTimerViewModel.onSelectTimerClick(timer)
                                            } else {
                                                chooseTimerViewModel.onTimerClicked(timer)
                                            }
                                        },
                                        onLongClick = chooseTimerViewModel::onTimerLongClicked
                                    ),
                                timer = timer,
                                onStarClicked = chooseTimerViewModel::onStarClicked
                            )
                        }
                    }
                })
            if (chooseTimerState.isSelectableModeOn) {
                Button(
                    elevation = ButtonDefaults.elevation(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primaryVariant,
                        contentColor = MaterialTheme.colors.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(
                            removeSelectedButton
                        ) {
                            bottom.linkTo(parent.bottom)
                        },
                    onClick = chooseTimerViewModel::onRemoveTimers
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 8.dp),
                        text = "Delete selected",
                        fontSize = 18.sp
                    )
                }
            }
        }

    }
}

@Composable
fun TimeCard(
    timer: Timer,
    modifier: Modifier = Modifier,
    onStarClicked: (Timer) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 6.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {

        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = timer.description,
                color = MaterialTheme.colors.onSurface,
                fontSize = 19.sp
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClockIconsColumn(clockTime = timer.whiteClockTime)
                ClockIconsColumn(clockTime = timer.blackClockTime, isWhite = false)
                val star = painterResource(id = R.drawable.ic_star_24)
                val starOutline = painterResource(id = R.drawable.ic_star_border_24)
                IconButton(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp)
                        .align(alignment = Alignment.CenterVertically),
                    onClick = { onStarClicked(timer) }) {
                    Icon(
                        painter = if (timer.isFavourite) star else starOutline,
                        contentDescription = "is favourite timer",
                        tint = Color.Yellow
                    )
                }
            }
        }
    }
}

@Composable
fun ClockIconsColumn(clockTime: ClockTime, modifier: Modifier = Modifier, isWhite: Boolean = true) {
    Column(modifier = modifier.padding(top = 4.dp)) {
        Row(modifier = Modifier.defaultMinSize(minHeight = 24.dp)) {
            val clockImage = painterResource(id = R.drawable.ic_clock_24)
            Image(
                modifier = Modifier.padding(end = 8.dp),
                painter = clockImage,
                contentDescription = "clock",
                colorFilter = ColorFilter.tint(if (isWhite) Color.White else Color.Black)
            )
            Text(
                text = clockTime.toString(),
                color = MaterialTheme.colors.onSurface,
                fontSize = 17.sp
            )
        }
        clockTime.increment.takeIf { it > 0 }?.let {
            Row {
                val incrementClock = ClockTime(
                    seconds = it
                )
                val incrementImage = painterResource(id = R.drawable.ic_arrow_circle_up_24)
                Image(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = incrementImage,
                    contentDescription = "increment per move",
                    colorFilter = ColorFilter.tint(if (isWhite) Color.White else Color.Black)
                )
                Text(
                    text = incrementClock.toString(),
                    fontSize = 17.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }

}
