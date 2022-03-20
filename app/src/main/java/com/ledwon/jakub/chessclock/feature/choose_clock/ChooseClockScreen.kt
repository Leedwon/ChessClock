package com.ledwon.jakub.chessclock.feature.choose_clock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.ui.widgets.OutlinePrimaryButton
import com.ledwon.jakub.chessclock.util.ClockNameProvider.obtainDeferrableName
import com.ledwon.jakub.chessclock.util.getString
import com.ledwon.jakub.chessclock.util.showInAppReviewIfPossible
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChooseClockScreen(navigationActions: NavigationActions, chooseClockViewModel: ChooseClockViewModel) {

    val chooseClockState = chooseClockViewModel.chooseClockState.observeAsState(initial = ChooseClockState.Loading)

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        chooseClockViewModel.command.observe(lifecycleOwner, { command ->
            if (command == null) {
                return@observe
            }
            when (command) {
                is ChooseClockViewModel.Command.NavigateToClock -> navigationActions.openClock(
                    OpenClockPayload(
                        whiteClock = command.clock.whitePlayerTime,
                        blackCLock = command.clock.blackPlayerTime,
                    )
                )
                is ChooseClockViewModel.Command.NavigateToCreateClock -> navigationActions.openCreateClock()
                is ChooseClockViewModel.Command.NavigateToSettings -> navigationActions.openSettings()
                is ChooseClockViewModel.Command.ShowInAppReview -> runBlocking { context.showInAppReviewIfPossible(command.reviewInfo) }
            }
        })
    }

    Scaffold(
        topBar = {
            ChooseClockTopBar(onSettingsIconClick = chooseClockViewModel::onOpenSettingsClicked)
        }
    ) { paddingValues ->
        when (val state = chooseClockState.value) {
            is ChooseClockState.Loaded -> {
                ChooseClockLoaded(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(paddingValues),
                    state = state,
                    onCreateClockClick = chooseClockViewModel::onCreateClockClicked,
                    onSelectClockClick = chooseClockViewModel::onSelectClockClick,
                    onClockClick = chooseClockViewModel::onClockClicked,
                    onClockLongClick = chooseClockViewModel::onClockLongClicked,
                    onStarClick = chooseClockViewModel::onStarClicked,
                    onRemoveClocksClick = chooseClockViewModel::onRemoveClocks
                )
            }
            ChooseClockState.Loading -> ChooseClockLoading()
        }
    }
}

@Composable
private fun ChooseClockLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(128.dp))
    }

}

@ExperimentalFoundationApi
@Composable
private fun ChooseClockLoaded(
    modifier: Modifier = Modifier,
    state: ChooseClockState.Loaded,
    onCreateClockClick: () -> Unit = {},
    onSelectClockClick: (Clock) -> Unit = {},
    onClockClick: (Clock) -> Unit = {},
    onClockLongClick: () -> Unit = {},
    onStarClick: (Clock) -> Unit = {},
    onRemoveClocksClick: () -> Unit = {},
) {
    ConstraintLayout(modifier = modifier) {

        val (column, removeSelectedButton) = createRefs()
        //this padding is a hack for bottom.linkTo(removeSelectedButton.top) causing Create new clock button to move up and be below TopAppBar
        LazyColumn(
            modifier = Modifier
                .padding(
                    bottom = if (state.isSelectableModeOn) 48.dp else
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
                        onClick = onCreateClockClick
                    ) {
                        Text(getString(R.string.create_new_clock), fontSize = 21.sp)
                    }
                }

                items(state.clocksToSelected.toList()) { (clock, selected) ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.isSelectableModeOn) {
                            RadioButton(
                                modifier = Modifier.padding(start = 8.dp),
                                selected = selected,
                                onClick = { onSelectClockClick(clock) }
                            )
                        }
                        ClockCard(
                            modifier = Modifier
                                .padding(vertical = 12.dp, horizontal = 16.dp)
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = {
                                        if (state.isSelectableModeOn) {
                                            onSelectClockClick(clock)
                                        } else {
                                            onClockClick(clock)
                                        }
                                    },
                                    onLongClick = onClockLongClick
                                ),
                            clock = clock,
                            onStarClicked = onStarClick
                        )
                    }
                }
            })
        if (state.isSelectableModeOn) {
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
                onClick = onRemoveClocksClick
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp),
                    text = getString(R.string.delete_selected_clocks),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun ChooseClockTopBar(
    onSettingsIconClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = getString(resId = R.string.choose_clock_title)) },
        actions = {
            val icon = painterResource(id = R.drawable.ic_settings_24)
            IconButton(onClick = onSettingsIconClick) {
                Icon(painter = icon, contentDescription = getString(resId = R.string.settings_content_description))
            }
        }
    )
}

@Composable
fun ClockCard(
    clock: Clock,
    modifier: Modifier = Modifier,
    onStarClicked: (Clock) -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 6.dp,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = clock.obtainDeferrableName().getString(),
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
                ClockIconsColumn(playerTime = clock.whitePlayerTime)
                ClockIconsColumn(playerTime = clock.blackPlayerTime, isWhite = false)
                val star = painterResource(id = R.drawable.ic_star_24)
                val starOutline = painterResource(id = R.drawable.ic_star_border_24)
                IconButton(
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp)
                        .align(alignment = Alignment.CenterVertically),
                    onClick = { onStarClicked(clock) }) {
                    Icon(
                        painter = if (clock.isFavourite) star else starOutline,
                        contentDescription = getString(resId = R.string.favourite_clock_content_description),
                        tint = Color.Yellow
                    )
                }
            }
        }
    }
}

@Composable
fun ClockIconsColumn(playerTime: PlayerTime, modifier: Modifier = Modifier, isWhite: Boolean = true) {
    Column(modifier = modifier.padding(top = 4.dp)) {
        Row(modifier = Modifier.defaultMinSize(minHeight = 24.dp)) {
            val clockImage = painterResource(id = R.drawable.ic_clock_24)
            Image(
                modifier = Modifier.padding(end = 8.dp),
                painter = clockImage,
                contentDescription = getString(resId = R.string.clock_content_description),
                colorFilter = ColorFilter.tint(if (isWhite) Color.White else Color.Black)
            )
            Text(
                text = playerTime.toString(),
                color = MaterialTheme.colors.onSurface,
                fontSize = 17.sp
            )
        }
        if (playerTime.increment > 0) {
            Row {
                val incrementClock = PlayerTime(
                    seconds = playerTime.increment
                )
                val incrementImage = painterResource(id = R.drawable.ic_arrow_circle_up_24)
                Image(
                    modifier = Modifier.padding(end = 8.dp),
                    painter = incrementImage,
                    contentDescription = getString(resId = R.string.increment_content_description),
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
