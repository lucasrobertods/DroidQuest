package com.lrds.androidroadapp.data.db

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun createDroidQuestDatabase(): DroidQuestDatabase {
    val fileManager = NSFileManager.defaultManager
    val dirUrl = fileManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    )
    val basePath = requireNotNull(dirUrl?.path) { "Unable to resolve iOS Application Support directory" }
    val dbPath = "$basePath/droidquest.db"
    return Room.databaseBuilder<DroidQuestDatabase>(
        name = dbPath,
    )
        .setDriver(BundledSQLiteDriver())
        .addMigrations(DROIDQUEST_MIGRATION_1_2, DROIDQUEST_MIGRATION_2_3, DROIDQUEST_MIGRATION_3_4)
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
