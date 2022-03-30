package com.ledwon.jakub.chessclock.feature.choose_clock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.util.showInAppReviewIfPossible
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChooseClockScreen(navigationActions: NavigationActions, chooseClockViewModel: ChooseClockViewModel) {

    val chooseClockState = chooseClockViewModel.chooseClockState.collectAsState()
    val state = chooseClockState.value

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val scaffoldState = rememberScaffoldState()
    val removedClocksMessage = stringResource(id = R.string.clocks_removed)

    LaunchedEffect(Unit) {
        chooseClockViewModel.command.collect { command ->
            when (command) {
                is ChooseClockViewModel.Command.NavigateToClock -> navigationActions.openClock(
                    OpenClockPayload(
                        whiteClock = command.clock.whitePlayerTime,
                        blackCLock = command.clock.blackPlayerTime,
                    )
                )
                is ChooseClockViewModel.Command.NavigateToCreateClock -> navigationActions.openCreateClock()
                is ChooseClockViewModel.Command.NavigateToSettings -> navigationActions.openSettings()
                is ChooseClockViewModel.Command.ShowInAppReview -> lifecycleOwner.lifecycleScope.launch {
                    context.showInAppReviewIfPossible(command.reviewInfo)
                }
                ChooseClockViewModel.Command.ShowClocksRemovedMessage -> lifecycleOwner.lifecycleScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(removedClocksMessage)
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ChooseClockTopBar(
                isSelectableModeOn = state is ChooseClockState.Loaded && state.isSelectableModeOn,
                onRemoveClocksClick = chooseClockViewModel::onRemoveClocks,
                onSettingsClick = chooseClockViewModel::onOpenSettingsClicked
            )
        },
        floatingActionButton = {
            if (state is ChooseClockState.Loaded && !state.isSelectableModeOn) {
                CreateNewClockFab(onClick = chooseClockViewModel::onCreateClockClicked)
            }
        },
    ) { paddingValues ->
        when (state) {
            is ChooseClockState.Loaded -> {
                ClocksList(
                    modifier = Modifier
                        .fillMaxHeight() //todo max size?
                        .padding(paddingValues),
                    state = state,
                    onSelectClockClick = chooseClockViewModel::onSelectClockClick,
                    onClockClick = chooseClockViewModel::onClockClicked,
                    onClockLongClick = chooseClockViewModel::onClockLongClicked,
                    onStarClick = chooseClockViewModel::onStarClicked,
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

@Composable
private fun ChooseClockTopBar(
    isSelectableModeOn: Boolean,
    onRemoveClocksClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.choose_clock_title)) },
        actions = {
            when (isSelectableModeOn) {
                true -> RemoveClocksButton(onRemoveClocksClick)
                false -> SettingsButton(onSettingsClick)
            }
        }
    )
}

@Composable
private fun RowScope.RemoveClocksButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = stringResource(id = R.string.remove_selected_clocks_content_description)
        )
    }
}

@Composable
private fun RowScope.SettingsButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(imageVector = Icons.Filled.Settings, contentDescription = stringResource(id = R.string.settings_content_description))
    }
}

@Composable
fun CreateNewClockFab(
    onClick: () -> Unit = {}
) {
    val addIcon = painterResource(id = R.drawable.ic_add_24)
    FloatingActionButton(onClick = onClick) {
        Icon(painter = addIcon, contentDescription = stringResource(R.string.create_new_clock))
    }
}
