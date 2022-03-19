package com.ledwon.jakub.chessclock.data.persistance

import android.content.Context
import android.content.ContextWrapper
import org.koin.dsl.module

val persistenceModule = module {

    single {
        val context: Context = get()
        val name = context.packageName + "_preferences"
        context.getSharedPreferences(name, ContextWrapper.MODE_PRIVATE)
    }

    single {
        PrepopulateDataStore(context = get())
    }
    single {
        SettingsDataStore(context = get())
    }
    single {
        InteractionCounterDataStore(context = get())
    }
    single {
        InAppReviewDataStore(context = get())
    }
}