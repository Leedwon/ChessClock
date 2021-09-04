package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp


@Composable
fun ClockCenterButton(
    onClick: () -> Unit,
    icon: Painter,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.Black,
        ),
        border = BorderStroke(2.dp, color = Color.White),
        shape = CircleShape
    ) {
        Icon(
            painter = icon,
            contentDescription = "clock center action",
            tint = iconTint
        )
    }
}