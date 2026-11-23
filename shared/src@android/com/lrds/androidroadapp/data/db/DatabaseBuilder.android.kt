package com.lrds.androidroadapp.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.lrds.androidroadapp.platform.AndroidPlatformHost
import kotlinx.coroutines.Dispatchers

actual fun createDroidQuestDatabase(): DroidQuestDatabase {
    val context = AndroidPlatformHost.requireAppContext()
    val dbFile = context.getDatabasePath("droidquest.db")
    return Room.databaseBuilder<DroidQuestDatabase>(
        context = context,
        name = dbFile.absolutePath,
    )
        .setDriver(BundledSQLiteDriver())
        .addMigrations(DROIDQUEST_MIGRATION_1_2)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
