package com.lrds.androidroadapp.domain.engine

import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.NextReviewResult
import com.lrds.androidroadapp.domain.model.ReviewDueItem
import com.lrds.androidroadapp.domain.model.ReviewQueues
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.ReviewStatus
import com.lrds.androidroadapp.domain.model.SprintCountdown
import com.lrds.androidroadapp.domain.model.StudyLogEntry
import com.lrds.androidroadapp.domain.model.TopicProgress
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object SpacedRepetitionEngine {
    val REVIEW_INTERVALS_DAYS: List<Int> = listOf(1, 3, 7, 21, 60)

    private const val DEFAULT_CONFIDENCE = 3
    private const val FORGOT_CONFIDENCE = 2
    private const val HARD_CONFIDENCE = 3
    private const val GOOD_CONFIDENCE = 4
    private const val EASY_CONFIDENCE = 5
    private const val HARD_MAX_INTERVAL_DAYS = 3
    private const val PACE_ROUNDING_SCALE = 10.0
    private const val PERCENT_SCALE = 100.0
    private const val MAX_PERCENT = 100
    private const val DATE_PART_COUNT = 3
    private const val ISO_DATE_PREFIX_LENGTH = 10

    fun computeNextReview(
        current: TopicProgress,
        rating: ReviewRating,
        now: Instant = Clock.System.now(),
    ): NextReviewResult {
        var nextStage = current.reviewStage
        var intervalDays = 1
        var confidence = current.confidence ?: DEFAULT_CONFIDENCE

        when (rating) {
            ReviewRating.FORGOT -> {
                nextStage = 1
                intervalDays = 1
                confidence = FORGOT_CONFIDENCE
            }
            ReviewRating.HARD -> {
                nextStage = maxOf(1, nextStage)
                val stageInterval = REVIEW_INTERVALS_DAYS[(nextStage - 1).coerceIn(0, REVIEW_INTERVALS_DAYS.lastIndex)]
                intervalDays = minOf(HARD_MAX_INTERVAL_DAYS, stageInterval)
                confidence = HARD_CONFIDENCE
            }
            ReviewRating.GOOD -> {
                nextStage = minOf(REVIEW_INTERVALS_DAYS.size, nextStage + 1)
                intervalDays = REVIEW_INTERVALS_DAYS[nextStage - 1]
                confidence = GOOD_CONFIDENCE
            }
            ReviewRating.EASY -> {
                nextStage = minOf(REVIEW_INTERVALS_DAYS.size, nextStage + 2)
                intervalDays = REVIEW_INTERVALS_DAYS[nextStage - 1]
                confidence = EASY_CONFIDENCE
            }
        }

        val nextDate = now + intervalDays.days
        return NextReviewResult(
            nextReviewAt = nextDate.toString(),
            nextStage = nextStage,
            intervalDays = intervalDays,
            confidence = confidence,
        )
    }

    /**
     * Returns `(today - targetDate)` in whole calendar days:
     * - `> 0`: overdue by N days
     * - `0`: due today
     * - `< 0`: upcoming in `abs(result)` days
     */
    fun getDayDiffFromToday(
        isoDate: String?,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Int {
        val targetDate = isoDate?.takeIf { it.isNotBlank() }?.let { parseIsoToLocalDate(it, timeZone) }
            ?: return 0
        val today = now.toLocalDateTime(timeZone).date
        return targetDate.daysUntil(today)
    }

    fun formatReviewStatus(
        progress: TopicProgress?,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): ReviewStatus = when {
        progress == null || !progress.completed ->
            ReviewStatus(label = "Not started", isDue = false, isOverdue = false)
        progress.flaggedForReview ->
            ReviewStatus(label = "Flagged for review", isDue = true, isOverdue = true)
        progress.nextReviewAt.isNullOrBlank() ->
            ReviewStatus(label = "Scheduled", isDue = false, isOverdue = false)
        else -> {
            val overdueDays = getDayDiffFromToday(progress.nextReviewAt, now, timeZone)
            when {
                overdueDays > 0 -> ReviewStatus(
                    label = "Overdue ${overdueDays}d",
                    isDue = true,
                    isOverdue = true,
                )
                overdueDays == 0 -> ReviewStatus(
                    label = "Due today",
                    isDue = true,
                    isOverdue = false,
                )
                else -> ReviewStatus(
                    label = "Review in ${abs(overdueDays)}d",
                    isDue = false,
                    isOverdue = false,
                )
            }
        }
    }

    fun buildReviewQueues(
        topics: Map<String, TopicProgress>,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): ReviewQueues {
        val dueNow = mutableListOf<ReviewDueItem>()
        val upcoming = mutableListOf<ReviewDueItem>()

        for (entry in RoadmapCatalog.allSubtopics) {
            val progress = topics[entry.subtopic.id]
            if (progress == null || !progress.completed) continue

            val daysOverdue = getDayDiffFromToday(progress.nextReviewAt, now, timeZone)
            val isFlagged = progress.flaggedForReview

            val item = ReviewDueItem(
                subtopic = entry.subtopic,
                quest = entry.quest,
                zone = entry.zone,
                progress = progress,
                daysOverdue = daysOverdue,
                isFlagged = isFlagged,
            )

            if (isFlagged || daysOverdue >= 0) {
                dueNow += item
            } else {
                upcoming += item
            }
        }

        dueNow.sortWith(
            compareByDescending<ReviewDueItem> { it.isFlagged }
                .thenByDescending { it.daysOverdue }
        )

        upcoming.sortByDescending { it.daysOverdue } // closest upcoming first (-1 before -7)

        return ReviewQueues(dueNow = dueNow, upcoming = upcoming)
    }

    fun calculateStreak(
        activityLog: Map<String, StudyLogEntry>,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): Int {
        val activeKeys = activityLog.entries
            .filter { (_, entry) ->
                entry.topicsCompleted > 0 || entry.reviewsDone > 0 || entry.xpEarned > 0
            }
            .map { it.key }
            .toSet()

        if (activeKeys.isEmpty()) return 0

        val today = now.toLocalDateTime(timeZone).date
        val todayKey = RoadmapCatalog.formatDateKey(today)

        var cursor = if (todayKey in activeKeys) {
            today
        } else {
            today.minus(1, DateTimeUnit.DAY)
        }

        var streak = 0
        while (true) {
            val key = RoadmapCatalog.formatDateKey(cursor)
            if (key in activeKeys) {
                streak++
                cursor = cursor.minus(1, DateTimeUnit.DAY)
            } else {
                break
            }
        }
        return streak
    }

    fun calculateSprintCountdown(
        startDateStr: String,
        targetDays: Int,
        remainingTopics: Int,
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): SprintCountdown {
        val safeTargetDays = targetDays.coerceAtLeast(1)
        val today = now.toLocalDateTime(timeZone).date
        val start = parseLocalDate(startDateStr) ?: today

        val elapsedDays = maxOf(0, start.daysUntil(today))
        val currentDay = minOf(safeTargetDays, elapsedDays + 1)
        val daysLeft = maxOf(0, safeTargetDays - elapsedDays)

        val safeRemaining = remainingTopics.coerceAtLeast(0)
        val dailyPace = if (daysLeft > 0) {
            ((safeRemaining.toDouble() / daysLeft) * PACE_ROUNDING_SCALE).roundToInt() / PACE_ROUNDING_SCALE
        } else {
            safeRemaining.toDouble()
        }

        val timeProgressPct = minOf(
            MAX_PERCENT,
            ((currentDay.toDouble() / safeTargetDays) * PERCENT_SCALE).roundToInt(),
        )

        return SprintCountdown(
            currentDay = currentDay,
            daysLeft = daysLeft,
            dailyPace = dailyPace,
            timeProgressPct = timeProgressPct,
        )
    }

    fun parseLocalDate(dateStr: String): LocalDate? {
        val parts = dateStr.trim().split("-")
        val parsedParts = if (parts.size == DATE_PART_COUNT) {
            parts.mapNotNull { it.toIntOrNull() }.takeIf { it.size == DATE_PART_COUNT }
        } else {
            null
        } ?: return null

        return runCatching { LocalDate(parsedParts[0], parsedParts[1], parsedParts[2]) }.getOrNull()
    }

    private fun parseIsoToLocalDate(isoDate: String, timeZone: TimeZone): LocalDate? {
        runCatching { Instant.parse(isoDate) }.getOrNull()?.let { instant ->
            return instant.toLocalDateTime(timeZone).date
        }
        return parseLocalDate(isoDate.take(ISO_DATE_PREFIX_LENGTH))
    }
}
