package com.lrds.androidroadapp

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.lrds.androidroadapp.data.db.DroidQuestDatabase
import com.lrds.androidroadapp.data.db.RoomProgressStorage
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.CustomLink
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ThemeMode
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class RoomProgressStorageTest {
    @Test
    fun `room kmp bundled sqlite saves and restores complete progress state`() = runTest {
        val db = Room.inMemoryDatabaseBuilder<DroidQuestDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()

        try {
            val storage = RoomProgressStorage(db)
            val now = Instant.parse("2026-11-22T12:00:00Z")
            val initial = RoadmapCatalog.createInitialProgressState(now, TimeZone.UTC).copy(
                themeMode = ThemeMode.LIGHT,
                dailyReminderEnabled = true,
                reminderHour = 19,
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
}
