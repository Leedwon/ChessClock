package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay

@Composable
fun BothPlayersTimeClock(
    playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    onClockButtonClick: (PlayerDisplay) -> Unit,
    enabled: Boolean = true
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (firstBtn, secondBtn, firstPlayerOwnTime, firstPlayerOpponentTime, secondPlayerOwnTime, secondPlayerOpponentTime) = createRefs()

        ClockButton(
            modifier = Modifier.constrainAs(firstBtn) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(secondBtn.top)
            }.fillMaxHeight(0.5f).fillMaxWidth(),
            player = playersDisplay.first,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            //no content here because we need constraints for text
        }
        Text(
            modifier = Modifier.rotate(180f).constrainAs(firstPlayerOwnTime) {
                top.linkTo(firstBtn.top, margin = 64.dp)
                start.linkTo(firstBtn.start, margin = 64.dp)
            },
            text = playersDisplay.first.text,
            fontSize = 35.sp,
            color = playersDisplay.first.textColor()
        )
        Text(
            modifier = Modifier.rotate(180f).constrainAs(firstPlayerOpponentTime) {
                bottom.linkTo(firstBtn.bottom, margin = 64.dp)
                end.linkTo(firstBtn.end, margin = 64.dp)
            },
            text = playersDisplay.second.text,
            fontSize = 24.sp,
            color = playersDisplay.first.textColor()
        )
        ClockButton(
            modifier = Modifier.constrainAs(
                secondBtn
            ) {
                top.linkTo(firstBtn.bottom)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }.fillMaxHeight(0.5f).fillMaxWidth(),
            player = playersDisplay.second,
            onClick = { onClockButtonClick(playersDisplay.second) },
            enabled = enabled,
        ) {
            //no content here because we need constraints for text
        }
        Text(
            modifier = Modifier.constrainAs(secondPlayerOwnTime) {
                bottom.linkTo(secondBtn.bottom, margin = 64.dp)
                start.linkTo(secondBtn.start, margin = 64.dp)
            },
            text = playersDisplay.second.text,
            fontSize = 35.sp,
            color = playersDisplay.second.textColor()
        )
        Text(
            modifier = Modifier.constrainAs(secondPlayerOpponentTime) {
                top.linkTo(secondBtn.top, margin = 64.dp)
                end.linkTo(secondBtn.end, margin = 64.dp)
            },
            text = playersDisplay.first.text,
            fontSize = 24.sp,
            color = playersDisplay.second.textColor()
        )
    }
}

@Preview
@Composable
fun ClockPreview() {
    BothPlayersTimeClock(
        playersDisplay = PlayerDisplay.White("05:00") to PlayerDisplay.Black("03:00"),
        onClockButtonClick = { }, //noop
    )
}