package com.example.chessclock

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

@Composable
fun Clock(clockViewModel: ClockViewModel) {

    val state: State by clockViewModel.state.observeAsState(
        State(
            "Start clock",
            "Start clock"
        )
    )

    val clockModifier = Modifier.fillMaxSize()
    Column(modifier = clockModifier, verticalArrangement = Arrangement.SpaceAround) {
        val btnModifier = Modifier.weight(1f).fillMaxWidth()
        ClockButton(
            modifier = btnModifier,
            player = Player.White, state.whiteText,
            onClick = {
                clockViewModel.clockClicked(Player.White)
            },
        rotateDegrees = 180f)
        ClockButton(
            modifier = btnModifier,
            player = Player.Black,
            state.blackText,
            onClick = {
                clockViewModel.clockClicked(Player.Black)
            })
    }
}

@Composable
fun ClockButton(modifier: Modifier, player: Player, text: String, onClick: () -> Unit, rotateDegrees: Float = 0f) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = when (player) {
                Player.White -> Color.White
                Player.Black -> Color.Black
            }
        ),
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier.rotate(rotateDegrees),
            text = text, fontSize = TextUnit.Companion.Sp(35), color = when (player) {
                Player.White -> Color.Black
                Player.Black -> Color.White
            }
        )
    }
}
