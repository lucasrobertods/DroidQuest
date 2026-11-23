package com.lrds.androidroadapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lrds.androidroadapp.domain.model.StudySchedule

@Entity(tableName = "mission_config")
data class MissionConfigEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int,
    val startDate: String,
    val targetDays: Int,
    val soundEnabled: Boolean,
    val themeMode: String = "SYSTEM",
    val appLanguage: String = "SYSTEM",
    val dailyReminderEnabled: Boolean,
    val reminderHour: Int,
    val studyReminderEnabled: Boolean = false,
    val studyReminderDays: String = StudySchedule.DEFAULT_DAYS_CSV,
    val studyReminderHour: Int = StudySchedule.DEFAULT_HOUR,
    val studyReminderMinute: Int = StudySchedule.DEFAULT_MINUTE,
    val lastUpdated: String,
)

@Entity(tableName = "topic_progress")
data class TopicProgressEntity(
    @PrimaryKey val subtopicId: String,
    val completed: Boolean,
    val completedAt: String?,
    val lastReviewedAt: String?,
    val nextReviewAt: String?,
    val reviewStage: Int,
    val reviewCount: Int,
    val flaggedForReview: Boolean,
    val confidence: Int?,
)

@Entity(tableName = "quest_notes")
data class QuestNoteEntity(
    @PrimaryKey val questId: String,
    val note: String,
)

@Entity(tableName = "custom_links")
data class CustomLinkEntity(
    @PrimaryKey val id: String,
    val questId: String,
    val title: String,
    val url: String,
    val typeName: String,
)

@Entity(tableName = "study_log")
data class StudyLogEntity(
    @PrimaryKey val date: String,
    val xpEarned: Int,
    val topicsCompleted: Int,
    val reviewsDone: Int,
)
