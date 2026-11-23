package com.lrds.androidroadapp.data

import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.UserProgressState

interface ProgressStorage {
    val storageLabel: String
    val savedStatus: SaveStatus
    suspend fun load(): UserProgressState?
    suspend fun save(state: UserProgressState)
}

expect fun createPlatformProgressStorage(): ProgressStorage
