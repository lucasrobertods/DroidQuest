package com.lrds.androidroadapp

import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.TopicProgress
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SpacedRepetitionEngineTest {
    private val utc = TimeZone.UTC
    private val fixedNow = Instant.parse("2026-11-22T12:00:00Z")

    @Test
    fun `curriculum mirrors exact 5 zones, 33 quests, 86 subtopics, and 5 bosses`() {
        assertEquals(5, RoadmapCatalog.zones.size)
        assertEquals(33, RoadmapCatalog.quests.size)
        assertEquals(86, RoadmapCatalog.totalTopics)
        assertEquals(5, RoadmapCatalog.quests.count { it.isBoss })
        assertEquals(RoadmapCatalog.totalTopics, RoadmapCatalog.subtopicsById.size)
    }

    @Test
    fun `good rating advances through exact 1d 3d 7d 21d 60d ladder`() {
        val expected = listOf(1 to 1, 2 to 3, 3 to 7, 4 to 21, 5 to 60, 5 to 60)
        var current = TopicProgress(completed = true, reviewStage = 0)

        for ((expectedStage, expectedInterval) in expected) {
            val res = SpacedRepetitionEngine.computeNextReview(current, ReviewRating.GOOD, fixedNow)
            assertEquals(expectedStage, res.nextStage)
            assertEquals(expectedInterval, res.intervalDays)
            assertEquals(4, res.confidence)
            assertEquals((fixedNow + expectedInterval.days).toString(), res.nextReviewAt)
            current = current.copy(reviewStage = res.nextStage, confidence = res.confidence)
        }
    }

    @Test
    fun `easy rating jumps two stages and caps at stage 5`() {
        val start = TopicProgress(completed = true, reviewStage = 1)
        val firstJump = SpacedRepetitionEngine.computeNextReview(start, ReviewRating.EASY, fixedNow)
        assertEquals(3, firstJump.nextStage)
        assertEquals(7, firstJump.intervalDays)
        assertEquals(5, firstJump.confidence)

        val secondJump = SpacedRepetitionEngine.computeNextReview(
            start.copy(reviewStage = firstJump.nextStage),
            ReviewRating.EASY,
            fixedNow,
        )
        assertEquals(5, secondJump.nextStage)
        assertEquals(60, secondJump.intervalDays)
    }

    @Test
    fun `forgot rating resets back to stage 1 with 1 day interval`() {
        val stage4 = TopicProgress(completed = true, reviewStage = 4, confidence = 5)
        val res = SpacedRepetitionEngine.computeNextReview(stage4, ReviewRating.FORGOT, fixedNow)
        assertEquals(1, res.nextStage)
        assertEquals(1, res.intervalDays)
        assertEquals(2, res.confidence)
    }

    @Test
    fun `hard rating keeps stage and caps interval at 3 days`() {
        val stage4 = TopicProgress(completed = true, reviewStage = 4, confidence = 4)
        val res = SpacedRepetitionEngine.computeNextReview(stage4, ReviewRating.HARD, fixedNow)
        assertEquals(4, res.nextStage)
        assertEquals(3, res.intervalDays)
        assertEquals(3, res.confidence)
    }

    @Test
    fun `formatReviewStatus distinguishes not started, flagged, overdue, due today, and upcoming`() {
        assertEquals("Not started", SpacedRepetitionEngine.formatReviewStatus(null, fixedNow, utc).label)

        val flagged = TopicProgress(
            completed = true,
            nextReviewAt = (fixedNow + 10.days).toString(),
            flaggedForReview = true,
        )
        val flaggedStatus = SpacedRepetitionEngine.formatReviewStatus(flagged, fixedNow, utc)
        assertEquals("Flagged for review", flaggedStatus.label)
        assertTrue(flaggedStatus.isDue)

        val overdue = TopicProgress(completed = true, nextReviewAt = (fixedNow - 3.days).toString())
        val overdueStatus = SpacedRepetitionEngine.formatReviewStatus(overdue, fixedNow, utc)
        assertEquals("Overdue 3d", overdueStatus.label)
        assertTrue(overdueStatus.isDue)
        assertTrue(overdueStatus.isOverdue)

        val dueToday = TopicProgress(completed = true, nextReviewAt = fixedNow.toString())
        val todayStatus = SpacedRepetitionEngine.formatReviewStatus(dueToday, fixedNow, utc)
        assertEquals("Due today", todayStatus.label)
        assertTrue(todayStatus.isDue)
        assertFalse(todayStatus.isOverdue)

        val upcoming = TopicProgress(completed = true, nextReviewAt = (fixedNow + 5.days).toString())
        val upcomingStatus = SpacedRepetitionEngine.formatReviewStatus(upcoming, fixedNow, utc)
        assertEquals("Review in 5d", upcomingStatus.label)
        assertFalse(upcomingStatus.isDue)
    }

    @Test
    fun `buildReviewQueues sorts flagged first, then most overdue, and closest upcoming first`() {
        val topics = mapOf(
            "af-1-os-art" to TopicProgress(completed = true, nextReviewAt = (fixedNow - 1.days).toString()),
            "af-2-context-process-death" to TopicProgress(
                completed = true,
                nextReviewAt = (fixedNow - 5.days).toString(),
            ),
            "af-3-gradle-basics" to TopicProgress(
                completed = true,
                nextReviewAt = (fixedNow + 10.days).toString(),
                flaggedForReview = true,
            ),
            "kt-1-inline-reified" to TopicProgress(completed = true, nextReviewAt = (fixedNow + 7.days).toString()),
            "kt-2-types-delegation" to TopicProgress(completed = true, nextReviewAt = (fixedNow + 2.days).toString()),
            "kt-3-generics-sequences" to TopicProgress(
                completed = false,
                nextReviewAt = (fixedNow - 2.days).toString(),
            ),
        )

        val queues = SpacedRepetitionEngine.buildReviewQueues(topics, fixedNow, utc)
        assertEquals(
            listOf("af-3-gradle-basics", "af-2-context-process-death", "af-1-os-art"),
            queues.dueNow.map { it.subtopic.id },
        )
        assertEquals(
            listOf("kt-2-types-delegation", "kt-1-inline-reified"),
            queues.upcoming.map { it.subtopic.id },
        )
    }
}
