package com.lrds.androidroadapp

import app.cash.turbine.test
import com.lrds.androidroadapp.data.ProgressJsonCodec
import com.lrds.androidroadapp.data.ProgressRepository
import com.lrds.androidroadapp.data.ProgressStorage
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.UserProgressState
import com.lrds.androidroadapp.platform.DeepLinkTarget
import com.lrds.androidroadapp.platform.PlatformBridge
import com.lrds.androidroadapp.presentation.ActiveTab
import com.lrds.androidroadapp.presentation.DroidQuestIntent
import com.lrds.androidroadapp.presentation.DroidQuestViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private class FakeProgressStorage(
    var stored: UserProgressState? = null,
) : ProgressStorage {
    override val storageLabel: String = "In-Memory Test Storage"
    override val savedStatus: SaveStatus = SaveStatus.SAVED_LOCAL
    var saveCalls: Int = 0

    override suspend fun load(): UserProgressState? = stored

    override suspend fun save(state: UserProgressState) {
        saveCalls += 1
        stored = state
    }
}

private data class StudySyncCall(
    val enabled: Boolean,
    val daysOfWeek: Set<Int>,
    val hour: Int,
    val minute: Int,
    val nextQuestId: String?,
    val nextQuestTitle: String?,
)

private class FakePlatformBridge(
    var grantPermission: Boolean = true,
) : PlatformBridge {
    override val supportsDailyReminders: Boolean = true
    override val supportsNativeFilePicker: Boolean = true

    private val _pendingDeepLink = MutableStateFlow<DeepLinkTarget?>(null)
    override val pendingDeepLink: StateFlow<DeepLinkTarget?> = _pendingDeepLink.asStateFlow()

    val openedUrls = mutableListOf<String>()
    val dailyReviewSyncCalls = mutableListOf<Pair<Boolean, Int>>()
    val studySyncCalls = mutableListOf<StudySyncCall>()

    fun emitDeepLink(target: DeepLinkTarget) {
        _pendingDeepLink.value = target
    }

    override fun consumePendingDeepLink() {
        _pendingDeepLink.value = null
    }

    override fun openUrl(url: String) {
        openedUrls += url
    }

    override fun exportProgressJson(
        fileName: String,
        jsonContent: String,
        onResult: (Boolean, String) -> Unit,
    ) {
        onResult(true, "Exported $fileName")
    }

    override fun pickProgressJson(onPicked: (String?) -> Unit) {
        onPicked(null)
    }

    override fun syncDailyReviewReminder(
        enabled: Boolean,
        hour: Int,
        dueCount: Int,
        onResult: (Boolean, String) -> Unit,
    ) {
        dailyReviewSyncCalls += enabled to hour
        val effective = enabled && grantPermission
        onResult(effective, if (effective) "Reminder enabled" else "Reminder disabled")
    }

    override fun syncStudySessionReminder(
        enabled: Boolean,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int,
        nextQuestId: String?,
        nextQuestTitle: String?,
        onResult: (Boolean, String) -> Unit,
    ) {
        studySyncCalls += StudySyncCall(enabled, daysOfWeek, hour, minute, nextQuestId, nextQuestTitle)
        val effective = enabled && grantPermission && daysOfWeek.isNotEmpty()
        onResult(
            effective,
            when {
                !enabled -> "Study schedule reminders paused."
                !grantPermission -> "Notification permission denied."
                daysOfWeek.isEmpty() -> "Select at least one day."
                else -> "Study schedule set!"
            },
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class DroidQuestViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val fixedInstant = Instant.parse("2026-11-22T12:00:00Z")
    private val fixedClock = object : Clock {
        override fun now(): Instant = fixedInstant
    }

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `completing topic and recording review updates XP stage and review queues via Turbine`() =
        runTest(testDispatcher) {
        val storage = FakeProgressStorage()
        val bridge = FakePlatformBridge()
        val repo = ProgressRepository(
            storage = storage,
            scope = backgroundScope,
            clock = fixedClock,
            timeZoneProvider = { TimeZone.UTC },
            saveDebounceMs = 0L,
        )
        val viewModel = DroidQuestViewModel(
            repository = repo,
            platformBridge = bridge,
            clock = fixedClock,
            timeZoneProvider = { TimeZone.UTC },
            toastDurationMs = 5_000L,
        )

        viewModel.uiState.test {
            val initial = awaitItem()
            // Initial state pre-populates the 3 SD Module 4 lessons (50 + 55 + 55 = 160 XP)
            assertEquals(3, initial.completedCount)
            assertEquals(160, initial.totalXp)
            assertEquals(1, initial.dueNow.size)
            assertEquals(2, initial.upcoming.size)

            // Wait for initial repository load/seed to settle
            advanceUntilIdle()
            val afterSeed = expectMostRecentItem()
            assertEquals(SaveStatus.SAVED_LOCAL, afterSeed.saveStatus)

            // Toggle first Android subtopic (+50 XP)
            viewModel.onIntent(DroidQuestIntent.ToggleTopicComplete("af-1-os-art"))
            advanceUntilIdle()
            val afterComplete = expectMostRecentItem()
            assertEquals(4, afterComplete.completedCount)
            assertEquals(210, afterComplete.totalXp)
            assertTrue(afterComplete.progress.topics["af-1-os-art"]?.completed == true)
            assertEquals(3, afterComplete.upcoming.size) // scheduled for tomorrow (+1d)

            // Record GOOD review on the due SD lesson (+25 XP bonus, stage 1 -> 2 = 3d interval)
            viewModel.onIntent(DroidQuestIntent.RecordReview("sd-course-1-intro", ReviewRating.GOOD))
            advanceUntilIdle()
            val afterReview = expectMostRecentItem()
            assertEquals(235, afterReview.totalXp)
            assertEquals(0, afterReview.dueNow.size)
            assertEquals(1, afterReview.totalReviewsCompleted)
            assertEquals(2, afterReview.progress.topics["sd-course-1-intro"]?.reviewStage)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `high yield toggle quest notes custom links start date and JSON import work cleanly`() =
        runTest(testDispatcher) {
        val storage = FakeProgressStorage()
        val bridge = FakePlatformBridge()
        val repo = ProgressRepository(
            storage = storage,
            scope = backgroundScope,
            clock = fixedClock,
            timeZoneProvider = { TimeZone.UTC },
            saveDebounceMs = 0L,
        )
        val viewModel = DroidQuestViewModel(
            repository = repo,
            platformBridge = bridge,
            clock = fixedClock,
            timeZoneProvider = { TimeZone.UTC },
        )

        viewModel.uiState.test {
            awaitItem()
            advanceUntilIdle()

            viewModel.onIntent(DroidQuestIntent.ToggleHighYield)
            viewModel.onIntent(DroidQuestIntent.SelectTab(ActiveTab.CHECKLIST))
            viewModel.onIntent(DroidQuestIntent.UpdateQuestNote("q1-android-fundamentals", "ART AOT+JIT notes"))
            viewModel.onIntent(
                DroidQuestIntent.AddCustomLink(
                    questId = "q1-android-fundamentals",
                    title = "My Video",
                    url = "youtube.com/watch?v=123",
                    type = ResourceType.YOUTUBE,
                )
            )
            viewModel.onIntent(DroidQuestIntent.UpdateStartDate("2026-11-12"))
            advanceUntilIdle()

            val updated = expectMostRecentItem()
            assertTrue(updated.highYieldOnly)
            assertEquals(ActiveTab.CHECKLIST, updated.activeTab)
            assertEquals("ART AOT+JIT notes", updated.progress.questNotes["q1-android-fundamentals"])
            val savedLink = updated.progress.customLinks["q1-android-fundamentals"]?.firstOrNull()
            assertNotNull(savedLink)
            assertEquals("https://youtube.com/watch?v=123", savedLink.url)
            assertEquals(11, updated.countdown.currentDay)

            // Export JSON, reset, then import back and assert restoration
            val backupJson = viewModel.getCurrentBackupJson()
            assertTrue(ProgressJsonCodec.decode(backupJson).isSuccess)

            viewModel.onIntent(DroidQuestIntent.ResetProgress)
            advanceUntilIdle()
            val afterReset = expectMostRecentItem()
            assertFalse(afterReset.progress.questNotes.containsKey("q1-android-fundamentals"))

            viewModel.onIntent(DroidQuestIntent.ImportBackupJsonRaw(backupJson))
            advanceUntilIdle()
            val afterImport = expectMostRecentItem()
            assertEquals("ART AOT+JIT notes", afterImport.progress.questNotes["q1-android-fundamentals"])
            assertEquals(11, afterImport.countdown.currentDay)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `study schedule reminders coexist with daily review reminders and handle permissions cleanly`() =
        runTest(testDispatcher) {
            val storage = FakeProgressStorage()
            val bridge = FakePlatformBridge(grantPermission = true)
            val repo = ProgressRepository(
                storage = storage,
                scope = backgroundScope,
                clock = fixedClock,
                timeZoneProvider = { TimeZone.UTC },
                saveDebounceMs = 0L,
            )
            val viewModel = DroidQuestViewModel(
                repository = repo,
                platformBridge = bridge,
                clock = fixedClock,
                timeZoneProvider = { TimeZone.UTC },
            )

            viewModel.uiState.test {
                awaitItem()
                advanceUntilIdle()

                // Editing days/time while paused persists without triggering platform permission prompt
                viewModel.onIntent(DroidQuestIntent.SetStudySchedule(false, setOf(1, 3, 5), 8, 30))
                advanceUntilIdle()
                assertEquals(0, bridge.studySyncCalls.size)
                val pausedState = expectMostRecentItem()
                assertFalse(pausedState.progress.studyReminderEnabled)
                assertEquals(setOf(1, 3, 5), pausedState.progress.studyReminderDays)
                assertEquals(8, pausedState.progress.studyReminderHour)
                assertEquals(30, pausedState.progress.studyReminderMinute)

                // Enabling Study Schedule schedules notifications with Next Recommended Quest metadata
                viewModel.onIntent(DroidQuestIntent.SetStudySchedule(true, setOf(1, 3, 5), 8, 30))
                advanceUntilIdle()
                val enabledState = expectMostRecentItem()
                assertTrue(enabledState.progress.studyReminderEnabled)
                assertEquals(1, bridge.studySyncCalls.size)
                assertEquals("q1-android-fundamentals", bridge.studySyncCalls.last().nextQuestId)
                assertNotNull(bridge.studySyncCalls.last().nextQuestTitle)

                // Enabling Daily Review Reminder works independently without touching Study Schedule
                viewModel.onIntent(DroidQuestIntent.SetDailyReminder(enabled = true, hour = 21))
                advanceUntilIdle()
                val bothEnabled = expectMostRecentItem()
                assertTrue(bothEnabled.progress.dailyReminderEnabled)
                assertEquals(21, bothEnabled.progress.reminderHour)
                assertTrue(bothEnabled.progress.studyReminderEnabled)
                assertEquals(setOf(1, 3, 5), bothEnabled.progress.studyReminderDays)

                // Deselecting last remaining day while enabled cancels study schedule cleanly
                viewModel.onIntent(DroidQuestIntent.SetStudySchedule(true, emptySet(), 8, 30))
                advanceUntilIdle()
                val clearedState = expectMostRecentItem()
                assertFalse(clearedState.progress.studyReminderEnabled)
                assertEquals(emptySet(), clearedState.progress.studyReminderDays)
                assertTrue(clearedState.progress.dailyReminderEnabled)

                // Re-enabling with empty days defaults back to Mon-Fri, and respects permission denial
                bridge.grantPermission = false
                viewModel.onIntent(DroidQuestIntent.SetStudySchedule(true, emptySet(), 19, 0))
                advanceUntilIdle()
                val deniedState = expectMostRecentItem()
                assertFalse(deniedState.progress.studyReminderEnabled)
                assertEquals(StudySchedule.DEFAULT_DAYS, deniedState.progress.studyReminderDays)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `study notification deep link opens Next Recommended Quest on Trail tab and consumes event`() =
        runTest(testDispatcher) {
            val storage = FakeProgressStorage()
            val bridge = FakePlatformBridge()
            val repo = ProgressRepository(
                storage = storage,
                scope = backgroundScope,
                clock = fixedClock,
                timeZoneProvider = { TimeZone.UTC },
                saveDebounceMs = 0L,
            )
            val viewModel = DroidQuestViewModel(
                repository = repo,
                platformBridge = bridge,
                clock = fixedClock,
                timeZoneProvider = { TimeZone.UTC },
            )

            viewModel.uiState.test {
                awaitItem()
                advanceUntilIdle()

                // Navigate away and open settings before notification tap arrives
                viewModel.onIntent(DroidQuestIntent.SelectTab(ActiveTab.TROPHIES))
                viewModel.onIntent(DroidQuestIntent.SetSettingsOpen(true))
                advanceUntilIdle()

                bridge.emitDeepLink(
                    DeepLinkTarget.OpenNextRecommendedQuest(preferredQuestId = "q1-android-fundamentals")
                )
                advanceUntilIdle()

                val afterDeepLink = expectMostRecentItem()
                assertEquals(ActiveTab.TRAIL, afterDeepLink.activeTab)
                assertFalse(afterDeepLink.settingsOpen)
                assertEquals(afterDeepLink.nextRecommendedQuest?.id, afterDeepLink.selectedQuestId)
                assertNotNull(afterDeepLink.selectedQuest)
                assertNull(bridge.pendingDeepLink.value)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
