package com.lrds.androidroadapp.data.db

import com.lrds.androidroadapp.data.ProgressStorage
import com.lrds.androidroadapp.domain.model.CustomLink
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.StudyLogEntry
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.UserProgressState

expect fun createDroidQuestDatabase(): DroidQuestDatabase

class RoomProgressStorage(
    private val database: DroidQuestDatabase,
) : ProgressStorage {
    private val dao get() = database.droidQuestDao()

    override val storageLabel: String = "Room SQLite (Offline-First)"

    override val savedStatus: SaveStatus = SaveStatus.SAVED_LOCAL

    override suspend fun load(): UserProgressState? {
        val config = dao.getMissionConfig() ?: return null
        val topics = dao.getAllTopicProgress().associate { entity ->
            entity.subtopicId to TopicProgress(
                completed = entity.completed,
                completedAt = entity.completedAt,
                lastReviewedAt = entity.lastReviewedAt,
                nextReviewAt = entity.nextReviewAt,
                reviewStage = entity.reviewStage,
                reviewCount = entity.reviewCount,
                flaggedForReview = entity.flaggedForReview,
                confidence = entity.confidence,
            )
        }
        val notes = dao.getAllQuestNotes().associate { it.questId to it.note }
        val links = dao.getAllCustomLinks()
            .groupBy { it.questId }
            .mapValues { (_, list) ->
                list.map { entity ->
                    CustomLink(
                        id = entity.id,
                        title = entity.title,
                        url = entity.url,
                        type = runCatching { ResourceType.valueOf(entity.typeName) }
                            .getOrDefault(ResourceType.ARTICLE),
                    )
                }
            }
        val logs = dao.getAllStudyLogs().associate { entity ->
            entity.date to StudyLogEntry(
                date = entity.date,
                xpEarned = entity.xpEarned,
                topicsCompleted = entity.topicsCompleted,
                reviewsDone = entity.reviewsDone,
            )
        }

        return UserProgressState(
            version = config.version,
            startDate = config.startDate,
            targetDays = config.targetDays,
            soundEnabled = config.soundEnabled,
            themeMode = runCatching { ThemeMode.valueOf(config.themeMode) }.getOrDefault(ThemeMode.SYSTEM),
            dailyReminderEnabled = config.dailyReminderEnabled,
            reminderHour = config.reminderHour,
            topics = topics,
            questNotes = notes,
            customLinks = links,
            activityLog = logs,
            lastUpdated = config.lastUpdated,
        )
    }

    override suspend fun save(state: UserProgressState) {
        val config = MissionConfigEntity(
            id = 1,
            version = state.version,
            startDate = state.startDate,
            targetDays = state.targetDays,
            soundEnabled = state.soundEnabled,
            themeMode = state.themeMode.name,
            dailyReminderEnabled = state.dailyReminderEnabled,
            reminderHour = state.reminderHour,
            lastUpdated = state.lastUpdated,
        )
        val topics = state.topics.map { (id, progress) ->
            TopicProgressEntity(
                subtopicId = id,
                completed = progress.completed,
                completedAt = progress.completedAt,
                lastReviewedAt = progress.lastReviewedAt,
                nextReviewAt = progress.nextReviewAt,
                reviewStage = progress.reviewStage,
                reviewCount = progress.reviewCount,
                flaggedForReview = progress.flaggedForReview,
                confidence = progress.confidence,
            )
        }
        val notes = state.questNotes
            .filterValues { it.isNotEmpty() }
            .map { (questId, note) -> QuestNoteEntity(questId = questId, note = note) }
        val links = state.customLinks.flatMap { (questId, list) ->
            list.map { link ->
                CustomLinkEntity(
                    id = link.id,
                    questId = questId,
                    title = link.title,
                    url = link.url,
                    typeName = link.type.name,
                )
            }
        }
        val logs = state.activityLog.map { (_, log) ->
            StudyLogEntity(
                date = log.date,
                xpEarned = log.xpEarned,
                topicsCompleted = log.topicsCompleted,
                reviewsDone = log.reviewsDone,
            )
        }

        dao.replaceFullState(
            config = config,
            topics = topics,
            notes = notes,
            links = links,
            logs = logs,
        )
    }
}
