package com.ledwon.jakub.chessclock.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import com.ledwon.jakub.chessclock.util.LocalNavController
import org.koin.androidx.compose.getKoin
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition


@Composable
inline fun <reified VM : ViewModel> provideNavViewModel(
    noinline parameters: ParametersDefinition? = null
): VM {
    val storeOwner = LocalNavController.current.currentBackStackEntry as ViewModelStoreOwner?
        ?: error("back stack is empty - can't provide nav graph vm")
    val koin = getKoin()
    return remember {
        koin.getViewModel(
            owner = { ViewModelOwner.Companion.from(storeOwner = storeOwner) },
            parameters = parameters
        )
    }
}