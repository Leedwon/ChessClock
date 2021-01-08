package com.example.chessclock.navigation

import androidx.navigation.NavController
import com.example.chessclock.InitialData
import androidx.navigation.compose.navigate
import com.example.chessclock.TimerViewModel

object Routes {
    object ClockRouteArgs {
        const val WhiteMinutes = "whiteMinutes"
        const val BlackMinutes = "blackMinutes"
    }

    const val TimerChooserRoute = "TimerChooser"
    const val ClockRoute = "Clock/{${ClockRouteArgs.WhiteMinutes}}/{${ClockRouteArgs.BlackMinutes}}"
}

class Actions(navController: NavController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val openClock: (TimerViewModel.State) -> Unit = {
        navController.navigate("Clock/${it.whiteTime.toInt()}/${it.blackTime.toInt()}")
    }
}