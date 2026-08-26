package com.ledwon.jakub.chessclock.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.ParametersDefinition


@Composable
inline fun <reified VM : ViewModel> provideNavViewModel(
    noinline parameters: ParametersDefinition? = null
): VM {
    return koinViewModel(parameters = parameters)
}
