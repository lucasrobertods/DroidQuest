package com.lrds.androidroadapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface DroidQuestDao {
    @Query("SELECT * FROM mission_config WHERE id = 1")
    suspend fun getMissionConfig(): MissionConfigEntity?

    @Query("SELECT * FROM topic_progress")
    suspend fun getAllTopicProgress(): List<TopicProgressEntity>

    @Query("SELECT * FROM quest_notes")
    suspend fun getAllQuestNotes(): List<QuestNoteEntity>

    @Query("SELECT * FROM custom_links")
    suspend fun getAllCustomLinks(): List<CustomLinkEntity>

    @Query("SELECT * FROM study_log")
    suspend fun getAllStudyLogs(): List<StudyLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMissionConfig(config: MissionConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopicProgress(items: List<TopicProgressEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertQuestNotes(items: List<QuestNoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomLinks(items: List<CustomLinkEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudyLogs(items: List<StudyLogEntity>)

    @Query("DELETE FROM topic_progress")
    suspend fun clearTopicProgress()

    @Query("DELETE FROM quest_notes")
    suspend fun clearQuestNotes()

    @Query("DELETE FROM custom_links")
    suspend fun clearCustomLinks()

    @Query("DELETE FROM study_log")
    suspend fun clearStudyLogs()

    @Transaction
    suspend fun replaceFullState(
        config: MissionConfigEntity,
        topics: List<TopicProgressEntity>,
        notes: List<QuestNoteEntity>,
        links: List<CustomLinkEntity>,
        logs: List<StudyLogEntity>,
    ) {
        upsertMissionConfig(config)
        clearTopicProgress()
        clearQuestNotes()
        clearCustomLinks()
        clearStudyLogs()
        if (topics.isNotEmpty()) upsertTopicProgress(topics)
        if (notes.isNotEmpty()) upsertQuestNotes(notes)
        if (links.isNotEmpty()) upsertCustomLinks(links)
        if (logs.isNotEmpty()) upsertStudyLogs(logs)
    }
}
