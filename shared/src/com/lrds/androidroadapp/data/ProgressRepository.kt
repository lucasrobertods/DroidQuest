package com.lrds.androidroadapp.data

import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.AppLanguage
import com.lrds.androidroadapp.domain.model.CustomLink
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.StudyLogEntry
import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.UserProgressState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class MutationOutcome(
    val toastMessage: String? = null,
    val toastTone: ToastTone = ToastTone.EMERALD,
    val celebrateQuest: Boolean = false,
    val celebrateBoss: Boolean = false,
    val hapticKind: HapticKind = HapticKind.NONE,
    val success: Boolean = true,
)

enum class ToastTone { EMERALD, AMBER, VIOLET, SKY, ROSE }

enum class HapticKind { NONE, LIGHT, SUCCESS, CELEBRATION }

@OptIn(ExperimentalTime::class)
class ProgressRepository(
    private val storage: ProgressStorage,
    private val scope: CoroutineScope,
    private val clock: Clock = Clock.System,
    private val timeZoneProvider: () -> TimeZone = { TimeZone.currentSystemDefault() },
    private val saveDebounceMs: Long = 250L,
) {
    private val _state = MutableStateFlow(
        RoadmapCatalog.createInitialProgressState(clock.now(), timeZoneProvider())
    )
    val state: StateFlow<UserProgressState> = _state.asStateFlow()

    private val _saveStatus = MutableStateFlow(SaveStatus.LOADING)
    val saveStatus: StateFlow<SaveStatus> = _saveStatus.asStateFlow()

    val storageLabel: String get() = storage.storageLabel

    private var saveJob: Job? = null
    private var loaded = false

    fun loadInitial() {
        if (loaded) return
        loaded = true
        scope.launch {
            _saveStatus.value = SaveStatus.LOADING
            val stored = runCatching { storage.load() }.getOrNull()
            if (stored != null) {
                _state.value = stored
                _saveStatus.value = storage.savedStatus
            } else {
                val seeded = RoadmapCatalog.createInitialProgressState(clock.now(), timeZoneProvider())
                _state.value = seeded
                runCatching { storage.save(seeded) }
                _saveStatus.value = storage.savedStatus
            }
        }
    }

    fun toggleTopicComplete(subtopicId: String): MutationOutcome {
        val entry = RoadmapCatalog.subtopicsById[subtopicId] ?: return MutationOutcome(success = false)
        val subtopic = entry.subtopic
        val quest = entry.quest

        val now = clock.now()
        val tz = timeZoneProvider()
        val todayKey = RoadmapCatalog.getTodayKey(now, tz)

        var willComplete = false
        var questAllDone = false

        _state.update { prev ->
            val existing = prev.topics[subtopicId]
            willComplete = !(existing?.completed ?: false)
            val todayEntry = prev.activityLog[todayKey] ?: StudyLogEntry(date = todayKey)

            val nextTopics = prev.topics.toMutableMap()
            if (willComplete) {
                nextTopics[subtopicId] = TopicProgress(
                    completed = true,
                    completedAt = now.toString(),
                    lastReviewedAt = now.toString(),
                    nextReviewAt = RoadmapCatalog.addDaysIso(now, 1),
                    reviewStage = 1,
                    reviewCount = existing?.reviewCount ?: 0,
                    flaggedForReview = false,
                    confidence = existing?.confidence ?: 4,
                )
                questAllDone = quest.subtopics.all { st ->
                    if (st.id == subtopicId) true else nextTopics[st.id]?.completed == true
                }
            } else {
                nextTopics[subtopicId] = (existing ?: TopicProgress()).copy(
                    completed = false,
                    flaggedForReview = false,
                )
            }

            val deltaXp = if (willComplete) subtopic.xp else -subtopic.xp
            val deltaCount = if (willComplete) 1 else -1

            prev.copy(
                topics = nextTopics,
                activityLog = prev.activityLog + (
                    todayKey to todayEntry.copy(
                        xpEarned = maxOf(0, todayEntry.xpEarned + deltaXp),
                        topicsCompleted = maxOf(0, todayEntry.topicsCompleted + deltaCount),
                    )
                ),
                lastUpdated = now.toString(),
            )
        }

        scheduleSave()

        return buildToggleOutcome(
            willComplete = willComplete,
            questAllDone = questAllDone,
            isBoss = quest.isBoss,
            questTitle = quest.title,
            subtopicXp = subtopic.xp,
        )
    }

    private fun buildToggleOutcome(
        willComplete: Boolean,
        questAllDone: Boolean,
        isBoss: Boolean,
        questTitle: String,
        subtopicXp: Int,
    ): MutationOutcome = when {
        willComplete && questAllDone && isBoss -> MutationOutcome(
            toastMessage = "BOSS DEFEATED! \"$questTitle\" (+$subtopicXp XP)",
            toastTone = ToastTone.AMBER,
            celebrateQuest = true,
            celebrateBoss = true,
            hapticKind = HapticKind.CELEBRATION,
        )
        willComplete && questAllDone -> MutationOutcome(
            toastMessage = "Quest Complete! \"$questTitle\" (+$subtopicXp XP) — 1st review set for tomorrow!",
            toastTone = ToastTone.EMERALD,
            celebrateQuest = true,
            hapticKind = HapticKind.CELEBRATION,
        )
        willComplete -> MutationOutcome(
            toastMessage = "+$subtopicXp XP! Review reminder scheduled for tomorrow.",
            toastTone = ToastTone.EMERALD,
            hapticKind = HapticKind.SUCCESS,
        )
        else -> MutationOutcome(
            hapticKind = HapticKind.LIGHT,
        )
    }

    fun recordReview(subtopicId: String, rating: ReviewRating): MutationOutcome {
        val current = _state.value.topics[subtopicId]
        if (current == null || !current.completed) {
            return MutationOutcome(success = false)
        }

        val now = clock.now()
        val tz = timeZoneProvider()
        val todayKey = RoadmapCatalog.getTodayKey(now, tz)
        val next = SpacedRepetitionEngine.computeNextReview(current, rating, now)
        val reviewXp = if (rating == ReviewRating.FORGOT) REVIEW_XP_FORGOT else REVIEW_XP_RECALLED

        _state.update { prev ->
            val existing = prev.topics[subtopicId] ?: return@update prev
            val todayEntry = prev.activityLog[todayKey] ?: StudyLogEntry(date = todayKey)
            prev.copy(
                topics = prev.topics + (
                    subtopicId to existing.copy(
                        lastReviewedAt = now.toString(),
                        nextReviewAt = next.nextReviewAt,
                        reviewStage = next.nextStage,
                        reviewCount = existing.reviewCount + 1,
                        flaggedForReview = false,
                        confidence = next.confidence,
                    )
                ),
                activityLog = prev.activityLog + (
                    todayKey to todayEntry.copy(
                        xpEarned = todayEntry.xpEarned + reviewXp,
                        reviewsDone = todayEntry.reviewsDone + 1,
                    )
                ),
                lastUpdated = now.toString(),
            )
        }

        scheduleSave()
        val dayWord = if (next.intervalDays == 1) "day" else "days"
        return MutationOutcome(
            toastMessage = "Review logged (+$reviewXp XP)! Next review in ${next.intervalDays} $dayWord.",
            toastTone = ToastTone.VIOLET,
            hapticKind = HapticKind.SUCCESS,
        )
    }

    fun toggleFlagForReview(subtopicId: String): MutationOutcome {
        val existing = _state.value.topics[subtopicId]
        if (existing == null || !existing.completed) return MutationOutcome(success = false)

        val now = clock.now()
        val nextFlag = !existing.flaggedForReview

        _state.update { prev ->
            val cur = prev.topics[subtopicId] ?: return@update prev
            prev.copy(
                topics = prev.topics + (subtopicId to cur.copy(flaggedForReview = nextFlag)),
                lastUpdated = now.toString(),
            )
        }

        scheduleSave()
        return MutationOutcome(
            toastMessage = if (nextFlag) "Pinned to your Review Queue!" else "Removed manual review pin.",
            toastTone = ToastTone.AMBER,
            hapticKind = HapticKind.LIGHT,
        )
    }

    fun updateQuestNote(questId: String, note: String) {
        val now = clock.now()
        _state.update { prev ->
            prev.copy(
                questNotes = prev.questNotes + (questId to note),
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
    }

    fun addCustomLink(questId: String, title: String, rawUrl: String, type: ResourceType): MutationOutcome {
        val cleanTitle = title.trim()
        val cleanUrl = rawUrl.trim()
        if (cleanTitle.isEmpty() || cleanUrl.isEmpty()) {
            return MutationOutcome(
                toastMessage = "Enter both a title and a URL.",
                toastTone = ToastTone.ROSE,
                success = false,
            )
        }
        val normalizedUrl = if (cleanUrl.startsWith("http://", ignoreCase = true) ||
            cleanUrl.startsWith("https://", ignoreCase = true)
        ) {
            cleanUrl
        } else {
            "https://$cleanUrl"
        }

        val now = clock.now()
        val newLink = CustomLink(
            id = "custom-${now.toEpochMilliseconds()}",
            title = cleanTitle,
            url = normalizedUrl,
            type = type,
        )

        _state.update { prev ->
            val existingLinks = prev.customLinks[questId].orEmpty()
            prev.copy(
                customLinks = prev.customLinks + (questId to (existingLinks + newLink)),
                lastUpdated = now.toString(),
            )
        }

        scheduleSave()
        return MutationOutcome(
            toastMessage = "Study link saved to quest!",
            toastTone = ToastTone.SKY,
            hapticKind = HapticKind.LIGHT,
        )
    }

    fun removeCustomLink(questId: String, linkId: String) {
        val now = clock.now()
        _state.update { prev ->
            val filtered = prev.customLinks[questId].orEmpty().filterNot { it.id == linkId }
            prev.copy(
                customLinks = prev.customLinks + (questId to filtered),
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
    }

    fun updateStartDate(startDate: String): MutationOutcome {
        val parsed = SpacedRepetitionEngine.parseLocalDate(startDate)
            ?: return MutationOutcome(
                toastMessage = "Use YYYY-MM-DD format (e.g. 2026-11-22).",
                toastTone = ToastTone.ROSE,
                success = false,
            )
        val formatted = RoadmapCatalog.formatDateKey(parsed)
        val now = clock.now()
        _state.update { prev ->
            prev.copy(
                startDate = formatted,
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
        return MutationOutcome(
            toastMessage = "100-Day Mission start date updated to $formatted!",
            toastTone = ToastTone.EMERALD,
            hapticKind = HapticKind.LIGHT,
        )
    }

    fun toggleSound(): MutationOutcome {
        val now = clock.now()
        _state.update { prev ->
            prev.copy(
                soundEnabled = !prev.soundEnabled,
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
        return MutationOutcome(hapticKind = HapticKind.LIGHT)
    }

    fun setThemeMode(mode: ThemeMode): MutationOutcome {
        val now = clock.now()
        _state.update { prev ->
            prev.copy(
                themeMode = mode,
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
        return MutationOutcome(hapticKind = HapticKind.LIGHT)
    }

    fun setAppLanguage(language: AppLanguage): MutationOutcome {
        val now = clock.now()
        _state.update { prev ->
            prev.copy(
                appLanguage = language,
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
        return MutationOutcome(hapticKind = HapticKind.LIGHT)
    }

    fun setDailyReminder(enabled: Boolean, hour: Int = _state.value.reminderHour) {
        val now = clock.now()
        _state.update { prev ->
            prev.copy(
                dailyReminderEnabled = enabled,
                reminderHour = hour.coerceIn(0, MAX_REMINDER_HOUR),
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
    }

    fun setStudyScheduleReminder(
        enabled: Boolean,
        daysOfWeek: Set<Int> = _state.value.studyReminderDays,
        hour: Int = _state.value.studyReminderHour,
        minute: Int = _state.value.studyReminderMinute,
    ) {
        val now = clock.now()
        val normalizedDays = StudySchedule.normalizeDays(daysOfWeek)
        val effectiveEnabled = enabled && normalizedDays.isNotEmpty()
        _state.update { prev ->
            prev.copy(
                studyReminderEnabled = effectiveEnabled,
                studyReminderDays = normalizedDays,
                studyReminderHour = StudySchedule.clampHour(hour),
                studyReminderMinute = StudySchedule.clampMinute(minute),
                lastUpdated = now.toString(),
            )
        }
        scheduleSave()
    }

    fun exportProgressJson(): String = ProgressJsonCodec.encode(_state.value)

    fun importProgressJson(raw: String): MutationOutcome {
        val decoded = ProgressJsonCodec.decode(raw)
        return decoded.fold(
            onSuccess = { imported ->
                _state.value = imported
                scheduleSave(immediate = true)
                MutationOutcome(
                    toastMessage = "Progress imported and restored!",
                    toastTone = ToastTone.EMERALD,
                    hapticKind = HapticKind.SUCCESS,
                    success = true,
                )
            },
            onFailure = {
                MutationOutcome(
                    toastMessage = "Could not import JSON — invalid format.",
                    toastTone = ToastTone.ROSE,
                    success = false,
                )
            },
        )
    }

    fun resetProgress(): MutationOutcome {
        val fresh = RoadmapCatalog.createInitialProgressState(clock.now(), timeZoneProvider())
        _state.value = fresh
        scheduleSave(immediate = true)
        return MutationOutcome(
            toastMessage = "Progress reset to Day 1 (with your 3 SD lessons kept).",
            toastTone = ToastTone.AMBER,
            hapticKind = HapticKind.LIGHT,
        )
    }

    private fun scheduleSave(immediate: Boolean = false) {
        val snapshot = _state.value
        _saveStatus.value = SaveStatus.SAVING
        saveJob?.cancel()
        saveJob = scope.launch {
            if (!immediate && saveDebounceMs > 0L) {
                delay(saveDebounceMs)
            }
            runCatching { storage.save(snapshot) }
            _saveStatus.value = storage.savedStatus
        }
    }

    private companion object {
        const val REVIEW_XP_FORGOT = 10
        const val REVIEW_XP_RECALLED = 25
        const val MAX_REMINDER_HOUR = 23
    }
}
