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
                        composable(
                            Routes.ClockRoute,
                            arguments = listOf(navArgument(Routes.ClockRouteArgs.WhiteMinutes) {
                                type = NavType.IntType
                            }, navArgument(Routes.ClockRouteArgs.BlackMinutes) {
                                type = NavType.IntType
                            })
                        ) { navBackStackEntry ->
                            val whiteMinutes =
                                navBackStackEntry.arguments!!.getInt(Routes.ClockRouteArgs.WhiteMinutes)
                            val blackMinutes =
                                navBackStackEntry.arguments!!.getInt(Routes.ClockRouteArgs.BlackMinutes)

                            val initialData = InitialData(
                                whiteMinutes = whiteMinutes,
                                blackMinutes = blackMinutes
                            )

                            val clockViewModel = clockViewModelFactory.create(
                                SavedStateHandle(
                                    mapOf(
                                        ClockViewModel.SAVED_STATE_HANDLE_KEY to initialData
                                    )
                                )
                            )
                            Clock(clockViewModel = clockViewModel)
                        }
                    }
                }
            }
        }
    }
}