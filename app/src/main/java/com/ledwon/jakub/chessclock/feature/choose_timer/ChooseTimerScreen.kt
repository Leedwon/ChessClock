package com.ledwon.jakub.chessclock.feature.choose_timer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.unit.dp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.navigation.Actions

@Composable
fun ChooseTimerScreen(actions: Actions, chooseTimerViewModel: ChooseTimerViewModel) {

    val timers: List<Timer> by chooseTimerViewModel.timers.observeAsState(
        initial = emptyList()
    )

    chooseTimerViewModel.command.observe(AmbientLifecycleOwner.current, {
        when (it) {
            is ChooseTimerViewModel.Command.NavigateToClock -> actions.openClock(it.timer)
            is ChooseTimerViewModel.Command.NavigateToCreateTimer -> actions.openCreateTimer()
            else -> {}
        }
    })

    LazyColumn(content = {
        items(timers) { timer ->
            TimeCard(
                modifier = Modifier.padding(16.dp).fillMaxWidth().clickable(
                    onClick = {
                        chooseTimerViewModel.onTimerClicked(timer)
                    }),
                timer = timer
            )
        }
        item {
            Button(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                onClick = chooseTimerViewModel::onCreateTimerClicked
            ) {
                Text("create your own timer")
            }
        }
    })
}

@Composable
fun TimeCard(timer: Timer, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = 4.dp, backgroundColor = MaterialTheme.colors.primary) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
            Row {
                val image = loadVectorResource(id = R.drawable.ic_clock_24)
                image.resource.resource?.let {
                    Image(imageVector = image.resource.resource!!)
                }
                Text(text = timer.clockTime.toString())
            }
            timer.timeAdditionPerMove?.let {
                Row {
                    val image = loadVectorResource(id = R.drawable.ic_arrow_circle_up_24)
                    image.resource.resource?.let {
                        Image(imageVector = image.resource.resource!!)
                    }
                    Text(text = timer.timeAdditionPerMove.toString())
                }
            }
            Text(text = timer.description)
        }
    }
}