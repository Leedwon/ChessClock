package com.example.chessclock

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
import com.example.chessclock.navigation.Actions

@Composable
fun TimerChooser(actions: Actions, timerViewModel: TimerViewModel) {

    val timers: List<Timer> by timerViewModel.timers.observeAsState(
        initial = emptyList()
    )

    timerViewModel.command.observe(AmbientLifecycleOwner.current, {
        when (it) {
            is TimerViewModel.Command.NavigateToClock -> actions.openClock(it.timer)
            is TimerViewModel.Command.NavigateToCreateTimer -> actions.openCreateTimer()
            else -> {}
        }
    })

    LazyColumn(content = {
        items(timers) { timer ->
            TimeCard(
                modifier = Modifier.padding(16.dp).fillMaxWidth().clickable(
                    onClick = {
                        timerViewModel.onTimerClicked(timer)
                    }),
                timer = timer
            )
        }
        item {
            Button(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                onClick = timerViewModel::onCreateTimerClicked
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