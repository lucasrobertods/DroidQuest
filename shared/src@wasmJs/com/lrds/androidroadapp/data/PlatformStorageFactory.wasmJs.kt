package com.lrds.androidroadapp.data

import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.UserProgressState
import kotlin.js.ExperimentalWasmJsInterop

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsGetLocalStorage(key: String): String? =
    js("typeof globalThis.localStorage !== 'undefined' ? globalThis.localStorage.getItem(key) : null")

@OptIn(ExperimentalWasmJsInterop::class)
private fun jsSetLocalStorage(key: String, value: String) {
    js("if (typeof globalThis.localStorage !== 'undefined') { globalThis.localStorage.setItem(key, value); }")
}

private const val WEB_STORAGE_KEY = "droidquest_progress_v1"

class WebLocalStorageProgressStorage : ProgressStorage {
    override val storageLabel: String = "Browser LocalStorage"

    override val savedStatus: SaveStatus = SaveStatus.SAVED_BROWSER

    override suspend fun load(): UserProgressState? {
        val raw = runCatching { jsGetLocalStorage(WEB_STORAGE_KEY) }.getOrNull() ?: return null
        return ProgressJsonCodec.decode(raw).getOrNull()
    }

    override suspend fun save(state: UserProgressState) {
        val encoded = ProgressJsonCodec.encode(state)
        runCatching { jsSetLocalStorage(WEB_STORAGE_KEY, encoded) }
    }
}

actual fun createPlatformProgressStorage(): ProgressStorage = WebLocalStorageProgressStorage()
