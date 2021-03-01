package com.ledwon.jakub.chessclock.util

import android.view.Window
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

//todo find a way without a factory
val LocalNavController = compositionLocalOf<NavController?>(defaultFactory = { null })
val LocalIsDarkMode = compositionLocalOf<Boolean>(defaultFactory = { false })
val LocalWindowProvider = compositionLocalOf<Window?>(defaultFactory = { null })