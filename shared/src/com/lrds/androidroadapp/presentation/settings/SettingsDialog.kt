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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.components.SecondaryTonalButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.presentation.quest.questTextFieldColors

private val REMINDER_HOUR_PRESETS = listOf(9, 13, 19, 21)

@Composable
fun SettingsDialog(
    startDate: String,
    themeMode: ThemeMode,
    saveStatus: SaveStatus,
    storageLabel: String,
    dailyReminderEnabled: Boolean,
    reminderHour: Int,
    supportsDailyReminders: Boolean,
    supportsNativeFilePicker: Boolean,
    onClose: () -> Unit,
    onSetThemeMode: (ThemeMode) -> Unit,
    onUpdateStartDate: (String) -> Unit,
    onSetDailyReminder: (Boolean, Int) -> Unit,
    onExportJson: () -> Unit,
    onPickJsonFile: () -> Unit,
    onImportJsonRaw: (String) -> Unit,
    onGetCurrentJson: () -> String,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
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

                    SprintStartDateSection(
                        startDate = startDate,
                        onUpdateStartDate = onUpdateStartDate,
                    )

                    LocalPersistenceCard(
                        storageLabel = storageLabel,
                        saveStatus = saveStatus,
                    )

                    if (supportsDailyReminders) {
                        DailyReminderCard(
                            dailyReminderEnabled = dailyReminderEnabled,
                            reminderHour = reminderHour,
                            onSetDailyReminder = onSetDailyReminder,
                        )
                    }

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
        Text(
            text = "Settings & Backup",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
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
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
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
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Icon(
                    imageVector = DroidQuestIcons.HardDrive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(sizing.iconMd),
                )
                Text(
                    text = "$storageLabel · ${saveStatus.name.lowercase().replace('_', ' ')}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text = "Offline-first storage compatible with DroidQuest Web progress.json backups.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
            verticalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Daily Review Reminder",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Local notification at ${reminderHour.toString().padStart(2, '0')}:00",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
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
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                REMINDER_HOUR_PRESETS.forEach { hour ->
                    ReminderHourChip(
                        hour = hour,
                        selected = reminderHour == hour,
                        onClick = { onSetDailyReminder(true, hour) },
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
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val bgColor = if (selected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bgColor,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = Modifier
            .clip(DroidQuestCornerShapes.MicroBadge)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = "${hour.toString().padStart(2, '0')}:00",
            style = DroidQuestTheme.mono.micro,
            color = textColor,
            modifier = Modifier.padding(horizontal = spacing.base, vertical = spacing.sm),
        )
    }
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
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
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
