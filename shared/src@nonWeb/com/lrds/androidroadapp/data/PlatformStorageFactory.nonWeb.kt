package com.lrds.androidroadapp.data

import com.lrds.androidroadapp.data.db.RoomProgressStorage
import com.lrds.androidroadapp.data.db.createDroidQuestDatabase

actual fun createPlatformProgressStorage(): ProgressStorage =
    RoomProgressStorage(createDroidQuestDatabase())
