package com.ledwon.jakub.chessclock.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.ledwon.jakub.chessclock.util.LocalNavController
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.koin.getViewModel
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.ParametersDefinition

@Composable
inline fun <reified VM : ViewModel> provideNavViewModel(
    noinline parameters: ParametersDefinition? = null
): VM {
    val store = LocalNavController.current?.currentBackStackEntry?.viewModelStore
        ?: LocalViewModelStoreOwner.current.viewModelStore
    val koin = getKoin()
    return remember {
        koin.getViewModel(
            owner = {
                ViewModelOwner.Companion.from(
                    store = store
                )
            },
            parameters = parameters
        )
    }
}

@Composable
fun getKoin(): Koin = remember {
    GlobalContext.get()
}