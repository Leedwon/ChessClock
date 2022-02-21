package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay
import com.ledwon.jakub.chessclock.util.toDeferrableString

@Composable
fun ClockButton(
    modifier: Modifier = Modifier,
    player: PlayerDisplay,
    onClick: (PlayerDisplay) -> Unit,
    enabled: Boolean,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = player.backgroundColor(),
            contentColor = player.contentColor(),
            disabledBackgroundColor = player.backgroundColor()
        ),
        onClick = { onClick.invoke(player) },
        content = content
    )
}

@Preview
@Composable
fun ClockButtonPreview() {
    ClockButton(
        modifier = Modifier.fillMaxWidth(),
        player = PlayerDisplay.White("01:00".toDeferrableString(), 1.0f, true),
        onClick = { },
        enabled = true
    ) {
        Text("01:00")
    }
}