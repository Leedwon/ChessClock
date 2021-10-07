package com.ledwon.jakub.chessclock.feature.stats.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.feature.stats.data.Move
import com.ledwon.jakub.chessclock.feature.stats.data.PlayerColor

@Composable
fun MoveBar(widthFraction: Float, move: Move) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight()
                .background(
                    color = when (move.color) {
                        PlayerColor.Unspecified,
                        PlayerColor.White -> Color.White
                        PlayerColor.Black -> Color.Black
                    }
                )
        )
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .background(
                    color = when (move.color) {
                        PlayerColor.Unspecified,
                        PlayerColor.White -> Color.White
                        PlayerColor.Black -> Color.Black
                    }
                )
        ) {
            Text(
                modifier = Modifier.padding(end = 2.dp),
                text = move.duration,
                fontSize = 9.sp,
                color = when (move.color) {
                    PlayerColor.Unspecified,
                    PlayerColor.White -> Color.Black
                    PlayerColor.Black -> Color.White
                }
            )
        }
    }
}


@Composable
fun MoveBars(modifier: Modifier = Modifier, moves: List<Move>) {
    LazyColumn(modifier.fillMaxWidth()) {
        items(moves) { move ->
            MoveBar(
                widthFraction = move.ratioToSlowestMove?.let { it - 0.1f } ?: 0.0f,
                move = move
            )
        }
    }
}

@Preview
@Composable
fun MoveBarPreview() {
    MoveBar(0.75f, Move(350, PlayerColor.Black))
}

@Preview
@Composable
fun MoveBarsPreview() {
    MoveBars(
        moves = listOf(
            Move(350, PlayerColor.White, 0.75f),
            Move(250, PlayerColor.Black, 0.25f),
            Move(2211, PlayerColor.Black, 0.15f),
            Move(3000, PlayerColor.White, 0.50f),
        )
    )
}