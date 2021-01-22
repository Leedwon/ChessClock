package com.ledwon.jakub.chessclock.feature.clock.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.DeferredResource
import androidx.compose.ui.unit.dp


@Composable
fun ClockCenterButton(
    onClick: () -> Unit,
    icon: DeferredResource<ImageVector>,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        modifier = modifier.height(96.dp).width(96.dp),
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = Color.Black,
        ),
        border = BorderStroke(2.dp, color = Color.White),
        shape = CircleShape
    ) {
        icon.resource.resource?.let {
            Icon(
                imageVector = it,
                tint = iconTint
            )
        }
    }
}