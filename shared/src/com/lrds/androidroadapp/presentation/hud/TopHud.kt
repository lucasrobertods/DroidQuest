package com.lrds.androidroadapp.presentation.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.model.RankInfo
import com.lrds.androidroadapp.domain.model.SaveStatus
import com.lrds.androidroadapp.presentation.ActiveTab

fun formatXpNumber(xp: Int): String {
    val negative = xp < 0
    val digits = kotlin.math.abs(xp).toString()
    val grouped = buildString {
        digits.forEachIndexed { idx, ch ->
            if (idx > 0 && (digits.length - idx) % 3 == 0) append(',')
            append(ch)
        }
    }
    return if (negative) "-$grouped" else grouped
}

@Composable
fun TopHud(
    totalXp: Int,
    rankInfo: RankInfo,
    streak: Int,
    currentDay: Int,
    targetDays: Int,
    dueReviewCount: Int,
    saveStatus: SaveStatus,
    soundEnabled: Boolean,
    isDarkTheme: Boolean,
    activeTab: ActiveTab,
    showTelemetryStrip: Boolean,
    onSelectTab: (ActiveTab) -> Unit,
    onToggleTheme: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenSettings: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                val isWide = maxWidth >= 820.dp

                Column(
                    modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    // Compact single-row header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(DroidQuestCornerShapes.HudPill)
                                .clickable(onClick = onGoHome)
                                .padding(end = 6.dp),
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = DroidQuestCornerShapes.TactileButton,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.size(34.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = DroidQuestIcons.Terminal,
                                        contentDescription = "DroidQuest",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(17.dp),
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    text = "DroidQuest",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                )
                                Text(
                                    text = "Lv.${rankInfo.current.level} · ${rankInfo.current.title}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        if (isWide) {
                            DesktopTabBar(
                                activeTab = activeTab,
                                dueReviewCount = dueReviewCount,
                                onSelectTab = onSelectTab,
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            HudGhostIconButton(
                                icon = if (isDarkTheme) DroidQuestIcons.Sun else DroidQuestIcons.Moon,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                                onClick = onToggleTheme,
                            )
                            HudGhostIconButton(
                                icon = if (soundEnabled) DroidQuestIcons.Volume2 else DroidQuestIcons.VolumeX,
                                tint = if (soundEnabled) MaterialTheme.colorScheme.onSurfaceVariant else ext.inkSubtle,
                                contentDescription = if (soundEnabled) "Mute effects" else "Enable effects",
                                onClick = onToggleSound,
                            )
                            HudGhostIconButton(
                                icon = DroidQuestIcons.Settings,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = "Settings & Backup",
                                onClick = onOpenSettings,
                            )
                        }
                    }

                    // Unified 3-pill monochrome telemetry strip (shown on Trail / Home)
                    if (showTelemetryStrip) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TelemetryPill(
                                icon = DroidQuestIcons.CalendarClock,
                                label = "Day $currentDay/$targetDays",
                                modifier = Modifier.weight(1f),
                            )
                            TelemetryPill(
                                icon = DroidQuestIcons.Flame,
                                label = "${streak}d streak",
                                modifier = Modifier.weight(1f),
                            )
                            TelemetryPill(
                                icon = DroidQuestIcons.Zap,
                                label = "${formatXpNumber(totalXp)} XP",
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun TelemetryPill(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.HudPill,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = label,
                style = DroidQuestTheme.mono.badge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun DesktopTabBar(
    activeTab: ActiveTab,
    dueReviewCount: Int,
    onSelectTab: (ActiveTab) -> Unit,
) {
    val tabs = listOf(
        ActiveTab.TRAIL to "Trail",
        ActiveTab.CHECKLIST to "Checklist",
        ActiveTab.REVIEWS to if (dueReviewCount > 0) "Reviews ($dueReviewCount)" else "Reviews",
        ActiveTab.TROPHIES to "Trophies",
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.FullPill,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { (tab, label) ->
                val selected = activeTab == tab
                Box(
                    modifier = Modifier
                        .clip(DroidQuestCornerShapes.FullPill)
                        .background(
                            if (selected) MaterialTheme.colorScheme.surfaceContainerHighest
                            else androidx.compose.ui.graphics.Color.Transparent
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
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

@Composable
private fun HudGhostIconButton(
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
    }
}
