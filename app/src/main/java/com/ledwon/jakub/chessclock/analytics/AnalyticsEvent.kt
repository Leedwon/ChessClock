package com.ledwon.jakub.chessclock.analytics

import android.os.Bundle
import androidx.core.os.bundleOf
import com.ledwon.jakub.chessclock.data.model.Timer
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

    data class OpenClockFromChooseTimer(val timer: Timer) : AnalyticsEvent() {
        override val eventName: String = "OpenClock"
        override val params: Bundle = timer.toBundle().apply { putString(clockOpenedFromKey, "Choose Timer") }
    }

    data class AddClock(val timer: Timer) : AnalyticsEvent() {
        override val eventName: String = "AddClock"
        override val params: Bundle = timer.toBundle()
    }

    data class OpenAndAddClock(val timer: Timer) : AnalyticsEvent() {
        override val eventName: String = "OpenAndAddClock"
        override val params: Bundle = timer.toBundle()
    }

    data class OpenClockFromCreateTimer(val timer: Timer) : AnalyticsEvent() {
        override val eventName: String = "OpenClock"
        override val params: Bundle = timer.toBundle().apply { putString(clockOpenedFromKey, "Create Timer") }
    }

    data class RemoveClock(val clock: Timer) : AnalyticsEvent() {
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

    protected fun Timer.toBundle() = bundleOf(
        "whiteSeconds" to this.whiteClockTime.secondsSum,
        "blackSeconds" to this.blackClockTime.secondsSum,
        "whiteIncrement" to this.whiteClockTime.increment,
        "blackIncrement" to this.blackClockTime.increment
    )
}