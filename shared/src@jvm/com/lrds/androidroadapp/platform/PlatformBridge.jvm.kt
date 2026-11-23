package com.lrds.androidroadapp.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.awt.Desktop
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URI

private class JvmPlatformBridge : PlatformBridge {
    override val supportsDailyReminders: Boolean = false
    override val supportsNativeFilePicker: Boolean = true
    override val pendingDeepLink: StateFlow<DeepLinkTarget?> = MutableStateFlow<DeepLinkTarget?>(null).asStateFlow()

    override fun consumePendingDeepLink() = Unit

    override fun openUrl(url: String) {
        runCatching {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(url))
            }
        }
    }

    override fun exportProgressJson(
        fileName: String,
        jsonContent: String,
        onResult: (success: Boolean, message: String) -> Unit,
    ) {
        runCatching {
            val dialog = FileDialog(null as Frame?, "Export DroidQuest Backup JSON", FileDialog.SAVE).apply {
                file = fileName
                isVisible = true
            }
            val dir = dialog.directory
            val selectedFile = dialog.file
            if (dir != null && selectedFile != null) {
                File(dir, selectedFile).writeText(jsonContent, Charsets.UTF_8)
                onResult(true, "Backup saved to $selectedFile!")
            } else {
                onResult(false, "Export cancelled.")
            }
        }.onFailure {
            onResult(false, "Could not save backup file.")
        }
    }

    override fun pickProgressJson(onPicked: (rawJson: String?) -> Unit) {
        val result = runCatching {
            val dialog = FileDialog(null as Frame?, "Import DroidQuest Backup JSON", FileDialog.LOAD).apply {
                setFilenameFilter { _, name -> name.endsWith(".json", ignoreCase = true) }
                isVisible = true
            }
            val dir = dialog.directory
            val selectedFile = dialog.file
            if (dir != null && selectedFile != null) {
                File(dir, selectedFile).readText(Charsets.UTF_8)
            } else {
                null
            }
        }.getOrNull()
        onPicked(result)
    }

    override fun syncDailyReviewReminder(
        enabled: Boolean,
        hour: Int,
        dueCount: Int,
        onResult: (granted: Boolean, message: String) -> Unit,
    ) {
        onResult(false, "Local push reminders are available on Android & iOS.")
    }

    override fun syncStudySessionReminder(
        enabled: Boolean,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int,
        nextQuestId: String?,
        nextQuestTitle: String?,
        onResult: (granted: Boolean, message: String) -> Unit,
    ) {
        onResult(false, "Local push reminders are available on Android & iOS.")
    }
}

actual fun createPlatformBridge(): PlatformBridge = JvmPlatformBridge()
