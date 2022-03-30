package com.ledwon.jakub.chessclock.feature.choose_clock

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.util.ClockNameProvider.obtainDeferrableName
import com.ledwon.jakub.chessclock.util.ClockTypeIconProvider.obtainTypeIcon

@ExperimentalFoundationApi
@Composable
internal fun ClocksList(
    modifier: Modifier = Modifier,
    state: ChooseClockState.Loaded,
    onSelectClockClick: (Clock) -> Unit = {},
    onClockClick: (Clock) -> Unit = {},
    onClockLongClick: () -> Unit = {},
    onStarClick: (Clock) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        content = {
            items(state.clocksToSelected.toList()) { (clock, selected) ->
                ClockItem(
                    clock = clock,
                    selected = selected,
                    showSelectButton = state.isSelectableModeOn,
                    onClick = {
                        if (state.isSelectableModeOn) {
                            onSelectClockClick(clock)
                        } else {
                            onClockClick(clock)
                        }
                    },
                    onLongClick = onClockLongClick,
                    onSelectClick = { onSelectClockClick(clock) },
                    onStarClick = { onStarClick(clock) }
                )
            }
        }
    )
}

@ExperimentalFoundationApi
@Composable
private fun LazyItemScope.ClockItem(
    modifier: Modifier = Modifier,
    clock: Clock,
    selected: Boolean,
    showSelectButton: Boolean,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onSelectClick: () -> Unit = {},
    onStarClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showSelectButton) {
            RadioButton(
                modifier = Modifier.padding(start = 8.dp),
                selected = selected,
                onClick = onSelectClick,
            )
        }
        ClockCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                ),
            clock = clock,
            onStarClicked = onStarClick
        )
    }
}

@Composable
private fun ClockCard(
    clock: Clock,
    modifier: Modifier = Modifier,
    onStarClicked: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
            ClockTitle(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                clock = clock
            )
            ClockBody(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                onStarClick = onStarClicked,
                clock = clock,
            )
        }
    }
}

@Composable
private fun ColumnScope.ClockTitle(
    modifier: Modifier = Modifier,
    clock: Clock,
) {
    Text(
        modifier = modifier,
        text = clock.obtainDeferrableName().getString(),
        color = MaterialTheme.colors.primary,
        fontSize = 19.sp
    )
}

@Composable
private fun ColumnScope.ClockBody(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    onStarClick: () -> Unit = {},
    clock: Clock,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        ClockTypeIcon(clock = clock)
        val clocksBodyModifier = Modifier.defaultMinSize(minHeight = 24.dp)
        if (clock.whitePlayerTime == clock.blackPlayerTime) {
            SameClocksBody(
                modifier = clocksBodyModifier,
                time = clock.whitePlayerTime,
            )
        } else {
            DifferentClocksBody(
                modifier = clocksBodyModifier,
                whiteTime = clock.whitePlayerTime,
                blackTime = clock.blackPlayerTime,
            )
        }
        FavouriteButton(
            modifier = Modifier
                .height(24.dp)
                .width(24.dp)
                .align(alignment = Alignment.CenterVertically),
            isFavourite = clock.isFavourite,
            onClick = onStarClick
        )
    }
}

@Composable
private fun RowScope.ClockTypeIcon(
    modifier: Modifier = Modifier,
    clock: Clock,
) {
    Icon(
        modifier = modifier,
        painter = clock.obtainTypeIcon(),
        contentDescription = null,
        tint = MaterialTheme.colors.secondary
    )
}

@Composable
private fun RowScope.SameClocksBody(
    modifier: Modifier = Modifier,
    time: PlayerTime,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TimeWithIcon(type = TimeWithIconType.ClockTime, time = time)
        if (time.increment > 0) {
            val incrementTime = PlayerTime(seconds = time.increment)
            TimeWithIcon(type = TimeWithIconType.Increment, time = incrementTime)
        }
    }
}

@Composable
private fun RowScope.DifferentClocksBody(
    modifier: Modifier = Modifier,
    whiteTime: PlayerTime,
    blackTime: PlayerTime,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ClockIconsWithPlayerNames(playerType = PlayerType.White, time = whiteTime)
        ClockIconsWithPlayerNames(playerType = PlayerType.Black, time = blackTime)
    }
}

private enum class PlayerType {
    White,
    Black
}

@Composable
private fun PlayerType.playerName() = when (this) {
    PlayerType.White -> stringResource(R.string.white)
    PlayerType.Black -> stringResource(id = R.string.black)
}

@Composable
private fun RowScope.ClockIconsWithPlayerNames(
    playerType: PlayerType,
    time: PlayerTime
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = playerType.playerName(),
            color = MaterialTheme.colors.onSurface,
            fontSize = 17.sp
        )
        TimeWithIcon(type = TimeWithIconType.ClockTime, time = time)
        if (time.increment > 0) {
            val incrementTime = PlayerTime(seconds = time.increment)
            TimeWithIcon(type = TimeWithIconType.Increment, time = incrementTime)
        }
    }
}

private enum class TimeWithIconType {
    ClockTime,
    Increment,
}

@Composable
private fun clockIcon() = painterResource(id = R.drawable.ic_clock_24)

@Composable
private fun incrementIcon() = painterResource(id = R.drawable.ic_arrow_circle_up_24)


@Composable
private fun TimeWithIconType.icon() = when (this) {
    TimeWithIconType.ClockTime -> clockIcon()
    TimeWithIconType.Increment -> incrementIcon()
}

@Composable
private fun TimeWithIconType.contentDescription() = when (this) {
    TimeWithIconType.ClockTime -> stringResource(id = R.string.clock_content_description)
    TimeWithIconType.Increment -> stringResource(id = R.string.increment_content_description)
}

@Composable
private fun TimeWithIcon(
    modifier: Modifier = Modifier,
    type: TimeWithIconType,
    time: PlayerTime,
) {
    Row(modifier) {
        Icon(
            modifier = Modifier.padding(end = 8.dp),
            painter = type.icon(),
            contentDescription = type.contentDescription(),
            tint = MaterialTheme.colors.primary
        )
        Text(
            text = time.toString(),
            color = MaterialTheme.colors.onSurface,
            fontSize = 17.sp
        )
    }
}

@Composable
private fun RowScope.FavouriteButton(
    modifier: Modifier = Modifier,
    isFavourite: Boolean,
    onClick: () -> Unit,
) {
    val star = painterResource(id = R.drawable.ic_star_24)
    val starOutline = painterResource(id = R.drawable.ic_star_border_24)
    IconButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = if (isFavourite) star else starOutline,
            contentDescription = stringResource(R.string.favourite_clock_content_description),
            tint = MaterialTheme.colors.secondary
        )
    }
}
