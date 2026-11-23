package com.lrds.androidroadapp.data.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val DROIDQUEST_MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE mission_config ADD COLUMN themeMode TEXT NOT NULL DEFAULT 'SYSTEM'"
        )
    }
}

@Database(
    entities = [
        MissionConfigEntity::class,
        TopicProgressEntity::class,
        QuestNoteEntity::class,
        CustomLinkEntity::class,
        StudyLogEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@ConstructedBy(DroidQuestDatabaseConstructor::class)
abstract class DroidQuestDatabase : RoomDatabase() {
    abstract fun droidQuestDao(): DroidQuestDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object DroidQuestDatabaseConstructor : RoomDatabaseConstructor<DroidQuestDatabase> {
    override fun initialize(): DroidQuestDatabase
}
