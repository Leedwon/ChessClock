package com.ledwon.jakub.chessclock.feature.choose_timer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.data.model.Timer
import com.ledwon.jakub.chessclock.navigation.Actions
import com.ledwon.jakub.chessclock.navigation.OpenClockPayload
import com.ledwon.jakub.chessclock.ui.widgets.OutlinePrimaryButton

@Composable
fun ChooseTimerScreen(actions: Actions, chooseTimerViewModel: ChooseTimerViewModel) {

    val timers: List<Timer> by chooseTimerViewModel.timers.observeAsState(
        initial = emptyList()
    )

    chooseTimerViewModel.command.observe(AmbientLifecycleOwner.current, {
        when (it) {
            is ChooseTimerViewModel.Command.NavigateToClock -> actions.openClock(
                OpenClockPayload(
                    whiteClock = it.timer.whiteClockTime,
                    blackCLock = it.timer.blackClockTime,
                )
            )
            is ChooseTimerViewModel.Command.NavigateToCreateTimer -> actions.openCreateTimer()
            else -> {
            }
        }
    })

    LazyColumn(content = {
        item {
            OutlinePrimaryButton(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                onClick = chooseTimerViewModel::onCreateTimerClicked
            ) {
                Text("Create new timer", fontSize = 21.sp)
            }
        }
        items(timers) { timer ->
            TimeCard(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp).fillMaxWidth()
                    .clickable(
                        onClick = {
                            chooseTimerViewModel.onTimerClicked(timer)
                        }),
                timer = timer,
                onRemove = chooseTimerViewModel::onRemoveTimer
            )
        }
    })
}

@Composable
fun TimeCard(timer: Timer, modifier: Modifier = Modifier, onRemove: (Timer) -> Unit) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ClockIconsColumn(clockTime = timer.whiteClockTime)
                ClockIconsColumn(clockTime = timer.blackClockTime, isWhite = false)
                val removeImage = painterResource(id = R.drawable.ic_delete_forever_24)
                Image(
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        onRemove(timer)
                    },
                    painter = removeImage,
                    contentDescription = "remove timer",
                    colorFilter = ColorFilter.tint(Color.Red)
                )
            }
        }

    }
}

@Composable
fun ClockIconsColumn(clockTime: ClockTime, modifier: Modifier = Modifier, isWhite: Boolean = true) {
    Column(modifier = modifier.padding(top = 4.dp)) {
        Row {
            val clockImage = painterResource(id = R.drawable.ic_clock_24)
            Image(
                modifier = Modifier.padding(end = 8.dp),
                painter = clockImage,
                contentDescription = "timer",
                colorFilter = ColorFilter.tint(if (isWhite) Color.White else Color.Black)
            )
            Text(
                text = clockTime.toString(),
                fontSize = 17.sp,
                color = MaterialTheme.colors.onSurface
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
                    text = incrementClock.toString(), fontSize = 17.sp,
                    color = MaterialTheme.colors.onSurface
                )
            }
        }
    }

}
