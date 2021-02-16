package com.ledwon.jakub.chessclock.util

import android.view.Window
import androidx.compose.runtime.ambientOf
import androidx.navigation.NavController

val AmbientNavController = ambientOf<NavController>()
val AmbientIsDarkMode = ambientOf<Boolean>()
val AmbientWindowProvider = ambientOf<Window>()