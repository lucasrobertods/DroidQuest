package com.lrds.androidroadapp.platform

interface PlatformBridge {
    val supportsDailyReminders: Boolean
    val supportsNativeFilePicker: Boolean

    fun openUrl(url: String)

    fun exportProgressJson(
        fileName: String,
        jsonContent: String,
        onResult: (success: Boolean, message: String) -> Unit,
    )

    fun pickProgressJson(onPicked: (rawJson: String?) -> Unit)

    fun syncDailyReviewReminder(
        enabled: Boolean,
        hour: Int,
        dueCount: Int,
        onResult: (granted: Boolean, message: String) -> Unit,
    )
}

expect fun createPlatformBridge(): PlatformBridge
