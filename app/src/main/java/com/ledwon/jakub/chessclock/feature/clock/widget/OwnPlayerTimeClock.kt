package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.feature.clock.PlayerDisplay

@Composable
fun OwnPlayerTimeClock(
    playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    rotations: Pair<Float, Float>,
    onClockButtonClick: (PlayerDisplay) -> Unit,
    enabled: Boolean = true
) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
        val btnModifier = Modifier.weight(1f).fillMaxWidth()
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.first,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            Text(
                modifier = Modifier.rotate(rotations.first),
                fontSize = 35.sp,
                text = playersDisplay.first.text,
                color = playersDisplay.first.contentColor()
            )
        }
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.second,
            onClick = onClockButtonClick,
            enabled = enabled,
        ) {
            Text(
                modifier = Modifier.rotate(rotations.second),
                fontSize = 35.sp,
                text = playersDisplay.second.text,
                color = playersDisplay.second.contentColor()
            )
        }
    }
}