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
fun rememberString(@StringRes resId: Int, vararg formatArgs: Any): String {
    val context = LocalContext.current
    return remember {
        context.getString(resId, formatArgs)
    }
}