package com.lrds.androidroadapp.presentation.hud

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
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
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.statusBarsPadding()) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.screenHorizontal, vertical = spacing.base),
                contentAlignment = Alignment.Center,
            ) {
                val isWide = maxWidth >= sizing.wideBreakpoint

                Column(
                    modifier = Modifier.widthIn(max = sizing.maxContentWidth).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(spacing.lg),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.base),
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .clip(DroidQuestCornerShapes.HudPill)
                                .clickable(onClick = onGoHome)
                                .padding(end = spacing.sm),
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = DroidQuestCornerShapes.TactileButton,
                                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.size(sizing.appBadgeSize),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = DroidQuestIcons.Terminal,
                                        contentDescription = "DroidQuest",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(sizing.iconMd),
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
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
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
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

                    val motion = DroidQuestTheme.motion
                    AnimatedVisibility(
                        visible = showTelemetryStrip,
                        enter = fadeIn(tween(motion.fastMs)) + expandVertically(tween(motion.standardMs)),
                        exit = fadeOut(tween(motion.fastMs)) + shrinkVertically(tween(motion.standardMs)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.md),
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
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.HudPill,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.base, vertical = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(sizing.iconSm),
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
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val tabs = listOf(
        ActiveTab.TRAIL to "Trail",
        ActiveTab.CHECKLIST to "Checklist",
        ActiveTab.REVIEWS to if (dueReviewCount > 0) "Reviews ($dueReviewCount)" else "Reviews",
        ActiveTab.TROPHIES to "Trophies",
    )

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.FullPill,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier.padding(spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { (tab, label) ->
                val selected = activeTab == tab
                Box(
                    modifier = Modifier
                        .clip(DroidQuestCornerShapes.FullPill)
                        .background(
                            if (selected) MaterialTheme.colorScheme.surfaceContainerHighest
                            else Color.Transparent
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(horizontal = spacing.lg, vertical = spacing.sm),
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
    tint: Color,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val sizing = DroidQuestTheme.sizing
    Box(
        modifier = Modifier
            .size(sizing.ghostButtonSize)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(sizing.iconLg),
        )
    }
}
