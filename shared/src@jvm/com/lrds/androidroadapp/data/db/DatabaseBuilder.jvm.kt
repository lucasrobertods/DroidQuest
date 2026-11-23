package com.lrds.androidroadapp.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import java.io.File

actual fun createDroidQuestDatabase(): DroidQuestDatabase {
    val appDir = File(System.getProperty("user.home"), ".droidquest")
    if (!appDir.exists()) {
        appDir.mkdirs()
    }
    val dbFile = File(appDir, "droidquest.db")
    return Room.databaseBuilder<DroidQuestDatabase>(
        name = dbFile.absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .addMigrations(DROIDQUEST_MIGRATION_1_2, DROIDQUEST_MIGRATION_2_3)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
