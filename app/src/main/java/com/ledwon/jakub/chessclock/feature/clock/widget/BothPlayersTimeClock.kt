package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay
import timber.log.Timber

@Composable
fun BothPlayersTimeClock(
    playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    onClockButtonClick: (PlayerDisplay) -> Unit,
    pulsationEnabled: Boolean,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition()

    val firstClockFontSize: Float = if (pulsationEnabled) {
        infiniteTransition.animateClockFontSize(isActive = playersDisplay.first.isActive).value
    } else {
        35f
    }
    val secondClockFontSize: Float = if (pulsationEnabled) {
        Timber.d("i should not be here to not waste resources")
        infiniteTransition.animateClockFontSize(isActive = playersDisplay.second.isActive).value
    } else {
        35f
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
        val btnModifier = Modifier.weight(1f).fillMaxWidth()
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.first,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val textColor = playersDisplay.first.contentColor()
                Text(
                    modifier = Modifier.rotate(180f).align(Alignment.CenterHorizontally),
                    fontSize = firstClockFontSize.sp,
                    text = playersDisplay.first.text,
                    color = textColor
                )
                Text(
                    modifier = Modifier.padding(top = 32.dp).rotate(180f).align(Alignment.End),
                    fontSize = 25.sp,
                    text = playersDisplay.second.text,
                    color = textColor
                )
            }
        }
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.second,
            onClick = onClockButtonClick,
            enabled = enabled,
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                val textColor = playersDisplay.second.contentColor()
                Text(
                    modifier = Modifier.align(Alignment.End).padding(bottom = 32.dp),
                    fontSize = 25.sp,
                    text = playersDisplay.first.text,
                    color = textColor
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = secondClockFontSize.sp,
                    text = playersDisplay.second.text,
                    color = textColor
                )
            }
        }
    }
}

@Preview
@Composable
fun ClockPreview() {
    BothPlayersTimeClock(
        playersDisplay = PlayerDisplay.White("05:00", 1.0f, true) to PlayerDisplay.Black(
            "03:00",
            0.6f,
            false
        ),
        onClockButtonClick = { }, //noop
        pulsationEnabled = true
    )
}