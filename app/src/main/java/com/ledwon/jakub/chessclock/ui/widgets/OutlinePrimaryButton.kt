package com.ledwon.jakub.chessclock.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
inline fun OutlinePrimaryButton(
    noinline onClick: () -> Unit,
    modifier: Modifier = Modifier,
    noinline content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(25),
        border = BorderStroke(1.dp, MaterialTheme.colors.primary)
    ) {
        content()
    }
}