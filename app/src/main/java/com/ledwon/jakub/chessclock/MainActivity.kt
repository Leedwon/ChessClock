package com.ledwon.jakub.chessclock

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.AppDarkTheme
import com.ledwon.jakub.chessclock.di.provideNavViewModel
import com.ledwon.jakub.chessclock.feature.choose_clock.ChooseClockScreen
import com.ledwon.jakub.chessclock.feature.choose_clock.ChooseClockViewModel
import com.ledwon.jakub.chessclock.feature.clock.ClockScreen
import com.ledwon.jakub.chessclock.feature.clock.ClockViewModel
import com.ledwon.jakub.chessclock.feature.clock.model.ClockInitialData
import com.ledwon.jakub.chessclock.feature.clock_preview.ClockPreviewScreen
import com.ledwon.jakub.chessclock.feature.clock_preview.ClockPreviewViewModel
import com.ledwon.jakub.chessclock.feature.create_timer.CreateTimerScreen
import com.ledwon.jakub.chessclock.feature.create_timer.CreateTimerViewModel
import com.ledwon.jakub.chessclock.feature.settings.SettingsScreen
import com.ledwon.jakub.chessclock.feature.settings.SettingsViewModel
import com.ledwon.jakub.chessclock.feature.stats.StatsScreen
import com.ledwon.jakub.chessclock.feature.stats.StatsViewModel
import com.ledwon.jakub.chessclock.navigation.NavigationActions
import com.ledwon.jakub.chessclock.navigation.Routes
import com.ledwon.jakub.chessclock.ui.ChessClockTheme
import com.ledwon.jakub.chessclock.util.LocalIsDarkMode
import com.ledwon.jakub.chessclock.util.LocalNavController
import com.ledwon.jakub.chessclock.util.LocalWindowProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@ExperimentalFoundationApi
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val appDarkThemeState = mainViewModel.appDarkTheme.collectAsState(AppDarkTheme.SystemDefault)
            val appColorThemeState = mainViewModel.appColorTheme.collectAsState(AppColorThemeType.DarkGreen)

            val isDarkTheme = when (appDarkThemeState.value) {
                AppDarkTheme.Light -> false
                AppDarkTheme.Dark -> true
                AppDarkTheme.SystemDefault -> isSystemInDarkTheme()
            }

            ChessClockTheme(
                darkTheme = isDarkTheme,
                colorTheme = appColorThemeState.value.value
            ) {
                window.statusBarColor =
                    appColorThemeState.value.value.colorTheme.darkColors.primaryVariant.toArgb()

                CompositionLocalProvider(
                    LocalIsDarkMode provides isDarkTheme,
                    LocalWindowProvider provides window
                ) {
                    Surface(color = MaterialTheme.colors.background) {
                        val navController = rememberNavController()
                        val actions = remember(navController) { NavigationActions(navController) }
                        CompositionLocalProvider(LocalNavController provides navController) {
                            NavHost(
                                navController = navController,
                                startDestination = Routes.TimerChooserRoute
                            ) {
                                composable(Routes.TimerChooserRoute) {
                                    val chooseClockViewModel: ChooseClockViewModel =
                                        provideNavViewModel()
                                    ChooseClockScreen(
                                        navigationActions = actions,
                                        chooseClockViewModel = chooseClockViewModel
                                    )
                                }
                                composable(Routes.SettingsRoute) {
                                    val settingsViewModel: SettingsViewModel = provideNavViewModel()
                                    SettingsScreen(
                                        navigationActions = actions,
                                        settingsViewModel = settingsViewModel
                                    )
                                }
                                composable(Routes.CreateTimerRoute) {
                                    val createTimerViewModel: CreateTimerViewModel = provideNavViewModel()
                                    CreateTimerScreen(
                                        navigationActions = actions,
                                        createTimerViewModel = createTimerViewModel
                                    )
                                }
                                composable(
                                    Routes.ClockRoute,
                                    arguments = listOf(navArgument(Routes.ClockArgs.WhiteSeconds) {
                                        type = NavType.IntType
                                    }, navArgument(Routes.ClockArgs.BlackSeconds) {
                                        type = NavType.IntType
                                    }, navArgument(Routes.ClockArgs.WhiteIncrementSeconds) {
                                        type = NavType.IntType
                                    }, navArgument(Routes.ClockArgs.BlackIncrementSeconds) {
                                        type = NavType.IntType
                                    })
                                ) { navBackStackEntry ->
                                    val whiteSeconds = navBackStackEntry.arguments!!.getInt(Routes.ClockArgs.WhiteSeconds)
                                    val blackSeconds = navBackStackEntry.arguments!!.getInt(Routes.ClockArgs.BlackSeconds)
                                    val whiteIncrementSeconds =
                                        navBackStackEntry.arguments!!.getInt(Routes.ClockArgs.WhiteIncrementSeconds)
                                    val blackIncrementSeconds =
                                        navBackStackEntry.arguments!!.getInt(Routes.ClockArgs.BlackIncrementSeconds)

                                    val initialData = ClockInitialData(
                                        whiteSeconds = whiteSeconds,
                                        blackSeconds = blackSeconds,
                                        whiteIncrementSeconds = whiteIncrementSeconds,
                                        blackIncrementSeconds = blackIncrementSeconds
                                    )

                                    val clockViewModel: ClockViewModel = provideNavViewModel(parameters = { parametersOf(initialData) })

                                    ClockScreen(
                                        actions = actions,
                                        clockViewModel = clockViewModel
                                    )
                                }
                                composable(
                                    Routes.ClockPreviewRoute,
                                    arguments = listOf(navArgument(Routes.ClockPreviewArgs.ClockDisplayId) {
                                        type = NavType.StringType
                                    })
                                ) { navBackStackEntry ->
                                    val clockPreviewName = navBackStackEntry.arguments!!.getString(Routes.ClockPreviewArgs.ClockDisplayId)

                                    val clockPreviewViewModel: ClockPreviewViewModel =
                                        provideNavViewModel(parameters = {
                                            parametersOf(
                                                clockPreviewName
                                            )
                                        })

                                    ClockPreviewScreen(
                                        navigationActions = actions,
                                        clockPreviewViewModel = clockPreviewViewModel
                                    )
                                }
                                composable(
                                    Routes.StatsRoute,
                                    arguments = listOf(navArgument(Routes.StatsArgs.MovesMillisCsv) { type = NavType.StringType })
                                ) { navBackStackEntry ->
                                    val movesCsv = navBackStackEntry.arguments!!.getString(Routes.StatsArgs.MovesMillisCsv)
                                    val movesMillis = movesCsv.orEmpty().split(",").mapNotNull { it.toLongOrNull() }

                                    val statsViewModel: StatsViewModel = provideNavViewModel(parameters = { parametersOf(movesMillis) })

                                    StatsScreen(
                                        actions = actions,
                                        viewModel = statsViewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}