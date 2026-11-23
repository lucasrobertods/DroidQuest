package com.lrds.androidroadapp.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.components.SecondaryTonalButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.model.AppLanguage
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.presentation.StableSet
import com.lrds.androidroadapp.presentation.quest.questTextFieldColors
import com.lrds.androidroadapp.presentation.toStableSet

private val REMINDER_HOUR_PRESETS = listOf(9, 13, 19, 21)
private val STUDY_TIME_PRESETS = listOf(8 to 0, 12 to 30, 19 to 0, 21 to 0)

private const val ACTIVE_CARD_BORDER_ALPHA = 0.35f
private const val SELECTED_CHIP_BORDER_ALPHA = 0.55f
private const val SELECTED_THEME_BORDER_ALPHA = 0.65f
private const val ERROR_BORDER_ALPHA = 0.5f

private const val HOURS_PER_DAY = 24
private const val PREV_HOUR_OFFSET = 23
private const val MINUTES_PER_HOUR = 60
private const val MINUTE_STEP = 5
private const val PREV_MINUTE_OFFSET = 55

@Composable
fun SettingsDialog(
    startDate: String,
    themeMode: ThemeMode,
    saveStatus: SaveStatus,
    storageLabel: String,
    dailyReminderEnabled: Boolean,
    reminderHour: Int,
    studyReminderEnabled: Boolean,
    studyReminderDays: StableSet<Int>,
    studyReminderHour: Int,
    studyReminderMinute: Int,
    supportsDailyReminders: Boolean,
    supportsNativeFilePicker: Boolean,
    onClose: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onUpdateStartDate: (String) -> Unit,
    onSetDailyReminder: (Boolean, Int) -> Unit,
    onSetStudySchedule: (Boolean, Set<Int>, Int, Int) -> Unit,
    onExportJson: () -> Unit,
    onPickJsonFile: () -> Unit,
    onImportJsonRaw: (String) -> Unit,
    onGetCurrentJson: () -> String,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
    appLanguage: AppLanguage = AppLanguage.SYSTEM,
    onSetAppLanguage: (AppLanguage) -> Unit = {},
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .clickable(onClick = onClose)
            .padding(spacing.screenHorizontal)
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = DroidQuestCornerShapes.Dialog,
            border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
            shadowElevation = sizing.elevationModal,
            modifier = Modifier
                .widthIn(max = sizing.dialogMaxWidth)
                .fillMaxWidth()
                .clickable(enabled = false) {},
        ) {
            Column {
                SettingsDialogHeader(onClose = onClose)

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(
                    modifier = Modifier
                        .heightIn(max = sizing.dialogMaxHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.dialogPadding),
                    verticalArrangement = Arrangement.spacedBy(spacing.section),
                ) {
                    AppearanceSection(
                        themeMode = themeMode,
                        onSetThemeMode = onSetThemeMode,
                    )

                    LanguageSection(
                        appLanguage = appLanguage,
                        onSetAppLanguage = onSetAppLanguage,
                    )

                    SprintStartDateSection(
                        startDate = startDate,
                        onUpdateStartDate = onUpdateStartDate,
                    )

                    if (supportsDailyReminders) {
                        NotificationsSection(
                            studyReminderEnabled = studyReminderEnabled,
                            studyReminderDays = studyReminderDays,
                            studyReminderHour = studyReminderHour,
                            studyReminderMinute = studyReminderMinute,
                            dailyReminderEnabled = dailyReminderEnabled,
                            reminderHour = reminderHour,
                            onSetStudySchedule = onSetStudySchedule,
                            onSetDailyReminder = onSetDailyReminder,
                        )
                    }

                    LocalPersistenceCard(
                        storageLabel = storageLabel,
                        saveStatus = saveStatus,
                    )

                    BackupRestoreSection(
                        supportsNativeFilePicker = supportsNativeFilePicker,
                        onExportJson = onExportJson,
                        onPickJsonFile = onPickJsonFile,
                        onImportJsonRaw = onImportJsonRaw,
                        onGetCurrentJson = onGetCurrentJson,
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    DangerZoneResetSection(onReset = onReset)

                    Spacer(Modifier.height(spacing.xxs))
                }
            }
        }
    }
}

@Composable
private fun SettingsDialogHeader(onClose: () -> Unit) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.dialogPadding, vertical = spacing.cardPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            SettingsIconBadge(
                icon = DroidQuestIcons.Settings,
                active = true,
            )
            Text(
                text = "Settings & Backup",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = CircleShape,
            border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .size(sizing.ghostButtonSize)
                .clip(CircleShape)
                .clickable(onClick = onClose),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = DroidQuestIcons.Close,
                    contentDescription = "Close settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(sizing.iconMd),
                )
            }
        }
    }
}

@Composable
private fun AppearanceSection(
    themeMode: ThemeMode,
    onSetThemeMode: (ThemeMode) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val options = listOf(
        ThemeMode.SYSTEM to "System",
        ThemeMode.LIGHT to "Light",
        ThemeMode.DARK to "Dark",
    )

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text(
            text = "APPEARANCE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            options.forEach { (mode, label) ->
                val selected = themeMode == mode
                val bgColor = if (selected) {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
                val borderColor = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = SELECTED_THEME_BORDER_ALPHA)
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                val textColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                Surface(
                    color = bgColor,
                    shape = DroidQuestCornerShapes.TactileButton,
                    border = BorderStroke(borders.hairline, borderColor),
                    modifier = Modifier
                        .weight(1f)
                        .clip(DroidQuestCornerShapes.TactileButton)
                        .clickable { onSetThemeMode(mode) },
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = spacing.base),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LanguageSection(
    appLanguage: AppLanguage,
    onSetAppLanguage: (AppLanguage) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "LANGUAGE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "APP LOCALE",
                style = DroidQuestTheme.mono.micro,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            AppLanguage.entries.forEach { language ->
                val selected = appLanguage == language
                val bgColor = if (selected) {
                    MaterialTheme.colorScheme.surfaceContainerHigh
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
                val borderColor = if (selected) {
                    MaterialTheme.colorScheme.primary.copy(alpha = SELECTED_THEME_BORDER_ALPHA)
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                val textColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                Surface(
                    color = bgColor,
                    shape = DroidQuestCornerShapes.TactileButton,
                    border = BorderStroke(borders.hairline, borderColor),
                    modifier = Modifier
                        .weight(1f)
                        .clip(DroidQuestCornerShapes.TactileButton)
                        .clickable { onSetAppLanguage(language) },
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = spacing.base),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = language.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SprintStartDateSection(
    startDate: String,
    onUpdateStartDate: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    var startDateInput by remember(startDate) { mutableStateOf(startDate) }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        Text(
            text = "100-DAY SPRINT START DATE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = startDateInput,
                onValueChange = { startDateInput = it },
                singleLine = true,
                placeholder = { Text("2026-11-22") },
                colors = questTextFieldColors(),
                shape = DroidQuestCornerShapes.TactileButton,
                modifier = Modifier.weight(1f),
            )
            PrimaryActionButton(
                onClick = { onUpdateStartDate(startDateInput) },
                contentPadding = PaddingValues(
                    horizontal = spacing.cardPadding,
                    vertical = spacing.cardPaddingCompact,
                ),
            ) {
                Text("Apply", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun NotificationsSection(
    studyReminderEnabled: Boolean,
    studyReminderDays: StableSet<Int>,
    studyReminderHour: Int,
    studyReminderMinute: Int,
    dailyReminderEnabled: Boolean,
    reminderHour: Int,
    onSetStudySchedule: (Boolean, Set<Int>, Int, Int) -> Unit,
    onSetDailyReminder: (Boolean, Int) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val anyActive = studyReminderEnabled || dailyReminderEnabled

    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "NOTIFICATIONS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (anyActive) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Box(
                        modifier = Modifier
                            .size(sizing.progressBarStandard)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                    Text(
                        text = "ACTIVE",
                        style = DroidQuestTheme.mono.micro,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        StudyScheduleCard(
            enabled = studyReminderEnabled,
            daysOfWeek = studyReminderDays,
            hour = studyReminderHour,
            minute = studyReminderMinute,
            onUpdate = onSetStudySchedule,
        )

        DailyReminderCard(
            dailyReminderEnabled = dailyReminderEnabled,
            reminderHour = reminderHour,
            onSetDailyReminder = onSetDailyReminder,
        )
    }
}

@Composable
private fun StudyScheduleCard(
    enabled: Boolean,
    daysOfWeek: StableSet<Int>,
    hour: Int,
    minute: Int,
    onUpdate: (enabled: Boolean, daysOfWeek: Set<Int>, hour: Int, minute: Int) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val normalizedDays = remember(daysOfWeek) { StudySchedule.normalizeDays(daysOfWeek).toStableSet() }
    val borderColor = if (enabled) {
        MaterialTheme.colorScheme.primary.copy(alpha = ACTIVE_CARD_BORDER_ALPHA)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, borderColor),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            StudyScheduleHeaderRow(
                enabled = enabled,
                normalizedDays = normalizedDays,
                hour = hour,
                minute = minute,
                onToggle = { checked -> onUpdate(checked, normalizedDays, hour, minute) },
            )

            StudyScheduleWeekdaysSection(
                enabled = enabled,
                normalizedDays = normalizedDays,
                onToggleDay = { isoDay ->
                    val nextDays = if (isoDay in normalizedDays) {
                        normalizedDays - isoDay
                    } else {
                        normalizedDays + isoDay
                    }
                    onUpdate(enabled, nextDays, hour, minute)
                },
            )

            StudyScheduleTimeBar(
                hour = hour,
                minute = minute,
                onHourChange = { nextHour -> onUpdate(enabled, normalizedDays, nextHour, minute) },
                onMinuteChange = { nextMinute -> onUpdate(enabled, normalizedDays, hour, nextMinute) },
            )

            StudySchedulePresetsRow(
                hour = hour,
                minute = minute,
                onSelectPreset = { presetHour, presetMinute ->
                    onUpdate(enabled, normalizedDays, presetHour, presetMinute)
                },
            )
        }
    }
}

@Composable
private fun StudyScheduleHeaderRow(
    enabled: Boolean,
    normalizedDays: StableSet<Int>,
    hour: Int,
    minute: Int,
    onToggle: (Boolean) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val formattedTime = StudySchedule.formatTime(hour, minute)
    val subtitle = when {
        normalizedDays.isEmpty() -> "Choose weekdays and study time"
        enabled -> "${StudySchedule.formatDaysSummary(normalizedDays)} at $formattedTime · Opens Next Quest"
        else -> "Paused · ${StudySchedule.formatDaysSummary(normalizedDays)} at $formattedTime"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            SettingsIconBadge(
                icon = DroidQuestIcons.CalendarClock,
                active = enabled,
            )
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                Text(
                    text = "Study Schedule",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        SettingsToggleSwitch(checked = enabled, onCheckedChange = onToggle)
    }
}

@Composable
private fun StudyScheduleWeekdaysSection(
    enabled: Boolean,
    normalizedDays: StableSet<Int>,
    onToggleDay: (Int) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val dayWord = if (normalizedDays.size == 1) "DAY" else "DAYS"
    val daysPerWeekLabel = "${normalizedDays.size} $dayWord / WK"

    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "TARGET DAYS",
                style = DroidQuestTheme.mono.micro,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = daysPerWeekLabel,
                style = DroidQuestTheme.mono.micro,
                color = if (enabled && normalizedDays.isNotEmpty()) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            StudySchedule.ALL_ISO_DAYS.forEach { isoDay ->
                val selected = isoDay in normalizedDays
                val fillColor = when {
                    selected && enabled -> MaterialTheme.colorScheme.primary
                    selected -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceContainerLowest
                }
                val borderColor = when {
                    selected && enabled -> MaterialTheme.colorScheme.primary
                    selected -> MaterialTheme.colorScheme.primary.copy(alpha = SELECTED_CHIP_BORDER_ALPHA)
                    else -> MaterialTheme.colorScheme.outlineVariant
                }
                val textColor = when {
                    selected && enabled -> MaterialTheme.colorScheme.onPrimary
                    selected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }

                Surface(
                    color = fillColor,
                    shape = DroidQuestCornerShapes.MicroBadge,
                    border = BorderStroke(borders.hairline, borderColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(sizing.ghostButtonSize)
                        .clip(DroidQuestCornerShapes.MicroBadge)
                        .semantics {
                            contentDescription = "${StudySchedule.dayShortName(isoDay)} study reminder"
                        }
                        .clickable { onToggleDay(isoDay) },
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = StudySchedule.dayInitial(isoDay),
                            style = DroidQuestTheme.mono.badge,
                            color = textColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyScheduleTimeBar(
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = DroidQuestCornerShapes.TactileButton,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Icon(
                    imageVector = DroidQuestIcons.Clock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(sizing.iconSm),
                )
                Text(
                    text = "Study time",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                TimeStepperGroup(
                    valueText = StudySchedule.clampHour(hour).toString().padStart(2, '0'),
                    decreaseDescription = "Decrease study hour",
                    increaseDescription = "Increase study hour",
                    onDecrease = { onHourChange((hour + PREV_HOUR_OFFSET) % HOURS_PER_DAY) },
                    onIncrease = { onHourChange((hour + 1) % HOURS_PER_DAY) },
                )
                Text(
                    text = ":",
                    style = DroidQuestTheme.mono.metric,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TimeStepperGroup(
                    valueText = StudySchedule.clampMinute(minute).toString().padStart(2, '0'),
                    decreaseDescription = "Decrease study minute",
                    increaseDescription = "Increase study minute",
                    onDecrease = { onMinuteChange((minute + PREV_MINUTE_OFFSET) % MINUTES_PER_HOUR) },
                    onIncrease = { onMinuteChange((minute + MINUTE_STEP) % MINUTES_PER_HOUR) },
                )
            }
        }
    }
}

@Composable
private fun StudySchedulePresetsRow(
    hour: Int,
    minute: Int,
    onSelectPreset: (Int, Int) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Presets:",
            style = DroidQuestTheme.mono.micro,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        STUDY_TIME_PRESETS.forEach { (presetHour, presetMinute) ->
            val selected = hour == presetHour && minute == presetMinute
            val fillColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLowest
            }
            val borderColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = SELECTED_CHIP_BORDER_ALPHA)
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
            val textColor = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Surface(
                color = fillColor,
                shape = DroidQuestCornerShapes.MicroBadge,
                border = BorderStroke(borders.hairline, borderColor),
                modifier = Modifier
                    .weight(1f)
                    .clip(DroidQuestCornerShapes.MicroBadge)
                    .clickable { onSelectPreset(presetHour, presetMinute) },
            ) {
                Box(
                    modifier = Modifier.padding(vertical = spacing.sm),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = StudySchedule.formatTime(presetHour, presetMinute),
                        style = DroidQuestTheme.mono.micro,
                        color = textColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeStepperGroup(
    valueText: String,
    decreaseDescription: String,
    increaseDescription: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.xs, vertical = spacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(sizing.checkCircleSize)
                    .clip(DroidQuestCornerShapes.MicroBadge)
                    .semantics { contentDescription = decreaseDescription }
                    .clickable(onClick = onDecrease),
            ) {
                Text(
                    text = "−",
                    style = DroidQuestTheme.mono.metric,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = valueText,
                style = DroidQuestTheme.mono.metric,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(sizing.checkCircleSize)
                    .clip(DroidQuestCornerShapes.MicroBadge)
                    .semantics { contentDescription = increaseDescription }
                    .clickable(onClick = onIncrease),
            ) {
                Text(
                    text = "+",
                    style = DroidQuestTheme.mono.metric,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LocalPersistenceCard(
    storageLabel: String,
    saveStatus: SaveStatus,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPaddingCompact),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            SettingsIconBadge(
                icon = DroidQuestIcons.HardDrive,
                active = true,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xxs),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Text(
                        text = storageLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Box(
                        modifier = Modifier
                            .size(sizing.progressBarStandard)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Text(
                    text = "${saveStatus.name.lowercase().replace('_', ' ')} · progress.json compatible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DailyReminderCard(
    dailyReminderEnabled: Boolean,
    reminderHour: Int,
    onSetDailyReminder: (Boolean, Int) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.base),
                ) {
                    SettingsIconBadge(
                        icon = DroidQuestIcons.Bell,
                        active = dailyReminderEnabled,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.xxs)) {
                        Text(
                            text = "Daily Review Reminder",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Spaced repetition alert at ${reminderHour.toString().padStart(2, '0')}:00",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                SettingsToggleSwitch(
                    checked = dailyReminderEnabled,
                    onCheckedChange = { checked -> onSetDailyReminder(checked, reminderHour) },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                REMINDER_HOUR_PRESETS.forEach { hour ->
                    ReminderHourChip(
                        hour = hour,
                        selected = reminderHour == hour,
                        onClick = { onSetDailyReminder(true, hour) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderHourChip(
    hour: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val bgColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = SELECTED_CHIP_BORDER_ALPHA)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bgColor,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = modifier
            .clip(DroidQuestCornerShapes.MicroBadge)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.padding(vertical = spacing.sm),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${hour.toString().padStart(2, '0')}:00",
                style = DroidQuestTheme.mono.micro,
                color = textColor,
            )
        }
    }
}

@Composable
private fun SettingsIconBadge(
    icon: ImageVector,
    active: Boolean,
) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val fillColor = if (active) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val borderColor = if (active) {
        MaterialTheme.colorScheme.primary.copy(alpha = ACTIVE_CARD_BORDER_ALPHA)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val tintColor = if (active) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = fillColor,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = Modifier.size(sizing.appBadgeSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor,
                modifier = Modifier.size(sizing.iconMd),
            )
        }
    }
}

@Composable
private fun SettingsToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            uncheckedBorderColor = MaterialTheme.colorScheme.outline,
        ),
    )
}

@Composable
private fun BackupRestoreSection(
    supportsNativeFilePicker: Boolean,
    onExportJson: () -> Unit,
    onPickJsonFile: () -> Unit,
    onImportJsonRaw: (String) -> Unit,
    onGetCurrentJson: () -> String,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current
    var showPasteImport by remember { mutableStateOf(false) }
    var copiedNotice by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
        Text(
            text = "BACKUP & RESTORE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            SecondaryTonalButton(onClick = onExportJson, modifier = Modifier.weight(1f)) {
                Icon(DroidQuestIcons.Download, contentDescription = null, modifier = Modifier.size(sizing.iconMd))
                Text("Export JSON", style = MaterialTheme.typography.labelLarge)
            }
            SecondaryTonalButton(
                onClick = {
                    if (supportsNativeFilePicker) onPickJsonFile() else showPasteImport = !showPasteImport
                },
                modifier = Modifier.weight(1f),
            ) {
                Icon(DroidQuestIcons.Upload, contentDescription = null, modifier = Modifier.size(sizing.iconMd))
                Text("Import JSON", style = MaterialTheme.typography.labelLarge)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val copyColor = if (copiedNotice) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = if (copiedNotice) "Copied to clipboard" else "Copy JSON",
                style = MaterialTheme.typography.bodySmall,
                color = copyColor,
                modifier = Modifier.clickable {
                    clipboard.setText(AnnotatedString(onGetCurrentJson()))
                    copiedNotice = true
                },
            )
            Text("·", color = MaterialTheme.colorScheme.outline)
            Text(
                text = if (showPasteImport) "Hide Paste Box" else "Paste JSON Manually",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { showPasteImport = !showPasteImport },
            )
        }

        AnimatedVisibility(visible = showPasteImport) {
            PasteJsonImportBox(onImportJsonRaw = onImportJsonRaw)
        }
    }
}

@Composable
private fun PasteJsonImportBox(onImportJsonRaw: (String) -> Unit) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    var pastedJson by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        OutlinedTextField(
            value = pastedJson,
            onValueChange = { pastedJson = it },
            placeholder = { Text("Paste contents of progress.json...") },
            colors = questTextFieldColors(),
            shape = DroidQuestCornerShapes.QuestCard,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = sizing.pasteBoxMinHeight, max = sizing.pasteBoxMaxHeight),
        )
        PrimaryActionButton(
            onClick = { onImportJsonRaw(pastedJson) },
            contentPadding = PaddingValues(horizontal = spacing.cardPaddingCompact, vertical = spacing.base),
        ) {
            Text("Restore JSON", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun DangerZoneResetSection(onReset: () -> Unit) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    var confirmReset by remember { mutableStateOf(false) }

    if (!confirmReset) {
        Row(
            modifier = Modifier
                .clip(DroidQuestCornerShapes.TactileButton)
                .clickable { confirmReset = true }
                .padding(vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Icon(
                imageVector = DroidQuestIcons.RotateCcw,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(sizing.iconMd),
            )
            Text(
                text = "Reset all roadmap progress",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.error,
            )
        }
    } else {
        DangerZoneConfirmCard(
            onCancel = { confirmReset = false },
            onConfirm = {
                confirmReset = false
                onReset()
            },
        )
    }
}

@Composable
private fun DangerZoneConfirmCard(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.error.copy(alpha = ERROR_BORDER_ALPHA)),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Reset all checkboxes & notes?",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.md)) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = DroidQuestCornerShapes.TactileButton,
                    modifier = Modifier
                        .clip(DroidQuestCornerShapes.TactileButton)
                        .clickable(onClick = onCancel),
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = spacing.base, vertical = spacing.sm),
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.error,
                    shape = DroidQuestCornerShapes.TactileButton,
                    modifier = Modifier
                        .clip(DroidQuestCornerShapes.TactileButton)
                        .clickable(onClick = onConfirm),
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.padding(horizontal = spacing.base, vertical = spacing.sm),
                    )
                }
            }
        }
    }
}
