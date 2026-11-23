package com.lrds.androidroadapp

import com.lrds.androidroadapp.data.ProgressJsonCodec
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.StudySchedule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProgressSerializationTest {
    @Test
    fun `decodes exact web progress json schema and round-trips cleanly`() {
        val webProgressJson = """
            {
              "version": 1,
              "startDate": "2026-11-21",
              "targetDays": 100,
              "soundEnabled": true,
              "topics": {
                "sd-course-1-intro": {
                  "completed": true,
                  "completedAt": "2026-11-19T11:57:49.800Z",
                  "lastReviewedAt": "2026-11-19T11:57:49.800Z",
                  "nextReviewAt": "2026-11-21T11:57:49.800Z",
                  "reviewStage": 1,
                  "reviewCount": 2,
                  "confidence": 4,
                  "flaggedForReview": false
                }
              },
              "questNotes": {
                "q29-sd-foundations": "Ready to review framework."
              },
              "customLinks": {
                "q1-android-fundamentals": [
                  {
                    "id": "custom-1",
                    "title": "ART Deep Dive",
                    "url": "https://example.com/art",
                    "type": "youtube"
                  }
                ]
              },
              "activityLog": {
                "2026-11-21": {
                  "date": "2026-11-21",
                  "xpEarned": 160,
                  "topicsCompleted": 3,
                  "reviewsDone": 1
                }
              },
              "lastUpdated": "2026-11-21T11:59:46.440Z"
            }
        """.trimIndent()

        val decoded = ProgressJsonCodec.decode(webProgressJson).getOrThrow()
        assertEquals(1, decoded.version)
        assertEquals("2026-11-21", decoded.startDate)
        assertEquals(100, decoded.targetDays)
        assertTrue(decoded.topics["sd-course-1-intro"]?.completed == true)
        assertEquals(2, decoded.topics["sd-course-1-intro"]?.reviewCount)
        assertEquals("Ready to review framework.", decoded.questNotes["q29-sd-foundations"])
        assertEquals(ResourceType.YOUTUBE, decoded.customLinks["q1-android-fundamentals"]?.first()?.type)
        // Legacy progress.json without studyReminder* fields decodes with safe defaults
        assertFalse(decoded.studyReminderEnabled)
        assertEquals(StudySchedule.DEFAULT_DAYS, decoded.studyReminderDays)
        assertEquals(StudySchedule.DEFAULT_HOUR, decoded.studyReminderHour)
        assertEquals(StudySchedule.DEFAULT_MINUTE, decoded.studyReminderMinute)

        val reEncoded = ProgressJsonCodec.encode(decoded)
        val roundTripped = ProgressJsonCodec.decode(reEncoded).getOrThrow()
        assertEquals(decoded, roundTripped)
    }

    @Test
    fun `round-trips study schedule settings in progress json and sanitizes invalid bounds`() {
        val jsonWithSchedule = """
            {
              "version": 1,
              "startDate": "2026-11-21",
              "targetDays": 100,
              "soundEnabled": true,
              "dailyReminderEnabled": true,
              "reminderHour": 20,
              "studyReminderEnabled": true,
              "studyReminderDays": [5, 1, 3, 7, 99],
              "studyReminderHour": 7,
              "studyReminderMinute": 45,
              "topics": {},
              "lastUpdated": "2026-11-21T11:59:46.440Z"
            }
        """.trimIndent()

        val decoded = ProgressJsonCodec.decode(jsonWithSchedule).getOrThrow()
        assertTrue(decoded.dailyReminderEnabled)
        assertEquals(20, decoded.reminderHour)
        assertTrue(decoded.studyReminderEnabled)
        assertEquals(setOf(1, 3, 5, 7), decoded.studyReminderDays)
        assertEquals(7, decoded.studyReminderHour)
        assertEquals(45, decoded.studyReminderMinute)

        val encoded = ProgressJsonCodec.encode(decoded)
        val roundTripped = ProgressJsonCodec.decode(encoded).getOrThrow()
        assertEquals(decoded, roundTripped)
    }

    @Test
    fun `rejects invalid json or missing topics key`() {
        assertFalse(ProgressJsonCodec.decode("").isSuccess)
        assertFalse(ProgressJsonCodec.decode("""{"foo": "bar"}""").isSuccess)
        assertFalse(ProgressJsonCodec.decode("not json").isSuccess)
    }
}
