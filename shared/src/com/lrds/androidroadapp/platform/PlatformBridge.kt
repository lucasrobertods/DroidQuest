package com.lrds.androidroadapp.platform

import kotlinx.coroutines.flow.StateFlow

sealed interface DeepLinkTarget {
    data class OpenNextRecommendedQuest(val preferredQuestId: String? = null) : DeepLinkTarget
}

interface PlatformBridge {
    val supportsDailyReminders: Boolean
    val supportsNativeFilePicker: Boolean
    val pendingDeepLink: StateFlow<DeepLinkTarget?>

    fun consumePendingDeepLink()

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

    fun syncStudySessionReminder(
        enabled: Boolean,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int,
        nextQuestId: String?,
        nextQuestTitle: String?,
        onResult: (granted: Boolean, message: String) -> Unit,
    )
}

expect fun createPlatformBridge(): PlatformBridge
