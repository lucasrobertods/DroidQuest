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

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(
                    modifier = Modifier
                        .heightIn(max = sizing.dialogMaxHeight)
                        .verticalScroll(rememberScrollState())
                        .padding(spacing.dialogPadding),
                    verticalArrangement = Arrangement.spacedBy(spacing.section),
                ) {
                    // Appearance (System / Light / Dark)
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
                                        borders.hairline,
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
                                        else MaterialTheme.colorScheme.outlineVariant,
                                    ),
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
                                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 100-Day Start Date
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
                                contentPadding = PaddingValues(horizontal = spacing.cardPadding, vertical = spacing.cardPaddingCompact),
                            ) {
                                Text("Apply", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }

                    // Local Persistence Info Card
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

                    // Daily Review Reminders
                    if (supportsDailyReminders) {
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
                                    listOf(9, 13, 19, 21).forEach { hour ->
                                        val selected = reminderHour == hour
                                        Surface(
                                            color = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLowest,
                                            shape = DroidQuestCornerShapes.MicroBadge,
                                            border = BorderStroke(
                                                borders.hairline,
                                                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outlineVariant,
                                            ),
                                            modifier = Modifier
                                                .clip(DroidQuestCornerShapes.MicroBadge)
                                                .clickable { onSetDailyReminder(true, hour) },
                                        ) {
                                            Text(
                                                text = "${hour.toString().padStart(2, '0')}:00",
                                                style = DroidQuestTheme.mono.micro,
                                                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = spacing.base, vertical = spacing.sm),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // JSON Export / Import
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
                            SecondaryTonalButton(
                                onClick = onExportJson,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(DroidQuestIcons.Download, contentDescription = null, modifier = Modifier.size(sizing.iconMd))
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
                                Icon(DroidQuestIcons.Upload, contentDescription = null, modifier = Modifier.size(sizing.iconMd))
                                Text("Import JSON", style = MaterialTheme.typography.labelLarge)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
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
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Danger Zone: 2-Step Reset
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
                                            .clickable { confirmReset = false },
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
                                            .clickable {
                                                confirmReset = false
                                                onReset()
                                            },
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

                    Spacer(Modifier.height(spacing.xxs))
                }
            }
        }
    }
}
