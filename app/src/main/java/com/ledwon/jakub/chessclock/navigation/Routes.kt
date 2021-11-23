package com.ledwon.jakub.chessclock.navigation

import androidx.navigation.NavController
import com.ledwon.jakub.chessclock.model.PlayerTime
import com.ledwon.jakub.chessclock.navigation.Routes.CreateClockRoute
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

    object StatsArgs {
        const val MovesMillisCsv = "movesMillis"
    }

    const val ChooseClockRoute = "ChooseClock"
    const val ClockRoute =
        "Clock/{${ClockArgs.WhiteSeconds}}/{${ClockArgs.BlackSeconds}}/{${ClockArgs.WhiteIncrementSeconds}}/{${ClockArgs.BlackIncrementSeconds}}"
    const val CreateClockRoute = "CreateClock"
    const val SettingsRoute = "Settings"
    const val ClockPreviewRoute = "ClockPreview/{${ClockPreviewArgs.ClockDisplayId}}"
    const val StatsRoute = "Stats/{${StatsArgs.MovesMillisCsv}}"
}

data class OpenClockPayload(
    val whiteClock: PlayerTime,
    val blackCLock: PlayerTime
)

class NavigationActions(navController: NavController) {
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }

    val openClock: (OpenClockPayload) -> Unit = {
        navController.navigate("Clock/${it.whiteClock.secondsSum}/${it.blackCLock.secondsSum}/${it.whiteClock.increment}/${it.blackCLock.increment}")
    }

    val openCreateClock: () -> Unit = {
        navController.navigate(CreateClockRoute)
    }

    val openSettings: () -> Unit = {
        navController.navigate(SettingsRoute)
    }

    val openClockDisplayPreview: (clockId: String) -> Unit = {
        navController.navigate("ClockPreview/$it")
    }

    val openStats: (movesMillis: List<Long>) -> Unit = { movesMillis ->
        val args =
            if (movesMillis.isEmpty()) "noMoves" else movesMillis.joinToString(separator = ",") //csv long values, composable navigation model seem not to work with long arrays
        navController.navigate("Stats/$args")
    }
}