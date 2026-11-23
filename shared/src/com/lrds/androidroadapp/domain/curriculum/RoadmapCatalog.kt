package com.lrds.androidroadapp.domain.curriculum

import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.PillarMeta
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.RankInfo
import com.lrds.androidroadapp.domain.model.RankTier
import com.lrds.androidroadapp.domain.model.StudyLogEntry
import com.lrds.androidroadapp.domain.model.SubTopicEntry
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.UserProgressState
import com.lrds.androidroadapp.domain.model.ZoneId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
object RoadmapCatalog {
    private const val PERCENT_SCALE = 100.0
    private const val MAX_PERCENT = 100
    private const val YEAR_PAD_LENGTH = 4

    val zones: List<AdventureZone> = ZonesCatalog.zones

    val quests: List<QuestModule> = AndroidQuestsCatalog.quests + DsaAndDesignQuestsCatalog.quests

    val questsById: Map<String, QuestModule> = quests.associateBy { it.id }

    val zonesById: Map<ZoneId, AdventureZone> = zones.associateBy { it.id }

    val allSubtopics: List<SubTopicEntry> = quests.flatMap { quest ->
        val zone = getZoneById(quest.zoneId)
        quest.subtopics.map { subtopic ->
            SubTopicEntry(subtopic = subtopic, quest = quest, zone = zone)
        }
    }

    val subtopicsById: Map<String, SubTopicEntry> = allSubtopics.associateBy { it.subtopic.id }

    val totalTopics: Int = allSubtopics.size

    val totalXpAvailable: Int = allSubtopics.sumOf { it.subtopic.xp }

    val pillarMeta: Map<PillarId, PillarMeta> = mapOf(
        PillarId.ANDROID to PillarMeta(PillarId.ANDROID, "Android & Kotlin", "Android"),
        PillarId.DSA to PillarMeta(PillarId.DSA, "DSA & LeetCode", "DSA"),
        PillarId.SYSTEM_DESIGN to PillarMeta(PillarId.SYSTEM_DESIGN, "Mobile System Design", "Sys Design"),
        PillarId.CAREER to PillarMeta(PillarId.CAREER, "US/EU Career & Loops", "Career"),
    )

    val rankTiers: List<RankTier> = listOf(
        RankTier(level = 1, title = "Android Explorer", minXp = 0, accentHex = 0xFF22C55E),
        RankTier(level = 2, title = "Compose Pathfinder", minXp = 400, accentHex = 0xFF14B8A6),
        RankTier(level = 3, title = "Coroutine Weaver", minXp = 1000, accentHex = 0xFF38BDF8),
        RankTier(level = 4, title = "Citadel Architect", minXp = 1800, accentHex = 0xFF6366F1),
        RankTier(level = 5, title = "Quality Sentinel", minXp = 2700, accentHex = 0xFFF43F5E),
        RankTier(level = 6, title = "Algorithm Alchemist", minXp = 3700, accentHex = 0xFFF59E0B),
        RankTier(level = 7, title = "System Design Sage", minXp = 4700, accentHex = 0xFFA855F7),
        RankTier(level = 8, title = "Global Senior Legend", minXp = 5700, accentHex = 0xFFFACC15),
    )

    fun getZoneById(zoneId: ZoneId): AdventureZone = zonesById[zoneId] ?: zones.first()

    fun getRankForXp(xp: Int): RankInfo {
        var current = rankTiers.first()
        var next: RankTier? = rankTiers.getOrNull(1)

        for (i in rankTiers.indices) {
            if (xp >= rankTiers[i].minXp) {
                current = rankTiers[i]
                next = rankTiers.getOrNull(i + 1)
            }
        }

        if (next == null) {
            return RankInfo(current = current, next = null, progressPct = 100)
        }

        val span = (next.minXp - current.minXp).coerceAtLeast(1)
        val earnedInTier = (xp - current.minXp).coerceAtLeast(0)
        val pct = ((earnedInTier.toDouble() / span) * PERCENT_SCALE).roundToInt().coerceIn(0, MAX_PERCENT)
        return RankInfo(current = current, next = next, progressPct = pct)
    }

    fun formatDateKey(date: LocalDate): String {
        val y = date.year.toString().padStart(YEAR_PAD_LENGTH, '0')
        val m = date.month.number.toString().padStart(2, '0')
        val d = date.day.toString().padStart(2, '0')
        return "$y-$m-$d"
    }

    fun getTodayKey(
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): String = formatDateKey(now.toLocalDateTime(timeZone).date)

    fun addDaysIso(baseInstant: Instant, days: Int): String = (baseInstant + days.days).toString()

    /**
     * Default initial progress pre-populated with the 3 completed System Design Module 4 lessons,
     * mirroring `createInitialProgressState()` in `roadmapData.ts`.
     */
    fun createInitialProgressState(
        now: Instant = Clock.System.now(),
        timeZone: TimeZone = TimeZone.currentSystemDefault(),
    ): UserProgressState {
        val twoDaysAgo = now - 2.days
        val todayKey = getTodayKey(now, timeZone)

        return UserProgressState(
            version = 1,
            startDate = todayKey,
            targetDays = 100,
            soundEnabled = true,
            dailyReminderEnabled = false,
            reminderHour = 20,
            topics = mapOf(
                "sd-course-1-intro" to TopicProgress(
                    completed = true,
                    completedAt = twoDaysAgo.toString(),
                    lastReviewedAt = twoDaysAgo.toString(),
                    nextReviewAt = now.toString(),
                    reviewStage = 1,
                    reviewCount = 0,
                    flaggedForReview = false,
                    confidence = 4,
                ),
                "sd-course-2-functional" to TopicProgress(
                    completed = true,
                    completedAt = twoDaysAgo.toString(),
                    lastReviewedAt = twoDaysAgo.toString(),
                    nextReviewAt = addDaysIso(now, 1),
                    reviewStage = 1,
                    reviewCount = 0,
                    flaggedForReview = false,
                    confidence = 4,
                ),
                "sd-course-3-nonfunctional" to TopicProgress(
                    completed = true,
                    completedAt = twoDaysAgo.toString(),
                    lastReviewedAt = twoDaysAgo.toString(),
                    nextReviewAt = addDaysIso(now, 2),
                    reviewStage = 1,
                    reviewCount = 0,
                    flaggedForReview = false,
                    confidence = 4,
                ),
            ),
            questNotes = mapOf(
                "q29-sd-foundations" to "Pre-marked my 3 completed System Design Module 4 lessons! Ready to review the framework and move into Mobile System Design building blocks & case studies.",
            ),
            customLinks = emptyMap(),
            activityLog = mapOf(
                todayKey to StudyLogEntry(
                    date = todayKey,
                    xpEarned = 160,
                    topicsCompleted = 3,
                    reviewsDone = 0,
                ),
            ),
            lastUpdated = now.toString(),
        )
    }
}
