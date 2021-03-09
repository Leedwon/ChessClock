package com.ledwon.jakub.chessclock.util

import android.view.Window
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController>(defaultFactory = { error("no nav controller available") })
val LocalIsDarkMode = compositionLocalOf<Boolean>(defaultFactory = { false })
val LocalWindowProvider = compositionLocalOf<Window>(defaultFactory = { error("no window available") })