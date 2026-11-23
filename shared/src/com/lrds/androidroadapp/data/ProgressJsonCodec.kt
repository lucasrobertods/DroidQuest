package com.lrds.androidroadapp.data

import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.UserProgressState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

object ProgressJsonCodec {
    val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        explicitNulls = false
    }

    fun encode(state: UserProgressState): String = json.encodeToString(UserProgressState.serializer(), state)

    fun decode(raw: String): Result<UserProgressState> = runCatching {
        val trimmed = raw.trim()
        require(trimmed.isNotEmpty()) { "Backup JSON is empty." }
        val root = json.parseToJsonElement(trimmed)
        require(root is JsonObject && "topics" in root) {
            "Invalid DroidQuest progress file: missing 'topics' object."
        }
        val decoded = json.decodeFromString(UserProgressState.serializer(), trimmed)
        val normalizedDays = StudySchedule.normalizeDays(decoded.studyReminderDays)
        decoded.copy(
            reminderHour = StudySchedule.clampHour(decoded.reminderHour),
            studyReminderEnabled = decoded.studyReminderEnabled && normalizedDays.isNotEmpty(),
            studyReminderDays = normalizedDays,
            studyReminderHour = StudySchedule.clampHour(decoded.studyReminderHour),
            studyReminderMinute = StudySchedule.clampMinute(decoded.studyReminderMinute),
        )
    }
}
