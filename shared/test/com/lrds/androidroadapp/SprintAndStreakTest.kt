package com.lrds.androidroadapp

import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.StudyLogEntry
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SprintAndStreakTest {
    private val utc = TimeZone.UTC
    private val fixedNow = Instant.parse("2026-11-22T12:00:00Z")

    @Test
    fun `100-day countdown computes day 1, mid-sprint, and end clamping accurately`() {
        val day1 = SpacedRepetitionEngine.calculateSprintCountdown(
            startDateStr = "2026-11-22",
            targetDays = 100,
            remainingTopics = 83,
            now = fixedNow,
            timeZone = utc,
        )
        assertEquals(1, day1.currentDay)
        assertEquals(100, day1.daysLeft)
        assertEquals(0.8, day1.dailyPace)
        assertEquals(1, day1.timeProgressPct)

        val midSprint = SpacedRepetitionEngine.calculateSprintCountdown(
            startDateStr = "2026-11-02", // 20 days elapsed
            targetDays = 100,
            remainingTopics = 40,
            now = fixedNow,
            timeZone = utc,
        )
        assertEquals(21, midSprint.currentDay)
        assertEquals(80, midSprint.daysLeft)
        assertEquals(0.5, midSprint.dailyPace)
        assertEquals(21, midSprint.timeProgressPct)

        val overdueSprint = SpacedRepetitionEngine.calculateSprintCountdown(
            startDateStr = "2026-07-01",
            targetDays = 100,
            remainingTopics = 12,
            now = fixedNow,
            timeZone = utc,
        )
        assertEquals(100, overdueSprint.currentDay)
        assertEquals(0, overdueSprint.daysLeft)
        assertEquals(12.0, overdueSprint.dailyPace)
        assertEquals(100, overdueSprint.timeProgressPct)
    }

    @Test
    fun `streak counts consecutive days including yesterday grace window`() {
        assertEquals(0, SpacedRepetitionEngine.calculateStreak(emptyMap(), fixedNow, utc))

        val activeToday = mapOf(
            "2026-11-22" to StudyLogEntry("2026-11-22", xpEarned = 50, topicsCompleted = 1),
            "2026-11-21" to StudyLogEntry("2026-11-21", xpEarned = 25, reviewsDone = 1),
            "2026-11-20" to StudyLogEntry("2026-11-20", xpEarned = 75, topicsCompleted = 1),
            "2026-11-18" to StudyLogEntry("2026-11-18", xpEarned = 50, topicsCompleted = 1), // gap on Nov 19
        )
        assertEquals(3, SpacedRepetitionEngine.calculateStreak(activeToday, fixedNow, utc))

        // Mid-day grace window: no activity yet today, but yesterday & day before active
        val activeYesterday = mapOf(
            "2026-11-21" to StudyLogEntry("2026-11-21", xpEarned = 60, topicsCompleted = 1),
            "2026-11-20" to StudyLogEntry("2026-11-20", xpEarned = 25, reviewsDone = 1),
        )
        assertEquals(2, SpacedRepetitionEngine.calculateStreak(activeYesterday, fixedNow, utc))

        // Streak broken if neither today nor yesterday had activity
        val stale = mapOf(
            "2026-11-19" to StudyLogEntry("2026-11-19", xpEarned = 60, topicsCompleted = 1),
        )
        assertEquals(0, SpacedRepetitionEngine.calculateStreak(stale, fixedNow, utc))
    }

    @Test
    fun `rank ladder computes levels and progress percentage`() {
        val level1 = RoadmapCatalog.getRankForXp(200)
        assertEquals(1, level1.current.level)
        assertEquals(2, level1.next?.level)
        assertEquals(50, level1.progressPct)

        val maxLevel = RoadmapCatalog.getRankForXp(6200)
        assertEquals(8, maxLevel.current.level)
        assertEquals("Global Senior Legend", maxLevel.current.title)
        assertNull(maxLevel.next)
        assertEquals(100, maxLevel.progressPct)
    }
}
