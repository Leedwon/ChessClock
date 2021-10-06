package com.ledwon.jakub.chessclock.feature.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.stats.widget.MoveBars
import com.ledwon.jakub.chessclock.feature.stats.widget.MovesStats
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.ui.darkGray
import com.ledwon.jakub.chessclock.ui.lightGray
import com.ledwon.jakub.chessclock.util.rememberString

@Composable
fun StatsScreen(actions: NavigationActions, viewModel: StatsViewModel) {

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = darkGray,
        topBar = {
            TopAppBar(
                title = { Text(text = rememberString(R.string.stats)) },
                navigationIcon = {
                    val backIcon = painterResource(id = R.drawable.ic_arrow_back_24)
                    IconButton(onClick = actions.navigateBack) {
                        Icon(painter = backIcon, contentDescription = rememberString(R.string.navigate_back_content_description))
                    }
                }
            )
        }
    ) {

        when (val data = viewModel.data) {
            StatsViewModel.Stats.Empty -> EmptyStats()
            is StatsViewModel.Stats.Data -> Stats(data)
        }
    }
}

@Composable
fun StatsDivider() {
    Divider(
        modifier = Modifier.padding(vertical = 4.dp),
        color = lightGray,
        thickness = 0.75.dp,
    )
}

@Composable
fun Stats(
    data: StatsViewModel.Stats.Data
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MovesStats(
            modifier = Modifier.padding(bottom = 8.dp),
            slowestMoves = data.slowestMoves,
            averageMoves = data.averageMoves,
            fastestMoves = data.fastestMoves,
            movesCount = data.moves.size
        )
        StatsDivider()
        Text(
            modifier = Modifier.padding(8.dp),
            text = rememberString(resId = R.string.moves_list), fontSize = 21 .sp
        )
        StatsDivider()
        MoveBars(
            modifier = Modifier.padding(vertical = 8.dp),
            moves = data.moves
        )
    }
}

@Composable
fun EmptyStats() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color = darkGray),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            text = rememberString(resId = R.string.stats_empty), fontSize = 21.sp
        )
    }
}