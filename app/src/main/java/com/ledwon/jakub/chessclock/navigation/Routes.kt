package com.ledwon.jakub.chessclock.navigation

import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.navigation.Routes.CreateTimerRoute
import com.ledwon.jakub.chessclock.navigation.Routes.SettingsRoute

object Routes {
    object ClockRouteArgs {
        const val WhiteSeconds = "whiteSeconds"
        const val BlackSeconds = "blackSeconds"
        const val WhiteIncrementSeconds = "whiteIncrementSeconds"
        const val BlackIncrementSeconds = "blackIncrementSeconds"
        const val ClockDisplayName = "clockPreviewTypeName"
    }

    const val TimerChooserRoute = "TimerChooser"
    const val ClockRoute =
        "Clock/{${ClockRouteArgs.WhiteSeconds}}/{${ClockRouteArgs.BlackSeconds}}/{${ClockRouteArgs.WhiteIncrementSeconds}}/{${ClockRouteArgs.BlackIncrementSeconds}}"
    const val CreateTimerRoute = "CreateTimer"
    const val SettingsRoute = "Settings"
    const val ClockPreviewRoute = "ClockPreview/{${ClockRouteArgs.ClockDisplayName}}"
}

data class OpenClockPayload(
    val whiteClock: ClockTime,
    val blackCLock: ClockTime
)

class Actions(navController: NavController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val openClock: (OpenClockPayload) -> Unit = {
        navController.navigate("Clock/${it.whiteClock.secondsSum}/${it.blackCLock.secondsSum}/${it.whiteClock.increment}/${it.blackCLock.increment}")
    }

    val openCreateTimer: () -> Unit = {
        navController.navigate(CreateTimerRoute)
    }

    val openSettings: () -> Unit = {
        navController.navigate(SettingsRoute)
    }

    val openClockDisplayPreview: (clockName: String) -> Unit = {
        navController.navigate("ClockPreview/${it}")
    }
}