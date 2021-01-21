package com.example.chessclock

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.chessclock.feature.clock.ClockScreen
import com.example.chessclock.feature.clock.ClockViewModel
import com.example.chessclock.feature.clock.ClockViewModel_AssistedFactory
import com.example.chessclock.feature.clock.InitialData
import com.example.chessclock.navigation.Actions
import com.example.chessclock.navigation.Routes
import com.example.chessclock.ui.ChessClockTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var clockViewModelFactory: ClockViewModel_AssistedFactory
    private val timerViewModel: TimerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            ChessClockTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    val actions = remember(navController) { Actions(navController) }
                    NavHost(
                        navController = navController,
                        startDestination = Routes.TimerChooserRoute
                    ) {
                        composable(Routes.TimerChooserRoute) {
                            TimerChooser(
                                actions = actions,
                                timerViewModel = timerViewModel
                            )
                        }
                        composable(Routes.CreateTimerRoute) {
                            CreateTimer()
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

                            //todo it is not bind to lifecycle - fix it
                            val clockViewModel = clockViewModelFactory.create(
                                SavedStateHandle(
                                    mapOf(
                                        ClockViewModel.SAVED_STATE_HANDLE_KEY to initialData
                                    )
                                )
                            )

                            ClockScreen(clockViewModel = clockViewModel)
                        }
                    }
                }
            }
        }
    }
}