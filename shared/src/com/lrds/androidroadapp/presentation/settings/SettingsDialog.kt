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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.components.SecondaryTonalButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.StudySchedule
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.presentation.quest.questTextFieldColors

@Composable
fun SettingsDialog(
    startDate: String,
    themeMode: ThemeMode,
    saveStatus: SaveStatus,
    storageLabel: String,
    dailyReminderEnabled: Boolean,
    reminderHour: Int,
    studyReminderEnabled: Boolean,
    studyReminderDays: Set<Int>,
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
) {
    @Suppress("DEPRECATION")
    val clipboard = LocalClipboardManager.current

    var startDateInput by remember(startDate) { mutableStateOf(startDate) }
    var showPasteImport by remember { mutableStateOf(false) }
    var pastedJson by remember { mutableStateOf("") }
    var copiedNotice by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
            .clickable(onClick = onClose)
            .padding(16.dp)
            .imePadding(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = DroidQuestCornerShapes.Dialog,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 24.dp,
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .clickable(enabled = false) {},
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onClose),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = DroidQuestIcons.Close,
                                contentDescription = "Close settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(
                    modifier = Modifier
                        .heightIn(max = 600.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    // 0. Appearance (System / Light / Dark)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "APPEARANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            listOf(
                                ThemeMode.SYSTEM to "System",
                                ThemeMode.LIGHT to "Light",
                                ThemeMode.DARK to "Dark",
                            ).forEach { (mode, label) ->
                                val selected = themeMode == mode
                                Surface(
                                    color = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
                                    shape = DroidQuestCornerShapes.TactileButton,
                                    border = BorderStroke(
                                        1.dp,
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                                        else MaterialTheme.colorScheme.outlineVariant,
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(DroidQuestCornerShapes.TactileButton)
                                        .clickable { onSetThemeMode(mode) },
                                ) {
                                    Box(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center,
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 1. 100-Day Start Date
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "100-DAY SPRINT START DATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                            ) {
                                Text("Apply", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }

                    // 2. Notifications: Study Schedule + Daily Review Reminder
                    if (supportsDailyReminders) {
                        val anyNotificationActive = studyReminderEnabled || dailyReminderEnabled
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                if (anyNotificationActive) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
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

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = DroidQuestCornerShapes.QuestCard,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            SettingsIconBadge(
                                                icon = DroidQuestIcons.Bell,
                                                active = dailyReminderEnabled,
                                            )
                                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
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
                                        Switch(
                                            checked = dailyReminderEnabled,
                                            onCheckedChange = { checked -> onSetDailyReminder(checked, reminderHour) },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                                uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                                            ),
                                        )
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        listOf(9, 13, 19, 21).forEach { hour ->
                                            val selected = reminderHour == hour
                                            Surface(
                                                color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                                                shape = DroidQuestCornerShapes.MicroBadge,
                                                border = BorderStroke(
                                                    1.dp,
                                                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outlineVariant,
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(DroidQuestCornerShapes.MicroBadge)
                                                    .clickable { onSetDailyReminder(true, hour) },
                                            ) {
                                                Box(
                                                    modifier = Modifier.padding(vertical = 6.dp),
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Text(
                                                        text = "${hour.toString().padStart(2, '0')}:00",
                                                        style = DroidQuestTheme.mono.micro,
                                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Local Persistence Info Card
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = DroidQuestCornerShapes.QuestCard,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            SettingsIconBadge(
                                icon = DroidQuestIcons.HardDrive,
                                active = true,
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = storageLabel,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
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

                    // 4. JSON Export / Import
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "BACKUP & RESTORE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            SecondaryTonalButton(
                                onClick = onExportJson,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(DroidQuestIcons.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                                Text("Export JSON", style = MaterialTheme.typography.labelLarge)
                            }

                            SecondaryTonalButton(
                                onClick = {
                                    if (supportsNativeFilePicker) {
                                        onPickJsonFile()
                                    } else {
                                        showPasteImport = !showPasteImport
                                    }
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(DroidQuestIcons.Upload, contentDescription = null, modifier = Modifier.size(15.dp))
                                Text("Import JSON", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (copiedNotice) "Copied to clipboard" else "Copy JSON",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (copiedNotice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
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
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = pastedJson,
                                    onValueChange = { pastedJson = it },
                                    placeholder = { Text("Paste contents of progress.json...") },
                                    colors = questTextFieldColors(),
                                    shape = DroidQuestCornerShapes.QuestCard,
                                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp, max = 160.dp),
                                )
                                PrimaryActionButton(
                                    onClick = { onImportJsonRaw(pastedJson) },
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                                ) {
                                    Text("Restore JSON", style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // 5. Danger Zone: 2-Step Reset
                    if (!confirmReset) {
                        Row(
                            modifier = Modifier
                                .clip(DroidQuestCornerShapes.TactileButton)
                                .clickable { confirmReset = true }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = DroidQuestIcons.RotateCcw,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(15.dp),
                            )
                            Text(
                                text = "Reset all roadmap progress",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = DroidQuestCornerShapes.QuestCard,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Reset all checkboxes & notes?",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f),
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                        shape = DroidQuestCornerShapes.TactileButton,
                                        modifier = Modifier
                                            .clip(DroidQuestCornerShapes.TactileButton)
                                            .clickable { confirmReset = false },
                                    ) {
                                        Text(
                                            text = "Cancel",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.error,
                                        shape = DroidQuestCornerShapes.TactileButton,
                                        modifier = Modifier
                                            .clip(DroidQuestCornerShapes.TactileButton)
                                            .clickable {
                                                confirmReset = false
                                                onReset()
                                            },
                                    ) {
                                        Text(
                                            text = "Reset",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsIconBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
) {
    Surface(
        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(
            1.dp,
            if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier.size(34.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun StudyScheduleCard(
    enabled: Boolean,
    daysOfWeek: Set<Int>,
    hour: Int,
    minute: Int,
    onUpdate: (enabled: Boolean, daysOfWeek: Set<Int>, hour: Int, minute: Int) -> Unit,
) {
    val normalizedDays = StudySchedule.normalizeDays(daysOfWeek)
    val formattedTime = StudySchedule.formatTime(hour, minute)
    val subtitle = when {
        normalizedDays.isEmpty() -> "Choose weekdays and study time"
        enabled -> "${StudySchedule.formatDaysSummary(normalizedDays)} at $formattedTime · Opens Next Quest"
        else -> "Paused · ${StudySchedule.formatDaysSummary(normalizedDays)} at $formattedTime"
    }
    val daysPerWeekLabel = "${normalizedDays.size} ${if (normalizedDays.size == 1) "DAY" else "DAYS"} / WK"

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(
            1.dp,
            if (enabled) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.outlineVariant,
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Header row + toggle switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    SettingsIconBadge(
                        icon = DroidQuestIcons.CalendarClock,
                        active = enabled,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
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
                Switch(
                    checked = enabled,
                    onCheckedChange = { checked ->
                        onUpdate(checked, normalizedDays, hour, minute)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                )
            }

            // Target Days label + weekday pills: M T W T F S S
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                            selected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
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
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(DroidQuestCornerShapes.MicroBadge)
                                .semantics {
                                    contentDescription = "${StudySchedule.dayShortName(isoDay)} study reminder"
                                }
                                .clickable {
                                    val nextDays = if (selected) normalizedDays - isoDay else normalizedDays + isoDay
                                    onUpdate(enabled, nextDays, hour, minute)
                                },
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

            // Time selector (grouped hour & minute steppers in JetBrains Mono)
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = DroidQuestCornerShapes.TactileButton,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = DroidQuestIcons.Clock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Study time",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        TimeStepperGroup(
                            valueText = StudySchedule.clampHour(hour).toString().padStart(2, '0'),
                            decreaseDescription = "Decrease study hour",
                            increaseDescription = "Increase study hour",
                            onDecrease = { onUpdate(enabled, normalizedDays, (hour + 23) % 24, minute) },
                            onIncrease = { onUpdate(enabled, normalizedDays, (hour + 1) % 24, minute) },
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
                            onDecrease = { onUpdate(enabled, normalizedDays, hour, (minute + 55) % 60) },
                            onIncrease = { onUpdate(enabled, normalizedDays, hour, (minute + 5) % 60) },
                        )
                    }
                }
            }

            // Quick time presets (evenly distributed across full card width)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Presets:",
                    style = DroidQuestTheme.mono.micro,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                listOf(8 to 0, 12 to 30, 19 to 0, 21 to 0).forEach { (presetHour, presetMinute) ->
                    val selected = hour == presetHour && minute == presetMinute
                    Surface(
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = DroidQuestCornerShapes.MicroBadge,
                        border = BorderStroke(
                            1.dp,
                            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                            else MaterialTheme.colorScheme.outlineVariant,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(DroidQuestCornerShapes.MicroBadge)
                            .clickable {
                                onUpdate(enabled, normalizedDays, presetHour, presetMinute)
                            },
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = StudySchedule.formatTime(presetHour, presetMinute),
                                style = DroidQuestTheme.mono.micro,
                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
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
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(22.dp)
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
                    .size(22.dp)
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
