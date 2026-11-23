package com.lrds.androidroadapp.platform

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import java.util.Calendar

object AndroidPlatformHost {
    private var appContext: Context? = null

    var onExportDocumentRequested: (
        (fileName: String, content: String, onResult: (Boolean, String) -> Unit) -> Unit
    )? = null
    var onOpenDocumentRequested: ((onPicked: (String?) -> Unit) -> Unit)? = null
    var onRequestNotificationPermission: ((onGranted: (Boolean) -> Unit) -> Unit)? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun requireAppContext(): Context = requireNotNull(appContext) {
        "AndroidPlatformHost.init(context) must be called in MainActivity.onCreate() " +
            "before accessing Room or PlatformBridge."
    }
}

private const val REMINDER_CHANNEL_ID = "droidquest_spaced_repetition"
private const val REMINDER_REQUEST_CODE = 10042

class ReviewReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        ensureChannel(manager)

        val dueCount = intent?.getIntExtra("due_count", 0) ?: 0
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                REMINDER_REQUEST_CODE + 1,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val bodyText = if (dueCount > 0) {
            "You have $dueCount spaced-repetition topic${if (dueCount == 1) "" else "s"} ready for active recall!"
        } else {
            "Keep your 100-Day Senior Android streak alive — review a quest or tackle a topic today!"
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, REMINDER_CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            android.app.Notification.Builder(context)
        }

        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("DroidQuest Daily Review Radar")
            .setContentText(bodyText)
            .setAutoCancel(true)
            .apply {
                if (contentIntent != null) setContentIntent(contentIntent)
            }
            .build()

        manager.notify(REMINDER_REQUEST_CODE, notification)
    }

    companion object {
        fun ensureChannel(manager: NotificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    REMINDER_CHANNEL_ID,
                    "Spaced Repetition Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Daily study & spaced repetition review notifications for DroidQuest"
                }
                manager.createNotificationChannel(channel)
            }
        }
    }
}

private class AndroidPlatformBridge : PlatformBridge {
    override val supportsDailyReminders: Boolean = true
    override val supportsNativeFilePicker: Boolean = true

    override fun openUrl(url: String) {
        val ctx = AndroidPlatformHost.requireAppContext()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { ctx.startActivity(intent) }
    }

    override fun exportProgressJson(
        fileName: String,
        jsonContent: String,
        onResult: (success: Boolean, message: String) -> Unit,
    ) {
        val handler = AndroidPlatformHost.onExportDocumentRequested
        if (handler != null) {
            handler(fileName, jsonContent, onResult)
            return
        }
        // Fallback: Android share sheet
        val ctx = AndroidPlatformHost.requireAppContext()
        val share = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, fileName)
            putExtra(Intent.EXTRA_TEXT, jsonContent)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching {
            val chooser = Intent.createChooser(share, "Export DroidQuest Backup")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(chooser)
            onResult(true, "Opened share sheet for $fileName")
        }.onFailure {
            onResult(false, "Could not export backup JSON.")
        }
    }

    override fun pickProgressJson(onPicked: (rawJson: String?) -> Unit) {
        val handler = AndroidPlatformHost.onOpenDocumentRequested
        if (handler != null) {
            handler(onPicked)
        } else {
            onPicked(null)
        }
    }

    override fun syncDailyReviewReminder(
        enabled: Boolean,
        hour: Int,
        dueCount: Int,
        onResult: (granted: Boolean, message: String) -> Unit,
    ) {
        val ctx = AndroidPlatformHost.requireAppContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val pendingIntent = buildReminderPendingIntent(ctx, dueCount)

        if (!enabled) {
            alarmManager?.cancel(pendingIntent)
            onResult(false, "Daily review reminders paused.")
            return
        }

        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

        if (needsPermission) {
            val requester = AndroidPlatformHost.onRequestNotificationPermission
            if (requester != null) {
                requester { granted ->
                    if (granted) {
                        scheduleRepeatingAlarm(ctx, alarmManager, pendingIntent, hour)
                        onResult(true, "Daily review reminder set for ${hour.toString().padStart(2, '0')}:00!")
                    } else {
                        onResult(false, "Notification permission denied — enable in Android Settings.")
                    }
                }
                return
            }
        }

        scheduleRepeatingAlarm(ctx, alarmManager, pendingIntent, hour)
        onResult(true, "Daily review reminder set for ${hour.toString().padStart(2, '0')}:00!")
    }

    private fun buildReminderPendingIntent(ctx: Context, dueCount: Int): PendingIntent {
        val intent = Intent(ctx, ReviewReminderReceiver::class.java).apply {
            putExtra("due_count", dueCount)
        }
        return PendingIntent.getBroadcast(
            ctx,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun scheduleRepeatingAlarm(
        ctx: Context,
        alarmManager: AlarmManager?,
        pendingIntent: PendingIntent,
        hour: Int,
    ) {
        val notifManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notifManager != null) {
            ReviewReminderReceiver.ensureChannel(notifManager)
        }
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, MAX_REMINDER_HOUR))
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent,
        )
    }

    private companion object {
        const val MAX_REMINDER_HOUR = 23
    }
}

actual fun createPlatformBridge(): PlatformBridge = AndroidPlatformBridge()
