package com.lrds.androidroadapp.platform

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
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
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

private const val IOS_REMINDER_ID = "com.lrds.androidroadapp.daily_review"

private class IosPlatformBridge : PlatformBridge {
    override val supportsDailyReminders: Boolean = true
    override val supportsNativeFilePicker: Boolean = false

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
            onResult(false, "Daily review reminders paused.")
            return
        }

        val options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        center.requestAuthorizationWithOptions(options) { granted, _ ->
            if (!granted) {
                onResult(false, "Notification permission denied — enable in iOS Settings.")
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
                if (error == null) {
                    onResult(true, "Daily review reminder set for ${hour.toString().padStart(2, '0')}:00!")
                } else {
                    onResult(false, "Could not schedule iOS reminder.")
                }
            }
        }
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
