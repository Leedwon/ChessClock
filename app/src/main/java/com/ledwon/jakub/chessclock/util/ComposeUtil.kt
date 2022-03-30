package com.ledwon.jakub.chessclock.util

import android.view.Window
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController>(defaultFactory = { error("no nav controller available") })
val LocalIsDarkMode = compositionLocalOf<Boolean>(defaultFactory = { false })
val LocalWindowProvider = compositionLocalOf<Window>(defaultFactory = { error("no window available") })

sealed interface DeferrableString {
    @Composable
    fun getString(): String
}

data class ResDeferrableString(
    @StringRes private val resId: Int,
    private val formatArgs: List<Any> = emptyList()
) : DeferrableString {
    @Composable
    override fun getString(): String = stringResource(resId, *formatArgs.toTypedArray())
}

data class ValueDeferrableString(
    private val value: String
) : DeferrableString {
    @Composable
    override fun getString(): String = value
}

fun String.toDeferrableString() = ValueDeferrableString(this)
