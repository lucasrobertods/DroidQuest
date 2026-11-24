package com.lrds.androidroadapp

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.lrds.androidroadapp.data.db.DROIDQUEST_MIGRATION_1_2
import com.lrds.androidroadapp.data.db.DROIDQUEST_MIGRATION_2_3
import com.lrds.androidroadapp.data.db.DROIDQUEST_MIGRATION_3_4
import com.lrds.androidroadapp.data.db.DroidQuestDatabase
import com.lrds.androidroadapp.data.db.RoomProgressStorage
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.AppLanguage
import com.lrds.androidroadapp.domain.model.CustomLink
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.ThemeMode
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class RoomProgressStorageTest {
    @Test
    fun `room kmp bundled sqlite saves and restores complete progress state including study schedule`() = runTest {
        val db = Room.inMemoryDatabaseBuilder<DroidQuestDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()

        try {
            val storage = RoomProgressStorage(db)
            val now = Instant.parse("2026-11-22T12:00:00Z")
            val initial = RoadmapCatalog.createInitialProgressState(now, TimeZone.UTC).copy(
                themeMode = ThemeMode.LIGHT,
                appLanguage = AppLanguage.PT_BR,
                dailyReminderEnabled = true,
                reminderHour = 19,
                studyReminderEnabled = true,
                studyReminderDays = setOf(1, 3, 5, 7),
                studyReminderHour = 8,
                studyReminderMinute = 30,
                questNotes = mapOf("q1-android-fundamentals" to "Room KMP note persisted!"),
                customLinks = mapOf(
                    "q1-android-fundamentals" to listOf(
                        CustomLink(
                            id = "custom-42",
                            title = "Official Room KMP Guide",
                            url = "https://developer.android.com/kotlin/multiplatform/room",
                            type = ResourceType.DOCS,
                        )
                    )
                ),
            )

            storage.save(initial)
            val loaded = storage.load()
            assertNotNull(loaded)
            assertEquals(initial, loaded)
        } finally {
            db.close()
        }
    }

    @Test
    fun `non-destructive migration v2 to v3 preserves existing data and populates study schedule defaults`() =
        runTest {
            val tempDir = Files.createTempDirectory("droidquest-v2-migration").toFile()
            val dbFile = File(tempDir, "droidquest-v2.db")
            val driver = BundledSQLiteDriver()

            try {
                seedLegacyV2Database(driver, dbFile.absolutePath)

                val db = Room.databaseBuilder<DroidQuestDatabase>(name = dbFile.absolutePath)
                    .setDriver(BundledSQLiteDriver())
                    .addMigrations(DROIDQUEST_MIGRATION_1_2, DROIDQUEST_MIGRATION_2_3, DROIDQUEST_MIGRATION_3_4)
                    .build()

                try {
                    val storage = RoomProgressStorage(db)
                    val migrated = storage.load()
                    assertNotNull(migrated)

                    assertEquals("2026-11-15", migrated.startDate)
                    assertEquals(ThemeMode.DARK, migrated.themeMode)
                    assertTrue(migrated.dailyReminderEnabled)
                    assertEquals(21, migrated.reminderHour)
                    assertTrue(migrated.topics["af-1-os-art"]?.completed == true)
                    assertEquals("Preserved across v2->v3", migrated.questNotes["q1-android-fundamentals"])
                    assertEquals(75, migrated.activityLog["2026-11-20"]?.xpEarned)

                    assertFalse(migrated.studyReminderEnabled)
                    assertEquals(StudySchedule.DEFAULT_DAYS, migrated.studyReminderDays)
                    assertEquals(StudySchedule.DEFAULT_HOUR, migrated.studyReminderHour)
                    assertEquals(StudySchedule.DEFAULT_MINUTE, migrated.studyReminderMinute)

                    val updated = migrated.copy(
                        studyReminderEnabled = true,
                        studyReminderDays = setOf(2, 4, 6),
                        studyReminderHour = 7,
                        studyReminderMinute = 15,
                    )
                    storage.save(updated)
                    val reloaded = storage.load()
                    assertNotNull(reloaded)
                    assertEquals(updated, reloaded)
                    assertTrue(reloaded.dailyReminderEnabled)
                    assertEquals(21, reloaded.reminderHour)
                } finally {
                    db.close()
                }
            } finally {
                tempDir.deleteRecursively()
            }
        }

    private fun seedLegacyV2Database(driver: BundledSQLiteDriver, dbPath: String) {
        val rawConnection = driver.open(dbPath)
        try {
            val statements = listOf(
                "CREATE TABLE IF NOT EXISTS `mission_config` (" +
                    "`id` INTEGER NOT NULL, `version` INTEGER NOT NULL, `startDate` TEXT NOT NULL, " +
                    "`targetDays` INTEGER NOT NULL, `soundEnabled` INTEGER NOT NULL, `themeMode` TEXT NOT NULL, " +
                    "`dailyReminderEnabled` INTEGER NOT NULL, `reminderHour` INTEGER NOT NULL, " +
                    "`lastUpdated` TEXT NOT NULL, PRIMARY KEY(`id`))",
                "CREATE TABLE IF NOT EXISTS `topic_progress` (" +
                    "`subtopicId` TEXT NOT NULL, `completed` INTEGER NOT NULL, `completedAt` TEXT, " +
                    "`lastReviewedAt` TEXT, `nextReviewAt` TEXT, `reviewStage` INTEGER NOT NULL, " +
                    "`reviewCount` INTEGER NOT NULL, `flaggedForReview` INTEGER NOT NULL, " +
                    "`confidence` INTEGER, PRIMARY KEY(`subtopicId`))",
                "CREATE TABLE IF NOT EXISTS `quest_notes` (" +
                    "`questId` TEXT NOT NULL, `note` TEXT NOT NULL, PRIMARY KEY(`questId`))",
                "CREATE TABLE IF NOT EXISTS `custom_links` (" +
                    "`id` TEXT NOT NULL, `questId` TEXT NOT NULL, `title` TEXT NOT NULL, " +
                    "`url` TEXT NOT NULL, `typeName` TEXT NOT NULL, PRIMARY KEY(`id`))",
                "CREATE TABLE IF NOT EXISTS `study_log` (" +
                    "`date` TEXT NOT NULL, `xpEarned` INTEGER NOT NULL, `topicsCompleted` INTEGER NOT NULL, " +
                    "`reviewsDone` INTEGER NOT NULL, PRIMARY KEY(`date`))",
                "INSERT INTO `mission_config` (`id`, `version`, `startDate`, `targetDays`, `soundEnabled`, " +
                    "`themeMode`, `dailyReminderEnabled`, `reminderHour`, `lastUpdated`) VALUES " +
                    "(1, 1, '2026-11-15', 100, 1, 'DARK', 1, 21, '2026-11-22T12:00:00Z')",
                "INSERT INTO `topic_progress` (`subtopicId`, `completed`, `completedAt`, `lastReviewedAt`, " +
                    "`nextReviewAt`, `reviewStage`, `reviewCount`, `flaggedForReview`, `confidence`) VALUES " +
                    "('af-1-os-art', 1, '2026-11-20T10:00:00Z', '2026-11-20T10:00:00Z', " +
                    "'2026-11-23T10:00:00Z', 2, 1, 0, 4)",
                "INSERT INTO `quest_notes` (`questId`, `note`) " +
                    "VALUES ('q1-android-fundamentals', 'Preserved across v2->v3')",
                "INSERT INTO `study_log` (`date`, `xpEarned`, `topicsCompleted`, `reviewsDone`) " +
                    "VALUES ('2026-11-20', 75, 1, 1)",
                "PRAGMA user_version = 2",
            )
            statements.forEach { rawConnection.execSQL(it) }
        } finally {
            rawConnection.close()
        }
    }
}
