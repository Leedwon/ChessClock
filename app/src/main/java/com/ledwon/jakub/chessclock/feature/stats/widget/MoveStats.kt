package com.ledwon.jakub.chessclock.feature.stats.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.stats.data.Move
import com.ledwon.jakub.chessclock.feature.stats.data.Moves
import com.ledwon.jakub.chessclock.util.getString

@Composable
fun MovesStats(
    modifier: Modifier = Modifier,
    slowestMoves: Moves,
    averageMoves: Moves,
    fastestMoves: Moves,
    movesCount: Int
) {
    val slowestMoveText = getString(resId = R.string.slowest_move)
    val averageMoveText = getString(resId = R.string.average_move)
    val fastestMoveText = getString(resId = R.string.fastest_move)
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = getString(resId = R.string.moves_count, formatArgs = listOf(movesCount)),
            fontSize = 21.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MoveStat(
                moveDescription = slowestMoveText,
                iconRes = R.drawable.ic_slow_48,
                moves = slowestMoves
            )
            MoveStat(
                moveDescription = averageMoveText,
                iconRes = R.drawable.ic_medium_48,
                moves = averageMoves
            )
            MoveStat(
                moveDescription = fastestMoveText,
                iconRes = R.drawable.ic_fast_48,
                moves = fastestMoves
            )
        }
    }
}

@Composable
fun MoveStat(
    moveDescription: String,
    @DrawableRes iconRes: Int,
    moves: Moves
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = moveDescription, fontSize = 18.sp)
        Icon(
            modifier = Modifier.padding(vertical = 8.dp),
            painter = painterResource(id = iconRes),
            contentDescription = moveDescription,
            tint = Color.Unspecified
        )
        Text(
            getString(resId = R.string.stats_white_move, listOf(moves.white?.duration ?: getString(R.string.stats_no_moves_yet))),
            textAlign = TextAlign.Center,
            fontSize = 17.sp
        )
        Text(
            getString(resId = R.string.stats_black_move, listOf(moves.black?.duration ?: getString(R.string.stats_no_moves_yet))),
            textAlign = TextAlign.Center,
            fontSize = 17.sp
        )
        Text(
            getString(resId = R.string.stats_general_move, listOf(moves.general?.duration ?: getString(R.string.stats_no_moves_yet))),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview
fun MoveStatPreview() {
    MoveStat(
        moveDescription = "Slowest move",
        iconRes = R.drawable.ic_slow_48,
        moves = Moves(
            white = Move(44530),
            black = Move(52530),
            general = Move(123530)
        )
    )
}

@Composable
@Preview
fun MoveStatsPreview() {
    MovesStats(
        slowestMoves = Moves(
            Move(6200),
            Move(4500),
            Move(12200)
        ),
        averageMoves = Moves(
            Move(12345),
            Move(6789),
            Move(24224)
        ),
        fastestMoves = Moves(
            Move(123123),
            Move(12444),
            Move(1233)
        ),
        movesCount = 42
    )
}