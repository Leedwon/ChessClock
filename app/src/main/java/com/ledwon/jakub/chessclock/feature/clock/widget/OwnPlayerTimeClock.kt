package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay

@Composable
fun OwnPlayerTimeClock(
    playersDisplay: Pair<PlayerDisplay, PlayerDisplay>,
    rotations: Pair<Float, Float>,
    onClockButtonClick: (PlayerDisplay) -> Unit,
    pulsationEnabled: Boolean,
    enabled: Boolean = true,
    paddingFromCenter: Dp = 0.dp
) {
    val infiniteTransition: InfiniteTransition? = if (pulsationEnabled) rememberInfiniteTransition() else null

    val firstClockFontSize: Float = if (pulsationEnabled && infiniteTransition != null) {
        infiniteTransition.animateClockFontSize(isActive = playersDisplay.first.isActive).value
    } else {
        35f
    }
    val secondClockFontSize: Float = if (pulsationEnabled && infiniteTransition != null) {
        infiniteTransition.animateClockFontSize(isActive = playersDisplay.second.isActive).value
    } else {
        35f
    }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
        val btnModifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ClockButton(
            modifier = btnModifier,
            player = playersDisplay.first,
            onClick = onClockButtonClick,
            enabled = enabled
        ) {
            Text(
                modifier = Modifier
                    .padding(bottom = paddingFromCenter)
                    .rotate(rotations.first),
                fontSize = firstClockFontSize.sp,
                text = playersDisplay.first.text.getString(),
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
                modifier = Modifier
                    .padding(top = paddingFromCenter)
                    .rotate(rotations.second),
                fontSize = secondClockFontSize.sp,
                text = playersDisplay.second.text.getString(),
                color = playersDisplay.second.contentColor()
            )
        }
    }
}