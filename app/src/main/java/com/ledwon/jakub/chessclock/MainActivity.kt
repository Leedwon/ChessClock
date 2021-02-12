package com.ledwon.jakub.chessclock

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.ledwon.jakub.chessclock.di.provideNavViewModel
import com.ledwon.jakub.chessclock.feature.choose_timer.ChooseTimerViewModel
import com.ledwon.jakub.chessclock.feature.choose_timer.ChooseTimerScreen
import com.ledwon.jakub.chessclock.feature.clock.ClockScreen
import com.ledwon.jakub.chessclock.feature.clock.ClockViewModel
import com.ledwon.jakub.chessclock.feature.clock.InitialData
import com.ledwon.jakub.chessclock.feature.create_timer.CreateTimerScreen
import com.ledwon.jakub.chessclock.feature.create_timer.CreateTimerViewModel
import com.ledwon.jakub.chessclock.navigation.Actions
import com.ledwon.jakub.chessclock.navigation.Routes
import com.ledwon.jakub.chessclock.ui.ChessClockTheme
import com.ledwon.jakub.chessclock.util.AmbientNavController
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            ChessClockTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    val actions = remember(navController) { Actions(navController) }
                    Providers(AmbientNavController provides navController) {
                        NavHost(
                            navController = navController,
                            startDestination = Routes.TimerChooserRoute
                        ) {
                            composable(Routes.TimerChooserRoute) {
                                val chooseTimerViewModel: ChooseTimerViewModel =
                                    provideNavViewModel()
                                ChooseTimerScreen(
                                    actions = actions,
                                    chooseTimerViewModel = chooseTimerViewModel
                                )
                            }
                            composable(Routes.CreateTimerRoute) {
                                val createTimerViewModel: CreateTimerViewModel =
                                    provideNavViewModel()
                                CreateTimerScreen(
                                    actions = actions,
                                    createTimerViewModel = createTimerViewModel
                                )
                            }
                            composable(
                                Routes.ClockRoute,
                                arguments = listOf(navArgument(Routes.ClockRouteArgs.WhiteSeconds) {
                                    type = NavType.IntType
                                }, navArgument(Routes.ClockRouteArgs.BlackSeconds) {
                                    type = NavType.IntType
                                }, navArgument(Routes.ClockRouteArgs.WhiteIncrementSeconds) {
                                    type = NavType.IntType
                                }, navArgument(Routes.ClockRouteArgs.BlackIncrementSeconds) {
                                    type = NavType.IntType
                                })
                            ) { navBackStackEntry ->
                                val whiteSeconds =
                                    navBackStackEntry.arguments!!.getInt(Routes.ClockRouteArgs.WhiteSeconds)
                                val blackSeconds =
                                    navBackStackEntry.arguments!!.getInt(Routes.ClockRouteArgs.BlackSeconds)
                                val whiteIncrementSeconds =
                                    navBackStackEntry.arguments!!.getInt(Routes.ClockRouteArgs.WhiteIncrementSeconds)
                                val blackIncrementSeconds =
                                    navBackStackEntry.arguments!!.getInt(Routes.ClockRouteArgs.BlackIncrementSeconds)

                                val initialData = InitialData(
                                    whiteSeconds = whiteSeconds,
                                    blackSeconds = blackSeconds,
                                    whiteIncrementSeconds = whiteIncrementSeconds,
                                    blackIncrementSeconds = blackIncrementSeconds
                                )

                                val clockViewModel: ClockViewModel =
                                    provideNavViewModel(parameters = { parametersOf(initialData) })

                                ClockScreen(clockViewModel = clockViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}