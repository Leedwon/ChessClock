package com.ledwon.jakub.chessclock.feature.clock

import app.cash.turbine.test
import com.ledwon.jakub.chessclock.analytics.AnalyticsManager
import com.ledwon.jakub.chessclock.beEqualTo
import com.ledwon.jakub.chessclock.data.repository.SettingsRepository
import com.ledwon.jakub.chessclock.feature.clock.model.ClockState
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor.Black
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerColor.White
import com.ledwon.jakub.chessclock.feature.clock.model.PlayerDisplay
import com.ledwon.jakub.chessclock.feature.common.ClockDisplay
import com.ledwon.jakub.chessclock.should
import com.ledwon.jakub.chessclock.util.toDeferrableString
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClockViewModelTest {

    @MockK
    private val settingsRepository = mockk<SettingsRepository>(relaxUnitFun = true)

    @MockK
    private val analyticsManager = mockk<AnalyticsManager>(relaxUnitFun = true)

    @MockK
    private val positionRandomizer = mockk<PositionRandomizer>()

    private var millis = 0L

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        millis = 0L

        mockPositionRandomizationEnabled(true)
        mockPulsationEnabled(true)
        mockPositionRandomizer()
        mockClockDisplay()
    }

    @Test
    fun `should have correct initial state`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.clockDisplay.test { awaitItem().should.beEqualTo(ClockDisplay.OwnPlayerTimeClock(180f to 0f)) }
        vm.pulsationEnabled.test { awaitItem().should.beEqualTo(true) }
        vm.state.test { awaitItem().should.beEqualTo(createState()) }
    }

    @Test
    fun `should randomize positions`() = vmTestCase {
        every { settingsRepository.randomizePosition } returns flowOf(true).flowOn(testDispatcher)

        mockPositionRandomizer(
            listOf(
                White to Black,
                Black to White,
                White to Black,
                Black to White,
                White to Black
            )
        )

        //pausing because randomization is done in vm's init block, we want to first collect state (by .test) and then resume
        testDispatcher.pauseDispatcher()
        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())

            testDispatcher.resumeDispatcher()
            clockState = ClockState.RandomizingPositions
            awaitItem().should.beEqualTo(createState())

            playersDisplayOrder = Black to White
            awaitItem().should.beEqualTo(createState())

            playersDisplayOrder = White to Black
            awaitItem().should.beEqualTo(createState())

            playersDisplayOrder = Black to White
            awaitItem().should.beEqualTo(createState())

            playersDisplayOrder = White to Black
            awaitItem().should.beEqualTo(createState())

            clockState = ClockState.BeforeStarted
            awaitItem().should.beEqualTo(createState())
        }
    }

    @Test
    fun `should correctly stop position randomization`() = vmTestCase {
        val delayDispatcher = TestCoroutineDispatcher()
        every { settingsRepository.randomizePosition } returns flowOf(true)
            .onEach { delay(100) }
            .flowOn(delayDispatcher)

        val positions = MutableStateFlow(White to Black)

        every { positionRandomizer.randomizePositions() } returns positions

        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())
            delayDispatcher.advanceTimeBy(100)
            clockState = ClockState.RandomizingPositions
            awaitItem().should.beEqualTo(createState())

            positions.value = Black to White
            playersDisplayOrder = Black to White
            awaitItem().should.beEqualTo(createState())

            positions.value = White to Black
            playersDisplayOrder = White to Black
            awaitItem().should.beEqualTo(createState())

            positions.value = Black to White
            playersDisplayOrder = Black to White
            awaitItem().should.beEqualTo(createState())

            vm.cancelRandomization()
            positions.value = White to Black

            clockState = ClockState.BeforeStarted
            awaitItem().should.beEqualTo(createState())
        }
    }

    @Test
    fun `clock click should start game`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())

            vm.clockClicked(white)

            clockState = ClockState.Running
            white = white.copy(isActive = true)

            awaitItem().should.beEqualTo(createState())
        }
    }

    @Test
    fun `clock click should change players`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())

            vm.clockClicked(white)

            clockState = ClockState.Running
            white = white.copy(isActive = true)

            awaitItem().should.beEqualTo(createState())

            advanceTimeBy(100)

            white = white.copy(
                text = "00:59".toDeferrableString(),
                percentageLeft = 59_900F / 60_000F
            )
            awaitItem().should.beEqualTo(createState())

            vm.clockClicked(white)

            white = white.copy(isActive = false)
            black = black.copy(isActive = true)

            awaitItem().should.beEqualTo(createState())

            vm.clockClicked(white) //should not emit new state, only current player can click on clock
        }
    }

    @Test
    fun `should correctly swap sides`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())

            vm.swapSides()

            playersDisplayOrder = Black to White
            awaitItem().should.beEqualTo(createState())
        }
    }

    @Test
    fun `should correctly restart game`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())

            vm.clockClicked(white)

            clockState = ClockState.Running
            white = white.copy(isActive = true)

            awaitItem().should.beEqualTo(createState())

            advanceTimeBy(100)

            white = white.copy(
                text = "00:59".toDeferrableString(),
                percentageLeft = 59_900F / 60_000F
            )
            awaitItem().should.beEqualTo(createState())

            vm.pauseClock()

            clockState = ClockState.Paused
            awaitItem().should.beEqualTo(createState())

            vm.restartGame()

            clockState = ClockState.BeforeStarted
            white = white.copy(isActive = false)
            awaitItem().should.beEqualTo(createState())

            white = PlayerDisplay.White(
                text = "01:00".toDeferrableString(),
                percentageLeft = 1F,
                isActive = false
            )
            awaitItem().should.beEqualTo(createState())
        }
    }

    @Test
    fun `should correctly pause and resume`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.state.test {
            awaitItem().should.beEqualTo(createState())

            vm.clockClicked(white)

            clockState = ClockState.Running
            white = white.copy(isActive = true)

            awaitItem().should.beEqualTo(createState())

            advanceTimeBy(100)

            white = white.copy(
                text = "00:59".toDeferrableString(),
                percentageLeft = 59_900F / 60_000F
            )
            awaitItem().should.beEqualTo(createState())

            vm.pauseClock()

            clockState = ClockState.Paused
            awaitItem().should.beEqualTo(createState())

            advanceTimeBy(100)

            vm.resumeClock()

            clockState = ClockState.Running
            awaitItem().should.beEqualTo(createState())

            advanceTimeBy(100)

            white = white.copy(percentageLeft = 59_800F / 60_000F)
            awaitItem().should.beEqualTo(createState())
        }
    }

    @Test
    fun `should correctly send command with opening stats screen`() = vmTestCase {
        mockPositionRandomizationEnabled(false)

        val vm = createViewModel()

        vm.command.test {
            vm.clockClicked(white)
            advanceTimeBy(100)
            vm.pauseClock()
            advanceTimeBy(100)
            vm.resumeClock()
            advanceTimeBy(100)
            vm.clockClicked(white)
            vm.pauseClock()
            vm.showStats()

            awaitItem().should.beEqualTo(ClockViewModel.Command.NavigateToStats(listOf(200L)))
        }
    }

    private class VmTestCase {
        var white: PlayerDisplay.White = PlayerDisplay.White(
            text = "01:00".toDeferrableString(),
            percentageLeft = 1.0f,
            isActive = false,
        )

        var black: PlayerDisplay.Black = PlayerDisplay.Black(
            text = "01:00".toDeferrableString(),
            percentageLeft = 1.0f,
            isActive = false,
        )

        var playersDisplayOrder = White to Black

        private fun displayFor(playerColor: PlayerColor) = when (playerColor) {
            White -> white
            Black -> black
        }

        var clockState = ClockState.BeforeStarted

        fun createState() = ClockViewModel.State(
            playersDisplay = displayFor(playersDisplayOrder.first) to displayFor(playersDisplayOrder.second),
            clockState = clockState
        )
    }

    private fun vmTestCase(testBlock: suspend VmTestCase.() -> Unit) = runBlocking {
        val vmTestCase = VmTestCase()
        testBlock(vmTestCase)
    }

    private fun createViewModel(
        white: Player = Player(60 * 1_000L),
        black: Player = Player(60 * 1_000L),
        currentMillisProvider: () -> Long = { millis }
    ): ClockViewModel {
        val game = Game(
            white = white,
            black = black,
            movesTracker = MovesTracker(currentMillisProvider),
            defaultDispatcher = testDispatcher,
            currentTimeMillisProvider = currentMillisProvider,
            intervalMillis = 100,
        )

        return ClockViewModel(
            game = game,
            settingsRepository = settingsRepository,
            analyticsManager = analyticsManager,
            positionRandomizer = positionRandomizer,
            defaultDispatcher = testDispatcher,
        )
    }

    private fun mockPositionRandomizer(values: List<Pair<PlayerColor, PlayerColor>> = listOf(White to Black)) {
        every { positionRandomizer.randomizePositions() } returns values.asFlow().flowOn(testDispatcher)
    }

    private fun mockPositionRandomizationEnabled(value: Boolean) {
        every { settingsRepository.randomizePosition } returns MutableStateFlow(value)
    }

    private fun mockClockDisplay(clockDisplay: ClockDisplay = ClockDisplay.OwnPlayerTimeClock(180f to 0f)) {
        every { settingsRepository.clockDisplay } returns MutableStateFlow(clockDisplay)
    }

    private fun mockPulsationEnabled(enabled: Boolean) {
        every { settingsRepository.pulsationEnabled } returns MutableStateFlow(enabled)
    }

    private fun advanceTimeBy(period: Long) {
        millis += period
        testDispatcher.advanceTimeBy(period)
    }
}
