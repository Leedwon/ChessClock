package com.ledwon.jakub.chessclock.util

import android.view.Window
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController>(defaultFactory = { error("no nav controller available") })
val LocalIsDarkMode = compositionLocalOf<Boolean>(defaultFactory = { false })
val LocalWindowProvider = compositionLocalOf<Window>(defaultFactory = { error("no window available") })


@Composable
fun getString(@StringRes resId: Int, formatArgs: List<Any> = emptyList()): String {
    return LocalContext.current.getString(resId, *formatArgs.toTypedArray())
}

@Composable
fun rememberString(@StringRes resId: Int, formatArgs: List<Any> = emptyList()): String {
    val value = getString(resId = resId, formatArgs = formatArgs)
    return remember { value }
}

class DeferrableString(
    @StringRes private val resId: Int,
    private val formatArgs: List<Any> = emptyList()
) {
    @Composable
    fun getString(): String = getString(resId = resId, formatArgs)

    @Composable
    fun getAndRemember(): String = rememberString(resId, formatArgs)
}

