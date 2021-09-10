package com.ledwon.jakub.chessclock.navigation

import androidx.navigation.NavController
import com.ledwon.jakub.chessclock.data.model.ClockTime
import com.ledwon.jakub.chessclock.navigation.Routes.CreateTimerRoute
import com.ledwon.jakub.chessclock.navigation.Routes.SettingsRoute

//todo simplify - so we don't rely on hardcoded values in NavActions
object Routes {
    object ClockArgs {
        const val WhiteSeconds = "whiteSeconds"
        const val BlackSeconds = "blackSeconds"
        const val WhiteIncrementSeconds = "whiteIncrementSeconds"
        const val BlackIncrementSeconds = "blackIncrementSeconds"
    }

    object ClockPreviewArgs {
        const val ClockDisplayId = "clockPreviewTypeId"
    }

    const val TimerChooserRoute = "TimerChooser"
    const val ClockRoute =
        "Clock/{${ClockArgs.WhiteSeconds}}/{${ClockArgs.BlackSeconds}}/{${ClockArgs.WhiteIncrementSeconds}}/{${ClockArgs.BlackIncrementSeconds}}"
    const val CreateTimerRoute = "CreateTimer"
    const val SettingsRoute = "Settings"
    const val ClockPreviewRoute = "ClockPreview/{${ClockPreviewArgs.ClockDisplayId}}"
}

data class OpenClockPayload(
    val whiteClock: ClockTime,
    val blackCLock: ClockTime
)

class NavigationActions(navController: NavController) {
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

    val openClockDisplayPreview: (clockId: String) -> Unit = {
        navController.navigate("ClockPreview/${it}")
    }
}