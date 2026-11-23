package com.lrds.androidroadapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lrds.androidroadapp.data.HapticKind
import com.lrds.androidroadapp.data.MutationOutcome
import com.lrds.androidroadapp.data.ProgressRepository
import com.lrds.androidroadapp.data.ToastTone
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.PillarStat
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.RankInfo
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ReviewDueItem
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.SprintCountdown
import com.lrds.androidroadapp.domain.model.StudyLogEntry
import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.domain.model.UserProgressState
import com.lrds.androidroadapp.platform.DeepLinkTarget
import com.lrds.androidroadapp.platform.PlatformBridge
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

enum class ActiveTab {
    TRAIL,
    CHECKLIST,
    REVIEWS,
    TROPHIES,
}

enum class ChecklistStatusFilter {
    ALL,
    TODO,
    DONE,
    REVIEW,
}

data class HeatmapDay(
    val dateKey: String,
    val dayOfMonth: String,
    val monthShort: String,
    val score: Int,
    val xp: Int,
)

data class ToastBanner(
    val id: Long,
    val message: String,
    val tone: ToastTone,
)

data class CelebrationEvent(
    val id: Long = 0L,
    val isBoss: Boolean = false,
)

private const val DEFAULT_TARGET_DAYS = 100
private const val DEFAULT_DAILY_PACE = 0.9
private const val REVIEW_BONUS_XP = 25
private const val PERCENT_SCALE = 100.0
private const val MONTH_SHORT_LENGTH = 3

data class DroidQuestUiState(
    val progress: UserProgressState,
    val saveStatus: SaveStatus,
    val storageLabel: String,
    val activeTab: ActiveTab = ActiveTab.TRAIL,
    val selectedQuestId: String? = null,
    val highYieldOnly: Boolean = false,
    val settingsOpen: Boolean = false,
    val completedCount: Int = 0,
    val totalCount: Int = RoadmapCatalog.totalTopics,
    val overallPct: Int = 0,
    val totalXp: Int = 0,
    val rankInfo: RankInfo = RoadmapCatalog.getRankForXp(0),
    val streak: Int = 0,
    val countdown: SprintCountdown = SprintCountdown(
        currentDay = 1,
        daysLeft = DEFAULT_TARGET_DAYS,
        dailyPace = DEFAULT_DAILY_PACE,
        timeProgressPct = 1,
    ),
    val pillarStats: List<PillarStat> = emptyList(),
    val dueNow: List<ReviewDueItem> = emptyList(),
    val upcoming: List<ReviewDueItem> = emptyList(),
    val nextRecommendedQuest: QuestModule? = null,
    val selectedQuest: QuestModule? = null,
    val totalReviewsCompleted: Int = 0,
    val heatmapDays: List<HeatmapDay> = emptyList(),
    val toast: ToastBanner? = null,
    val celebration: CelebrationEvent = CelebrationEvent(),
    val supportsDailyReminders: Boolean = false,
    val supportsNativeFilePicker: Boolean = false,
)

sealed interface DroidQuestIntent {
    data class SelectTab(val tab: ActiveTab) : DroidQuestIntent
    data object ToggleHighYield : DroidQuestIntent
    data class SelectQuest(val questId: String?) : DroidQuestIntent
    data class SetSettingsOpen(val open: Boolean) : DroidQuestIntent
    data class ToggleTopicComplete(val subtopicId: String) : DroidQuestIntent
    data class RecordReview(val subtopicId: String, val rating: ReviewRating) : DroidQuestIntent
    data class ToggleFlagForReview(val subtopicId: String) : DroidQuestIntent
    data class UpdateQuestNote(val questId: String, val note: String) : DroidQuestIntent
    data class AddCustomLink(
        val questId: String,
        val title: String,
        val url: String,
        val type: ResourceType,
    ) : DroidQuestIntent
    data class RemoveCustomLink(val questId: String, val linkId: String) : DroidQuestIntent
    data class UpdateStartDate(val startDate: String) : DroidQuestIntent
    data object ToggleSound : DroidQuestIntent
    data class SetThemeMode(val mode: ThemeMode) : DroidQuestIntent
    data class SetDailyReminder(val enabled: Boolean, val hour: Int) : DroidQuestIntent
    data class SetStudySchedule(
        val enabled: Boolean,
        val daysOfWeek: Set<Int>,
        val hour: Int,
        val minute: Int,
    ) : DroidQuestIntent
    data class OpenExternalUrl(val url: String) : DroidQuestIntent
    data object ExportBackupJson : DroidQuestIntent
    data object PickBackupJsonFile : DroidQuestIntent
    data class ImportBackupJsonRaw(val raw: String) : DroidQuestIntent
    data object ResetProgress : DroidQuestIntent
}

sealed interface DroidQuestEffect {
    data class TriggerHaptic(val kind: HapticKind) : DroidQuestEffect
}

private data class LocalUiOverrides(
    val activeTab: ActiveTab = ActiveTab.TRAIL,
    val selectedQuestId: String? = null,
    val highYieldOnly: Boolean = false,
    val settingsOpen: Boolean = false,
    val toast: ToastBanner? = null,
    val celebration: CelebrationEvent = CelebrationEvent(),
)

@OptIn(ExperimentalTime::class)
class DroidQuestViewModel(
    private val repository: ProgressRepository,
    private val platformBridge: PlatformBridge,
    private val clock: Clock = Clock.System,
    private val timeZoneProvider: () -> TimeZone = { TimeZone.currentSystemDefault() },
    private val toastDurationMs: Long = 3200L,
) : ViewModel() {
    private val overrides = MutableStateFlow(LocalUiOverrides())
    private val _effects = MutableSharedFlow<DroidQuestEffect>(extraBufferCapacity = 8)
    val effects: SharedFlow<DroidQuestEffect> = _effects.asSharedFlow()

    private var toastJob: Job? = null
    private var eventSeq: Long = 0L

    init {
        repository.loadInitial()
        observePlatformDeepLinks()
    }

    private fun observePlatformDeepLinks() {
        viewModelScope.launch {
            combine(platformBridge.pendingDeepLink, repository.saveStatus) { target, status ->
                if (target != null && status != SaveStatus.LOADING) target else null
            }.collect { target ->
                if (target != null) {
                    handleDeepLink(target)
                    platformBridge.consumePendingDeepLink()
                }
            }
        }
    }

    private fun handleDeepLink(target: DeepLinkTarget) {
        when (target) {
            is DeepLinkTarget.OpenNextRecommendedQuest -> {
                val nextQuestId = findNextRecommendedQuest(repository.state.value)?.id
                    ?: target.preferredQuestId?.takeIf { RoadmapCatalog.questsById.containsKey(it) }
                overrides.update {
                    it.copy(
                        activeTab = ActiveTab.TRAIL,
                        settingsOpen = false,
                        selectedQuestId = nextQuestId,
                    )
                }
            }
        }
    }

    val uiState: StateFlow<DroidQuestUiState> = combine(
        repository.state,
        repository.saveStatus,
        overrides,
    ) { progress, saveStatus, local ->
        deriveUiState(
            progress = progress,
            saveStatus = saveStatus,
            local = local,
            now = clock.now(),
            timeZone = timeZoneProvider(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = deriveUiState(
            progress = repository.state.value,
            saveStatus = repository.saveStatus.value,
            local = overrides.value,
            now = clock.now(),
            timeZone = timeZoneProvider(),
        ),
    )

    fun onIntent(intent: DroidQuestIntent) {
        when (intent) {
            is DroidQuestIntent.SelectTab -> overrides.update { it.copy(activeTab = intent.tab) }
            DroidQuestIntent.ToggleHighYield -> overrides.update { it.copy(highYieldOnly = !it.highYieldOnly) }
            is DroidQuestIntent.SelectQuest -> overrides.update { it.copy(selectedQuestId = intent.questId) }
            is DroidQuestIntent.SetSettingsOpen -> overrides.update { it.copy(settingsOpen = intent.open) }
            is DroidQuestIntent.ToggleTopicComplete ->
                applyOutcome(repository.toggleTopicComplete(intent.subtopicId))
            is DroidQuestIntent.RecordReview ->
                applyOutcome(repository.recordReview(intent.subtopicId, intent.rating))
            is DroidQuestIntent.ToggleFlagForReview ->
                applyOutcome(repository.toggleFlagForReview(intent.subtopicId))
            is DroidQuestIntent.UpdateQuestNote -> repository.updateQuestNote(intent.questId, intent.note)
            is DroidQuestIntent.AddCustomLink ->
                applyOutcome(repository.addCustomLink(intent.questId, intent.title, intent.url, intent.type))
            is DroidQuestIntent.RemoveCustomLink -> repository.removeCustomLink(intent.questId, intent.linkId)
            is DroidQuestIntent.UpdateStartDate -> applyOutcome(repository.updateStartDate(intent.startDate))
            DroidQuestIntent.ToggleSound -> applyOutcome(repository.toggleSound())
            is DroidQuestIntent.SetThemeMode -> applyOutcome(repository.setThemeMode(intent.mode))
            is DroidQuestIntent.SetDailyReminder -> handleSetDailyReminder(intent.enabled, intent.hour)
            is DroidQuestIntent.SetStudySchedule -> handleSetStudySchedule(intent)
            is DroidQuestIntent.OpenExternalUrl -> platformBridge.openUrl(intent.url)
            DroidQuestIntent.ExportBackupJson -> handleExportBackupJson()
            DroidQuestIntent.PickBackupJsonFile -> handlePickBackupJsonFile()
            is DroidQuestIntent.ImportBackupJsonRaw -> handleImportBackupJson(intent.raw)
            DroidQuestIntent.ResetProgress -> handleResetProgress()
        }
    }

    private fun handleSetDailyReminder(enabled: Boolean, hour: Int) {
        val dueCount = uiState.value.dueNow.size
        platformBridge.syncDailyReviewReminder(
            enabled = enabled,
            hour = hour,
            dueCount = dueCount,
        ) { granted, message ->
            val effectiveEnabled = enabled && granted
            repository.setDailyReminder(effectiveEnabled, hour)
            showToast(
                message = message,
                tone = if (effectiveEnabled) ToastTone.VIOLET else ToastTone.AMBER,
            )
        }
    }

    private fun handleExportBackupJson() {
        val todayKey = RoadmapCatalog.getTodayKey(clock.now(), timeZoneProvider())
        val fileName = "droidquest-progress-$todayKey.json"
        val content = repository.exportProgressJson()
        platformBridge.exportProgressJson(fileName, content) { ok, message ->
            showToast(message, if (ok) ToastTone.EMERALD else ToastTone.ROSE)
        }
    }

    private fun handlePickBackupJsonFile() {
        platformBridge.pickProgressJson { raw ->
            if (!raw.isNullOrBlank()) {
                handleImportBackupJson(raw)
            }
        }
    }

    private fun handleImportBackupJson(raw: String) {
        val outcome = repository.importProgressJson(raw)
        applyOutcome(outcome)
        if (outcome.success) {
            syncRemindersAfterRestore(repository.state.value)
            overrides.update { it.copy(settingsOpen = false) }
        }
    }

    private fun handleResetProgress() {
        val outcome = repository.resetProgress()
        applyOutcome(outcome)
        cancelAllPlatformRemindersQuietly()
        overrides.update { it.copy(settingsOpen = false) }
    }

    private fun handleSetStudySchedule(intent: DroidQuestIntent.SetStudySchedule) {
        val wasEnabled = repository.state.value.studyReminderEnabled
        val normalizedDays = StudySchedule.normalizeDays(intent.daysOfWeek)
        val clampedHour = StudySchedule.clampHour(intent.hour)
        val clampedMinute = StudySchedule.clampMinute(intent.minute)

        // Allow adjusting preferred days/time quietly while study reminders are turned off.
        if (!intent.enabled && !wasEnabled) {
            repository.setStudyScheduleReminder(
                enabled = false,
                daysOfWeek = normalizedDays,
                hour = clampedHour,
                minute = clampedMinute,
            )
            return
        }

        // Turning ON with no days selected defaults back to Mon–Fri; deselecting last day while ON pauses reminders.
        val effectiveDays = if (intent.enabled && !wasEnabled && normalizedDays.isEmpty()) {
            StudySchedule.DEFAULT_DAYS
        } else {
            normalizedDays
        }
        val shouldEnable = intent.enabled && effectiveDays.isNotEmpty()
        val nextQuest = findNextRecommendedQuest(repository.state.value)

        platformBridge.syncStudySessionReminder(
            enabled = shouldEnable,
            daysOfWeek = effectiveDays,
            hour = clampedHour,
            minute = clampedMinute,
            nextQuestId = nextQuest?.id,
            nextQuestTitle = nextQuest?.title,
        ) { granted, message ->
            val effectiveEnabled = shouldEnable && granted
            repository.setStudyScheduleReminder(
                enabled = effectiveEnabled,
                daysOfWeek = effectiveDays,
                hour = clampedHour,
                minute = clampedMinute,
            )
            showToast(
                message = message,
                tone = if (effectiveEnabled) ToastTone.EMERALD else ToastTone.AMBER,
            )
        }
    }

    private fun syncRemindersAfterRestore(restored: UserProgressState) {
        if (!platformBridge.supportsDailyReminders) return
        val nextQuest = findNextRecommendedQuest(restored)
        platformBridge.syncStudySessionReminder(
            enabled = restored.studyReminderEnabled && restored.studyReminderDays.isNotEmpty(),
            daysOfWeek = restored.studyReminderDays,
            hour = restored.studyReminderHour,
            minute = restored.studyReminderMinute,
            nextQuestId = nextQuest?.id,
            nextQuestTitle = nextQuest?.title,
        ) { granted, _ ->
            if (restored.studyReminderEnabled && !granted) {
                repository.setStudyScheduleReminder(
                    enabled = false,
                    daysOfWeek = restored.studyReminderDays,
                    hour = restored.studyReminderHour,
                    minute = restored.studyReminderMinute,
                )
            }
        }
    }

    private fun cancelAllPlatformRemindersQuietly() {
        if (!platformBridge.supportsDailyReminders) return
        platformBridge.syncDailyReviewReminder(
            enabled = false,
            hour = StudySchedule.DEFAULT_HOUR,
            dueCount = 0,
        ) { _, _ -> }
        platformBridge.syncStudySessionReminder(
            enabled = false,
            daysOfWeek = emptySet(),
            hour = StudySchedule.DEFAULT_HOUR,
            minute = StudySchedule.DEFAULT_MINUTE,
            nextQuestId = null,
            nextQuestTitle = null,
        ) { _, _ -> }
    }

    private fun findNextRecommendedQuest(progress: UserProgressState): QuestModule? =
        RoadmapCatalog.quests.firstOrNull { quest ->
            quest.subtopics.any { sub -> progress.topics[sub.id]?.completed != true }
        }
    fun getCurrentBackupJson(): String = repository.exportProgressJson()

    private fun applyOutcome(outcome: MutationOutcome) {
        if (outcome.hapticKind != HapticKind.NONE) {
            _effects.tryEmit(DroidQuestEffect.TriggerHaptic(outcome.hapticKind))
        }
        if (outcome.celebrateQuest || outcome.celebrateBoss) {
            eventSeq += 1L
            overrides.update {
                it.copy(celebration = CelebrationEvent(id = eventSeq, isBoss = outcome.celebrateBoss))
            }
        }
        outcome.toastMessage?.let { msg ->
            showToast(msg, outcome.toastTone)
        }
    }

    private fun showToast(message: String, tone: ToastTone) {
        eventSeq += 1L
        val banner = ToastBanner(id = eventSeq, message = message, tone = tone)
        overrides.update { it.copy(toast = banner) }
        toastJob?.cancel()
        toastJob = viewModelScope.launch {
            delay(toastDurationMs)
            overrides.update { current ->
                if (current.toast?.id == banner.id) current.copy(toast = null) else current
            }
        }
    }

    private fun deriveUiState(
        progress: UserProgressState,
        saveStatus: SaveStatus,
        local: LocalUiOverrides,
        now: Instant,
        timeZone: TimeZone,
    ): DroidQuestUiState {
        var completed = 0
        var topicXp = 0

        val doneByPillar = mutableMapOf<PillarId, Int>().apply {
            PillarId.entries.forEach { put(it, 0) }
        }
        val totalByPillar = mutableMapOf<PillarId, Int>().apply {
            PillarId.entries.forEach { put(it, 0) }
        }

        for (entry in RoadmapCatalog.allSubtopics) {
            val pillar = entry.quest.pillar
            totalByPillar[pillar] = (totalByPillar[pillar] ?: 0) + 1
            if (progress.topics[entry.subtopic.id]?.completed == true) {
                completed += 1
                topicXp += entry.subtopic.xp
                doneByPillar[pillar] = (doneByPillar[pillar] ?: 0) + 1
            }
        }

        val totalReviewsCompleted = progress.topics.values.sumOf { it.reviewCount }
        val reviewBonusXp = totalReviewsCompleted * REVIEW_BONUS_XP
        val totalXp = topicXp + reviewBonusXp

        val pillarStats = PillarId.entries.map { pillar ->
            val done = doneByPillar[pillar] ?: 0
            val total = totalByPillar[pillar] ?: 0
            val pct = if (total > 0) ((done.toDouble() / total) * PERCENT_SCALE).roundToInt() else 0
            PillarStat(pillar = pillar, done = done, total = total, pct = pct)
        }

        val nextRecommended = findNextRecommendedQuest(progress)

        val reviewQueues = SpacedRepetitionEngine.buildReviewQueues(progress.topics, now, timeZone)
        val streak = SpacedRepetitionEngine.calculateStreak(progress.activityLog, now, timeZone)
        val rankInfo = RoadmapCatalog.getRankForXp(totalXp)
        val countdown = SpacedRepetitionEngine.calculateSprintCountdown(
            startDateStr = progress.startDate,
            targetDays = progress.targetDays,
            remainingTopics = (RoadmapCatalog.totalTopics - completed).coerceAtLeast(0),
            now = now,
            timeZone = timeZone,
        )

        val overallPct = if (RoadmapCatalog.totalTopics > 0) {
            ((completed.toDouble() / RoadmapCatalog.totalTopics) * PERCENT_SCALE).roundToInt()
        } else {
            0
        }

        val heatmap = build28DayHeatmap(progress.activityLog, now, timeZone)

        return DroidQuestUiState(
            progress = progress,
            saveStatus = saveStatus,
            storageLabel = repository.storageLabel,
            activeTab = local.activeTab,
            selectedQuestId = local.selectedQuestId,
            highYieldOnly = local.highYieldOnly,
            settingsOpen = local.settingsOpen,
            completedCount = completed,
            totalCount = RoadmapCatalog.totalTopics,
            overallPct = overallPct,
            totalXp = totalXp,
            rankInfo = rankInfo,
            streak = streak,
            countdown = countdown,
            pillarStats = pillarStats,
            dueNow = reviewQueues.dueNow,
            upcoming = reviewQueues.upcoming,
            nextRecommendedQuest = nextRecommended,
            selectedQuest = local.selectedQuestId?.let { RoadmapCatalog.questsById[it] },
            totalReviewsCompleted = totalReviewsCompleted,
            heatmapDays = heatmap,
            toast = local.toast,
            celebration = local.celebration,
            supportsDailyReminders = platformBridge.supportsDailyReminders,
            supportsNativeFilePicker = platformBridge.supportsNativeFilePicker,
        )
    }

    private fun build28DayHeatmap(
        activityLog: Map<String, StudyLogEntry>,
        now: Instant,
        timeZone: TimeZone,
    ): List<HeatmapDay> {
        val today = now.toLocalDateTime(timeZone).date
        return (27 downTo 0).map { offset ->
            val date = today.minus(offset, DateTimeUnit.DAY)
            val key = RoadmapCatalog.formatDateKey(date)
            val entry = activityLog[key]
            val score = (entry?.topicsCompleted ?: 0) + (entry?.reviewsDone ?: 0)
            HeatmapDay(
                dateKey = key,
                dayOfMonth = date.day.toString(),
                monthShort = date.month.name.take(MONTH_SHORT_LENGTH)
                    .lowercase()
                    .replaceFirstChar { it.uppercase() },
                score = score,
                xp = entry?.xpEarned ?: 0,
            )
        }
    }
}
