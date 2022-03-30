package com.ledwon.jakub.chessclock.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.ledwon.jakub.chessclock.R
import com.ledwon.jakub.chessclock.feature.choose_clock.ClockTimeType
import com.ledwon.jakub.chessclock.feature.choose_clock.clockTimeType
import com.ledwon.jakub.chessclock.model.Clock

object ClockTypeIconProvider {

    @Composable
    internal fun Clock.obtainTypeIcon(): Painter = when (clockTimeType) {
        ClockTimeType.Bullet -> painterResource(id = R.drawable.ic_bullet_24)
        ClockTimeType.Blitz -> painterResource(id = R.drawable.ic_lightning_24)
        ClockTimeType.Rapid -> painterResource(id = R.drawable.ic_rabbit_24)
        ClockTimeType.Custom,
        ClockTimeType.Classic -> painterResource(id = R.drawable.ic_classic_clock_24)
    }
}
