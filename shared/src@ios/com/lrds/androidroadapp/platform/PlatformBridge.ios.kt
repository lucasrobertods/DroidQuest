package com.lrds.androidroadapp.platform

import com.lrds.androidroadapp.domain.model.StudySchedule
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSDateComponents
import platform.Foundation.NSString
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.writeToFile
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotification
import platform.UserNotifications.UNNotificationPresentationOptionBanner
import platform.UserNotifications.UNNotificationPresentationOptionSound
import platform.UserNotifications.UNNotificationPresentationOptions
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationResponse
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.UserNotifications.UNUserNotificationCenterDelegateProtocol
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

private const val IOS_REMINDER_ID = "com.lrds.androidroadapp.daily_review"
private const val IOS_STUDY_REMINDER_PREFIX = "com.lrds.androidroadapp.study_session.day_"
private const val IOS_STUDY_THREAD_ID = "droidquest_study_schedule"
private const val IOS_DEEP_LINK_KEY = "deep_link"
private const val IOS_DEEP_LINK_NEXT_QUEST = "next_quest"
private const val IOS_QUEST_ID_KEY = "quest_id"

private val IOS_ALL_STUDY_REMINDER_IDS: List<String> =
    StudySchedule.ALL_ISO_DAYS.map { "$IOS_STUDY_REMINDER_PREFIX$it" }

private class StudyNotificationCenterDelegate(
    private val onDeepLink: (DeepLinkTarget) -> Unit,
) : NSObject(), UNUserNotificationCenterDelegateProtocol {
    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        willPresentNotification: UNNotification,
        withCompletionHandler: (UNNotificationPresentationOptions) -> Unit,
    ) {
        withCompletionHandler(UNNotificationPresentationOptionBanner or UNNotificationPresentationOptionSound)
    }

    override fun userNotificationCenter(
        center: UNUserNotificationCenter,
        didReceiveNotificationResponse: UNNotificationResponse,
        withCompletionHandler: () -> Unit,
    ) {
        val userInfo = didReceiveNotificationResponse.notification.request.content.userInfo
        val deepLink = userInfo[IOS_DEEP_LINK_KEY] as? String
        if (deepLink == IOS_DEEP_LINK_NEXT_QUEST) {
            val questId = (userInfo[IOS_QUEST_ID_KEY] as? String)?.takeIf { it.isNotBlank() }
            dispatch_async(dispatch_get_main_queue()) {
                onDeepLink(DeepLinkTarget.OpenNextRecommendedQuest(questId))
            }
        }
        withCompletionHandler()
    }
}

private class IosPlatformBridge : PlatformBridge {
    override val supportsDailyReminders: Boolean = true
    override val supportsNativeFilePicker: Boolean = false

    private val _pendingDeepLink = MutableStateFlow<DeepLinkTarget?>(null)
    override val pendingDeepLink: StateFlow<DeepLinkTarget?> = _pendingDeepLink.asStateFlow()

    // Retained strongly because UNUserNotificationCenter.delegate is a weak reference.
    private val notificationDelegate = StudyNotificationCenterDelegate { target ->
        _pendingDeepLink.value = target
    }

    init {
        UNUserNotificationCenter.currentNotificationCenter().delegate = notificationDelegate
    }

    override fun consumePendingDeepLink() {
        _pendingDeepLink.value = null
    }

    override fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(
            url = nsUrl,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null,
        )
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    override fun exportProgressJson(
        fileName: String,
        jsonContent: String,
        onResult: (success: Boolean, message: String) -> Unit,
    ) {
        val tempPath = "${NSTemporaryDirectory()}$fileName"
        val nsText = NSString.create(string = jsonContent)
        val written = nsText.writeToFile(
            path = tempPath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null,
        )
        if (!written) {
            onResult(false, "Could not prepare $fileName for export.")
            return
        }

        val fileUrl = NSURL.fileURLWithPath(tempPath)
        val controller = topViewController()
        if (controller == null) {
            onResult(false, "Unable to open iOS share sheet.")
            return
        }

        val shareSheet = UIActivityViewController(
            activityItems = listOf(fileUrl),
            applicationActivities = null,
        )
        controller.presentViewController(shareSheet, animated = true, completion = null)
        onResult(true, "Share sheet ready for $fileName!")
    }

    override fun pickProgressJson(onPicked: (rawJson: String?) -> Unit) {
        // Users can paste or restore JSON directly from Settings on iOS
        onPicked(null)
    }

    override fun syncDailyReviewReminder(
        enabled: Boolean,
        hour: Int,
        dueCount: Int,
        onResult: (granted: Boolean, message: String) -> Unit,
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        if (!enabled) {
            center.removePendingNotificationRequestsWithIdentifiers(listOf(IOS_REMINDER_ID))
            dispatchMain { onResult(false, "Daily review reminders paused.") }
            return
        }

        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options) { granted, _ ->
            if (!granted) {
                dispatchMain { onResult(false, "Notification permission denied — enable in iOS Settings.") }
                return@requestAuthorizationWithOptions
            }

            val content = UNMutableNotificationContent().apply {
                setTitle("DroidQuest Daily Review Radar")
                setBody(
                    if (dueCount > 0) {
                        "You have $dueCount spaced-repetition topic${if (dueCount == 1) "" else "s"} ready for active recall!"
                    } else {
                        "Keep your 100-Day Senior Android streak alive — review a quest or tackle a topic today!"
                    }
                )
                setSound(UNNotificationSound.defaultSound)
            }

            val components = NSDateComponents().apply {
                this.hour = hour.coerceIn(0, 23).toLong()
                this.minute = 0L
            }
            val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                dateComponents = components,
                repeats = true,
            )
            val request = UNNotificationRequest.requestWithIdentifier(
                identifier = IOS_REMINDER_ID,
                content = content,
                trigger = trigger,
            )

            center.removePendingNotificationRequestsWithIdentifiers(listOf(IOS_REMINDER_ID))
            center.addNotificationRequest(request) { error ->
                dispatchMain {
                    if (error == null) {
                        onResult(true, "Daily review reminder set for ${hour.toString().padStart(2, '0')}:00!")
                    } else {
                        onResult(false, "Could not schedule iOS reminder.")
                    }
                }
            }
        }
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
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val normalizedDays = StudySchedule.normalizeDays(daysOfWeek)
        val clampedHour = StudySchedule.clampHour(hour)
        val clampedMinute = StudySchedule.clampMinute(minute)

        if (!enabled || normalizedDays.isEmpty()) {
            center.removePendingNotificationRequestsWithIdentifiers(IOS_ALL_STUDY_REMINDER_IDS)
            val msg = if (!enabled) {
                "Study schedule reminders paused."
            } else {
                "Select at least one day to schedule study reminders."
            }
            dispatchMain { onResult(false, msg) }
            return
        }

        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options) { granted, _ ->
            if (!granted) {
                center.removePendingNotificationRequestsWithIdentifiers(IOS_ALL_STUDY_REMINDER_IDS)
                dispatchMain { onResult(false, "Notification permission denied — enable in iOS Settings.") }
                return@requestAuthorizationWithOptions
            }

            center.removePendingNotificationRequestsWithIdentifiers(IOS_ALL_STUDY_REMINDER_IDS)

            val summary = "${StudySchedule.formatDaysSummary(normalizedDays)} at ${StudySchedule.formatTime(clampedHour, clampedMinute)}"
            val bodyText = if (!nextQuestTitle.isNullOrBlank()) {
                "Time for your study session — continue \"$nextQuestTitle\"."
            } else {
                "Time for your scheduled 100-Day DroidQuest study session!"
            }

            val total = normalizedDays.size
            var completed = 0
            var anyError = false

            normalizedDays.forEach { isoDay ->
                val content = UNMutableNotificationContent().apply {
                    setTitle("DroidQuest Study Session")
                    setBody(bodyText)
                    setSound(UNNotificationSound.defaultSound)
                    setThreadIdentifier(IOS_STUDY_THREAD_ID)
                    setUserInfo(
                        mapOf(
                            IOS_DEEP_LINK_KEY to IOS_DEEP_LINK_NEXT_QUEST,
                            IOS_QUEST_ID_KEY to (nextQuestId ?: ""),
                        )
                    )
                }

                val components = NSDateComponents().apply {
                    this.weekday = StudySchedule.isoDayToGregorianWeekday(isoDay).toLong()
                    this.hour = clampedHour.toLong()
                    this.minute = clampedMinute.toLong()
                }
                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    dateComponents = components,
                    repeats = true,
                )
                val request = UNNotificationRequest.requestWithIdentifier(
                    identifier = "$IOS_STUDY_REMINDER_PREFIX$isoDay",
                    content = content,
                    trigger = trigger,
                )

                center.addNotificationRequest(request) { error ->
                    dispatchMain {
                        if (error != null) anyError = true
                        completed += 1
                        if (completed == total) {
                            if (!anyError) {
                                onResult(true, "Study schedule set: $summary")
                            } else {
                                onResult(false, "Could not schedule iOS study session reminders.")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun dispatchMain(block: () -> Unit) {
        dispatch_async(dispatch_get_main_queue(), block)
    }

    private fun topViewController(): UIViewController? {
        @Suppress("DEPRECATION")
        val keyWindow = UIApplication.sharedApplication.keyWindow
            ?: UIApplication.sharedApplication.windows.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
        var top = keyWindow?.rootViewController
        while (top?.presentedViewController != null) {
            top = top.presentedViewController
        }
        return top
    }
}

actual fun createPlatformBridge(): PlatformBridge = IosPlatformBridge()
