package com.ledwon.jakub.chessclock.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.ledwon.jakub.chessclock.feature.choose_timer.Timer
import com.ledwon.jakub.chessclock.navigation.Routes.CreateTimerRoute

object Routes {
    object ClockRouteArgs {
        const val WhiteSeconds = "whiteSeconds"
        const val BlackSeconds = "blackSeconds"
        const val WhiteIncrementSeconds = "whiteIncrementSeconds"
        const val BlackIncrementSeconds = "blackIncrementSeconds"
    }

    const val TimerChooserRoute = "TimerChooser"
    const val ClockRoute = "Clock/{${ClockRouteArgs.WhiteSeconds}}/{${ClockRouteArgs.BlackSeconds}}/{${ClockRouteArgs.WhiteIncrementSeconds}}/{${ClockRouteArgs.BlackIncrementSeconds}}"
    const val CreateTimerRoute = "CreateTimer"
}

class Actions(navController: NavController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val openClock: (Timer) -> Unit = {
        navController.navigate("Clock/${it.clockTime.seconds}/${it.clockTime.seconds}/${it.timeAdditionPerMove?.seconds ?: 0}/${it.timeAdditionPerMove?.seconds ?: 0}")
    }

    val openCreateTimer: () -> Unit = {
        navController.navigate(CreateTimerRoute)
    }
}