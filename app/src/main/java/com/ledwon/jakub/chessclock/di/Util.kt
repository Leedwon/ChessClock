package com.ledwon.jakub.chessclock.di

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientViewModelStoreOwner
import androidx.lifecycle.ViewModel
import com.ledwon.jakub.chessclock.util.AmbientNavController
import org.koin.androidx.compose.getKoin
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified VM : ViewModel> provideNavViewModel(
    noinline parameters: ParametersDefinition? = null
): VM {
    val store = AmbientNavController.current.currentBackStackEntry?.viewModelStore
        ?: AmbientViewModelStoreOwner.current.viewModelStore
    return getKoin().getViewModel(
        owner = {
            ViewModelOwner.Companion.from(
                store = store
            )
        },
        parameters = parameters
    )
}