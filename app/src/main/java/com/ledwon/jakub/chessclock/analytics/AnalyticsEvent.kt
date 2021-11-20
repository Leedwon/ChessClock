package com.ledwon.jakub.chessclock.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.ledwon.jakub.chessclock.model.Clock
import com.ledwon.jakub.chessclock.data.repository.AppColorThemeType
import com.ledwon.jakub.chessclock.data.repository.ClockTypesRepository

sealed class AnalyticsEvent {
    abstract val eventName: String
    open val params: Bundle? = null

    companion object {
        private const val clockOpenedFromKey = "openedFrom"
    }

    object OpenSettings : AnalyticsEvent() {
        override val eventName: String = "OpenSettings"
    }

    object OpenCreateClock : AnalyticsEvent() {
        override val eventName: String = "OpenCreateClock"
    }

    object OpenStats : AnalyticsEvent() {
        override val eventName: String = "OpenStats"
    }

    data class OpenClockFromChooseClock(val clock: Clock) : AnalyticsEvent() {
        override val eventName: String = "OpenClock"
        override val params: Bundle = clock.toBundle().apply { putString(clockOpenedFromKey, "Choose clock") }
    }

    data class AddClock(val clock: Clock) : AnalyticsEvent() {
        override val eventName: String = "AddClock"
        override val params: Bundle = clock.toBundle()
    }

    data class OpenAndAddClock(val clock: Clock) : AnalyticsEvent() {
        override val eventName: String = "OpenAndAddClock"
        override val params: Bundle = clock.toBundle()
    }

    data class OpenClockFromCreateClock(val clock: Clock) : AnalyticsEvent() {
        override val eventName: String = "OpenClock"
        override val params: Bundle = clock.toBundle().apply { putString(clockOpenedFromKey, "Create clock") }
    }

    data class RemoveClock(val clock: Clock) : AnalyticsEvent() {
        override val eventName: String = "RemoveClock"
        override val params: Bundle = clock.toBundle()
    }

    data class UpdateClockType(val clockType: ClockTypesRepository.NamedClockDisplayType) : AnalyticsEvent() {
        override val eventName: String = "UpdateClockType"
        override val params: Bundle = bundleOf("clockType" to clockType.name)
    }

    data class UpdateAppColorTheme(val colorThemeType: AppColorThemeType) : AnalyticsEvent() {
        override val eventName: String = "UpdateAppColorTheme"
        override val params: Bundle = bundleOf("colorThemeType" to colorThemeType.name)
    }

    data class UpdatePulsationEnabled(val pulsationEnabled: Boolean) : AnalyticsEvent() {
        override val eventName: String = "UpdatePulsationEnabled"
        override val params: Bundle = bundleOf("pulsationEnabled" to pulsationEnabled)
    }

    data class UpdateRandomizePlayersPositions(val randomizePosition: Boolean) : AnalyticsEvent() {
        override val eventName: String  = "UpdateRandomizePlayersPositions"
        override val params: Bundle = bundleOf("randomizePositions" to randomizePosition)
    }

    protected fun Clock.toBundle() = bundleOf(
        "whiteSeconds" to this.whitePlayerTime.secondsSum,
        "blackSeconds" to this.blackPlayerTime.secondsSum,
        "whiteIncrement" to this.whitePlayerTime.increment,
        "blackIncrement" to this.blackPlayerTime.increment
    )
}