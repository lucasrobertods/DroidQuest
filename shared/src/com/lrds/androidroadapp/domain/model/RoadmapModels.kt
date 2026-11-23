package com.lrds.androidroadapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ZoneId {
    @SerialName("emerald") EMERALD,
    @SerialName("sapphire") SAPPHIRE,
    @SerialName("ruby") RUBY,
    @SerialName("golden") GOLDEN,
    @SerialName("cosmic") COSMIC,
}

@Serializable
enum class PillarId {
    @SerialName("android") ANDROID,
    @SerialName("dsa") DSA,
    @SerialName("system-design") SYSTEM_DESIGN,
    @SerialName("career") CAREER,
}

@Serializable
enum class ResourceType {
    @SerialName("youtube") YOUTUBE,
    @SerialName("docs") DOCS,
    @SerialName("article") ARTICLE,
    @SerialName("practice") PRACTICE,
    @SerialName("repo") REPO,
    @SerialName("course") COURSE,
}

@Serializable
enum class ReviewRating {
    @SerialName("forgot") FORGOT,
    @SerialName("hard") HARD,
    @SerialName("good") GOOD,
    @SerialName("easy") EASY,
}

@Serializable
enum class ThemeMode {
    @SerialName("system") SYSTEM,
    @SerialName("light") LIGHT,
    @SerialName("dark") DARK,
}

@Serializable
enum class AppLanguage(val label: String) {
    @SerialName("system") SYSTEM("System"),
    @SerialName("en") EN("EN"),
    @SerialName("pt-BR") PT_BR("PT-BR"),
    @SerialName("es") ES("ES"),
}

@Serializable
data class StudyResource(
    val id: String,
    val title: String,
    val url: String,
    val type: ResourceType,
    val note: String? = null,
)

@Serializable
data class InterviewQuestion(
    val question: String,
    val keyPoints: String,
)

@Serializable
data class SubTopic(
    val id: String,
    val title: String,
    val description: String,
    val xp: Int,
    val highYield: Boolean = false,
    val courseTag: String? = null,
    val resources: List<StudyResource> = emptyList(),
)

@Serializable
data class QuestModule(
    val id: String,
    val zoneId: ZoneId,
    val pillar: PillarId,
    val order: Int,
    val title: String,
    val subtitle: String,
    val dayRange: String,
    val estimatedHours: Int,
    val isBoss: Boolean = false,
    val highYield: Boolean = false,
    val sourceBadge: String? = null,
    val icon: String,
    val subtopics: List<SubTopic>,
    val interviewQuestions: List<InterviewQuestion> = emptyList(),
)

data class AdventureZone(
    val id: ZoneId,
    val number: Int,
    val title: String,
    val shortTitle: String,
    val subtitle: String,
    val daySpan: String,
    val badgeTitle: String,
    val badgeIcon: String,
)

data class SubTopicEntry(
    val subtopic: SubTopic,
    val quest: QuestModule,
    val zone: AdventureZone,
)

/**
 * Mirrors `TopicProgress` from `src/types/roadmap.ts` & `data/progress.json` for 1:1 JSON compatibility.
 */
@Serializable
data class TopicProgress(
    val completed: Boolean = false,
    val completedAt: String? = null,
    val lastReviewedAt: String? = null,
    val nextReviewAt: String? = null,
    val reviewStage: Int = 0,
    val reviewCount: Int = 0,
    val flaggedForReview: Boolean = false,
    val confidence: Int? = null,
)

@Serializable
data class CustomLink(
    val id: String,
    val title: String,
    val url: String,
    val type: ResourceType,
)

@Serializable
data class StudyLogEntry(
    val date: String, // YYYY-MM-DD
    val xpEarned: Int = 0,
    val topicsCompleted: Int = 0,
    val reviewsDone: Int = 0,
)

/**
 * Mirrors `UserProgressState` from `data/progress.json` so backups export/import seamlessly
 * between the local web app and Android/iOS/Desktop/Wasm builds.
 */
@Serializable
data class UserProgressState(
    val version: Int = 1,
    val startDate: String, // YYYY-MM-DD
    val targetDays: Int = 100,
    val soundEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val appLanguage: AppLanguage = AppLanguage.SYSTEM,
    val dailyReminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val studyReminderEnabled: Boolean = false,
    val studyReminderDays: Set<Int> = StudySchedule.DEFAULT_DAYS,
    val studyReminderHour: Int = StudySchedule.DEFAULT_HOUR,
    val studyReminderMinute: Int = StudySchedule.DEFAULT_MINUTE,
    val topics: Map<String, TopicProgress> = emptyMap(),
    val questNotes: Map<String, String> = emptyMap(),
    val customLinks: Map<String, List<CustomLink>> = emptyMap(),
    val activityLog: Map<String, StudyLogEntry> = emptyMap(),
    val lastUpdated: String,
)

data class ReviewDueItem(
    val subtopic: SubTopic,
    val quest: QuestModule,
    val zone: AdventureZone,
    val progress: TopicProgress,
    val daysOverdue: Int, // >= 0 means due today or overdue; < 0 means upcoming
    val isFlagged: Boolean,
)

data class NextReviewResult(
    val nextReviewAt: String,
    val nextStage: Int,
    val intervalDays: Int,
    val confidence: Int,
)

data class ReviewStatus(
    val label: String,
    val isDue: Boolean,
    val isOverdue: Boolean,
)

data class ReviewQueues(
    val dueNow: List<ReviewDueItem>,
    val upcoming: List<ReviewDueItem>,
)

data class SprintCountdown(
    val currentDay: Int,
    val daysLeft: Int,
    val dailyPace: Double,
    val timeProgressPct: Int,
)

data class RankTier(
    val level: Int,
    val title: String,
    val minXp: Int,
    val accentHex: Long,
)

data class RankInfo(
    val current: RankTier,
    val next: RankTier?,
    val progressPct: Int,
)

data class PillarMeta(
    val id: PillarId,
    val label: String,
    val shortLabel: String,
)

data class PillarStat(
    val pillar: PillarId,
    val done: Int,
    val total: Int,
    val pct: Int,
)

enum class SaveStatus {
    LOADING,
    SAVING,
    SAVED_LOCAL,
    SAVED_BROWSER,
}
