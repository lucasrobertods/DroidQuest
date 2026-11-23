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
import com.lrds.androidroadapp.domain.model.StudySchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.TimeZone

object AndroidPlatformHost {
    private var appContext: Context? = null
    private val _pendingDeepLink = MutableStateFlow<DeepLinkTarget?>(null)
    val pendingDeepLink: StateFlow<DeepLinkTarget?> = _pendingDeepLink.asStateFlow()

    var onExportDocumentRequested: ((fileName: String, content: String, onResult: (Boolean, String) -> Unit) -> Unit)? = null
    var onOpenDocumentRequested: ((onPicked: (String?) -> Unit) -> Unit)? = null
    var onRequestNotificationPermission: ((onGranted: (Boolean) -> Unit) -> Unit)? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun requireAppContext(): Context = requireNotNull(appContext) {
        "AndroidPlatformHost.init(context) must be called in MainActivity.onCreate() before accessing Room or PlatformBridge."
    }

    fun handleLaunchIntent(intent: Intent?) {
        if (intent?.getBooleanExtra(EXTRA_OPEN_NEXT_QUEST, false) == true) {
            val preferredQuestId = intent.getStringExtra(EXTRA_NEXT_QUEST_ID)?.takeIf { it.isNotBlank() }
            _pendingDeepLink.value = DeepLinkTarget.OpenNextRecommendedQuest(preferredQuestId)
            intent.removeExtra(EXTRA_OPEN_NEXT_QUEST)
        }
    }

    fun consumePendingDeepLink() {
        _pendingDeepLink.value = null
    }
}

private const val REMINDER_CHANNEL_ID = "droidquest_spaced_repetition"
private const val REMINDER_REQUEST_CODE = 10042

internal const val STUDY_CHANNEL_ID = "droidquest_study_schedule"
private const val STUDY_NOTIFICATION_ID = 20040
private const val STUDY_ALARM_BASE_REQUEST_CODE = 20040
private const val STUDY_CONTENT_INTENT_REQUEST_CODE = 20048

internal const val EXTRA_OPEN_NEXT_QUEST = "com.lrds.androidroadapp.extra.OPEN_NEXT_QUEST"
internal const val EXTRA_NEXT_QUEST_ID = "com.lrds.androidroadapp.extra.NEXT_QUEST_ID"
internal const val EXTRA_NEXT_QUEST_TITLE = "com.lrds.androidroadapp.extra.NEXT_QUEST_TITLE"

internal fun computeNextWeeklyTriggerMillis(
    isoDayOfWeek: Int,
    hour: Int,
    minute: Int,
    nowMillis: Long = System.currentTimeMillis(),
    timeZone: TimeZone = TimeZone.getDefault(),
): Long {
    val targetCalendarDay = StudySchedule.isoDayToGregorianWeekday(isoDayOfWeek)
    val calendar = Calendar.getInstance(timeZone).apply {
        timeInMillis = nowMillis
        val currentCalendarDay = get(Calendar.DAY_OF_WEEK)
        val daysAhead = (targetCalendarDay - currentCalendarDay + 7) % 7
        add(Calendar.DAY_OF_YEAR, daysAhead)
        set(Calendar.HOUR_OF_DAY, StudySchedule.clampHour(hour))
        set(Calendar.MINUTE, StudySchedule.clampMinute(minute))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        if (timeInMillis <= nowMillis) {
            add(Calendar.DAY_OF_YEAR, 7)
        }
    }
    return calendar.timeInMillis
}

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

class StudySessionReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        ensureChannel(manager)

        val nextQuestId = intent?.getStringExtra(EXTRA_NEXT_QUEST_ID)?.takeIf { it.isNotBlank() }
        val nextQuestTitle = intent?.getStringExtra(EXTRA_NEXT_QUEST_TITLE)?.takeIf { it.isNotBlank() }

        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_NEXT_QUEST, true)
            if (nextQuestId != null) {
                putExtra(EXTRA_NEXT_QUEST_ID, nextQuestId)
            }
        }
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                STUDY_CONTENT_INTENT_REQUEST_CODE,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        val bodyText = if (nextQuestTitle != null) {
            "Time for your study session — continue \"$nextQuestTitle\"."
        } else {
            "Time for your scheduled 100-Day DroidQuest study session!"
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, STUDY_CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            android.app.Notification.Builder(context)
        }

        val notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("DroidQuest Study Session")
            .setContentText(bodyText)
            .setAutoCancel(true)
            .apply {
                if (contentIntent != null) setContentIntent(contentIntent)
            }
            .build()

        manager.notify(STUDY_NOTIFICATION_ID, notification)
    }

    companion object {
        fun ensureChannel(manager: NotificationManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    STUDY_CHANNEL_ID,
                    "Study Schedule",
                    NotificationManager.IMPORTANCE_DEFAULT,
                ).apply {
                    description = "Recurring weekly study session reminders for DroidQuest"
                }
                manager.createNotificationChannel(channel)
            }
        }
    }
}

private class AndroidPlatformBridge : PlatformBridge {
    override val supportsDailyReminders: Boolean = true
    override val supportsNativeFilePicker: Boolean = true
    override val pendingDeepLink: StateFlow<DeepLinkTarget?> = AndroidPlatformHost.pendingDeepLink

    override fun consumePendingDeepLink() {
        AndroidPlatformHost.consumePendingDeepLink()
    }

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
            ctx.startActivity(Intent.createChooser(share, "Export DroidQuest Backup").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
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
            onResult(false, "Notification permission required — enable in Android Settings.")
            return
        }

        scheduleRepeatingAlarm(ctx, alarmManager, pendingIntent, hour)
        onResult(true, "Daily review reminder set for ${hour.toString().padStart(2, '0')}:00!")
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
        val ctx = AndroidPlatformHost.requireAppContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        val normalizedDays = StudySchedule.normalizeDays(daysOfWeek)
        val clampedHour = StudySchedule.clampHour(hour)
        val clampedMinute = StudySchedule.clampMinute(minute)

        if (!enabled || normalizedDays.isEmpty()) {
            cancelAllStudyAlarms(ctx, alarmManager)
            val msg = if (!enabled) {
                "Study schedule reminders paused."
            } else {
                "Select at least one day to schedule study reminders."
            }
            onResult(false, msg)
            return
        }

        val needsPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED

        val summary = "${StudySchedule.formatDaysSummary(normalizedDays)} at ${StudySchedule.formatTime(clampedHour, clampedMinute)}"
        if (needsPermission) {
            val requester = AndroidPlatformHost.onRequestNotificationPermission
            if (requester != null) {
                requester { granted ->
                    if (granted) {
                        scheduleWeeklyStudyAlarms(
                            ctx = ctx,
                            alarmManager = alarmManager,
                            days = normalizedDays,
                            hour = clampedHour,
                            minute = clampedMinute,
                            nextQuestId = nextQuestId,
                            nextQuestTitle = nextQuestTitle,
                        )
                        onResult(true, "Study schedule set: $summary")
                    } else {
                        cancelAllStudyAlarms(ctx, alarmManager)
                        onResult(false, "Notification permission denied — enable in Android Settings.")
                    }
                }
                return
            }
            cancelAllStudyAlarms(ctx, alarmManager)
            onResult(false, "Notification permission required — enable in Android Settings.")
            return
        }

        scheduleWeeklyStudyAlarms(
            ctx = ctx,
            alarmManager = alarmManager,
            days = normalizedDays,
            hour = clampedHour,
            minute = clampedMinute,
            nextQuestId = nextQuestId,
            nextQuestTitle = nextQuestTitle,
        )
        onResult(true, "Study schedule set: $summary")
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

    private fun buildStudyPendingIntent(
        ctx: Context,
        isoDay: Int,
        nextQuestId: String?,
        nextQuestTitle: String?,
    ): PendingIntent {
        val intent = Intent(ctx, StudySessionReminderReceiver::class.java).apply {
            if (!nextQuestId.isNullOrBlank()) putExtra(EXTRA_NEXT_QUEST_ID, nextQuestId)
            if (!nextQuestTitle.isNullOrBlank()) putExtra(EXTRA_NEXT_QUEST_TITLE, nextQuestTitle)
        }
        return PendingIntent.getBroadcast(
            ctx,
            STUDY_ALARM_BASE_REQUEST_CODE + isoDay,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun cancelAllStudyAlarms(ctx: Context, alarmManager: AlarmManager?) {
        StudySchedule.ALL_ISO_DAYS.forEach { isoDay ->
            val pendingIntent = buildStudyPendingIntent(ctx, isoDay, null, null)
            alarmManager?.cancel(pendingIntent)
        }
    }

    private fun scheduleWeeklyStudyAlarms(
        ctx: Context,
        alarmManager: AlarmManager?,
        days: Set<Int>,
        hour: Int,
        minute: Int,
        nextQuestId: String?,
        nextQuestTitle: String?,
    ) {
        val notifManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        if (notifManager != null) {
            StudySessionReminderReceiver.ensureChannel(notifManager)
        }
        val weeklyIntervalMillis = AlarmManager.INTERVAL_DAY * 7L
        StudySchedule.ALL_ISO_DAYS.forEach { isoDay ->
            val pendingIntent = buildStudyPendingIntent(ctx, isoDay, nextQuestId, nextQuestTitle)
            alarmManager?.cancel(pendingIntent)
            if (isoDay in days) {
                val triggerAtMillis = computeNextWeeklyTriggerMillis(isoDay, hour, minute)
                alarmManager?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    weeklyIntervalMillis,
                    pendingIntent,
                )
            }
        }
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
            set(Calendar.HOUR_OF_DAY, hour.coerceIn(0, 23))
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
}

actual fun createPlatformBridge(): PlatformBridge = AndroidPlatformBridge()
