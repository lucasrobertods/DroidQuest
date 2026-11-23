package com.lrds.androidroadapp.presentation.trophies

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.presentation.HeatmapDay
import com.lrds.androidroadapp.presentation.hud.formatXpNumber
import kotlin.math.roundToInt

@Composable
fun TrophiesContent(
    topics: Map<String, TopicProgress>,
    heatmapDays: List<HeatmapDay>,
    totalXp: Int,
    streak: Int,
    totalReviewsCompleted: Int,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val bossQuests = RoadmapCatalog.quests.filter { it.isBoss }
    val activeDaysCount = heatmapDays.count { it.score > 0 }
    val unlockedBossCount = bossQuests.count { boss ->
        boss.subtopics.isNotEmpty() && boss.subtopics.all { topics[it.id]?.completed == true }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        val isWide = maxWidth >= sizing.wideBreakpoint
        val isMedium = maxWidth >= sizing.mediumBreakpoint

        Column(
            modifier = Modifier.widthIn(max = sizing.maxContentWidth).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.section),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.base),
            ) {
                StatTileCard(
                    icon = DroidQuestIcons.Zap,
                    eyebrow = "XP",
                    value = formatXpNumber(totalXp),
                    caption = "Total XP",
                    modifier = Modifier.weight(1f),
                )
                StatTileCard(
                    icon = DroidQuestIcons.Flame,
                    eyebrow = "Streak",
                    value = "${streak}d",
                    caption = "Current Streak",
                    modifier = Modifier.weight(1f),
                )
                StatTileCard(
                    icon = DroidQuestIcons.Brain,
                    eyebrow = "Recall",
                    value = totalReviewsCompleted.toString(),
                    caption = "Reviews Done",
                    modifier = Modifier.weight(1f),
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.ZoneBanner,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.cardPaddingSpacious),
                    verticalArrangement = Arrangement.spacedBy(spacing.xl),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "STUDY CONSISTENCY · LAST 28 DAYS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "$activeDaysCount active days",
                            style = DroidQuestTheme.mono.micro,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    val cols = if (isMedium) 14 else 7
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        heatmapDays.chunked(cols).forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                week.forEach { day ->
                                    val tileColor = when {
                                        day.score == 0 -> ext.heatmapZero
                                        day.score == 1 -> ext.heatmapLow
                                        day.score <= 3 -> ext.heatmapMid
                                        else -> ext.heatmapHigh
                                    }
                                    val textColor = when {
                                        day.score >= 4 -> MaterialTheme.colorScheme.onPrimary
                                        day.score > 0 -> MaterialTheme.colorScheme.onSurface
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }

                                    Surface(
                                        color = tileColor,
                                        shape = DroidQuestCornerShapes.MicroBadge,
                                        modifier = Modifier.weight(1f).height(sizing.heatmapCellHeight),
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = day.dayOfMonth,
                                                style = DroidQuestTheme.mono.micro,
                                                color = textColor,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                        ) {
                            Text(
                                text = "Less",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            HeatmapLegendSwatch(ext.heatmapZero)
                            HeatmapLegendSwatch(ext.heatmapLow)
                            HeatmapLegendSwatch(ext.heatmapMid)
                            HeatmapLegendSwatch(ext.heatmapHigh)
                            Text(
                                text = "More",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
                Text(
                    text = "BOSS TROPHIES · $unlockedBossCount/${bossQuests.size} UNLOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                    bossQuests.forEach { boss ->
                        val zone = RoadmapCatalog.getZoneById(boss.zoneId)
                        val doneSubs = boss.subtopics.count { topics[it.id]?.completed == true }
                        val totalSubs = boss.subtopics.size
                        val defeated = totalSubs > 0 && doneSubs == totalSubs

                        Surface(
                            color = if (defeated) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
                            shape = DroidQuestCornerShapes.QuestCard,
                            border = BorderStroke(
                                borders.hairline,
                                if (defeated) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(DroidQuestCornerShapes.QuestCard)
                                .clickable { onSelectQuest(boss.id) },
                        ) {
                            Row(
                                modifier = Modifier.padding(spacing.cardPaddingCompact),
                                horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    color = if (defeated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = DroidQuestCornerShapes.TactileButton,
                                    border = BorderStroke(
                                        borders.hairline,
                                        if (defeated) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                                    ),
                                    modifier = Modifier.size(sizing.trophyIconBoxSize),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (defeated) DroidQuestIcons.Trophy else DroidQuestIcons.Lock,
                                            contentDescription = null,
                                            tint = if (defeated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(sizing.iconXl),
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                                ) {
                                    Text(
                                        text = zone.badgeTitle,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (defeated) MaterialTheme.colorScheme.onSurface else ext.inkSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = "Zone ${zone.number} · ${boss.title}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }

                                Surface(
                                    color = if (defeated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLowest,
                                    shape = DroidQuestCornerShapes.MicroBadge,
                                    border = BorderStroke(
                                        borders.hairline,
                                        if (defeated) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                                    ),
                                ) {
                                    Text(
                                        text = if (defeated) "UNLOCKED" else "$doneSubs/$totalSubs",
                                        style = DroidQuestTheme.mono.micro,
                                        color = if (defeated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(spacing.lg),
                ) {
                    Text(
                        text = "ZONE MASTERY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    RoadmapCatalog.zones.forEachIndexed { idx, zone ->
                        if (idx > 0) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                        val subs = RoadmapCatalog.quests.filter { it.zoneId == zone.id }.flatMap { it.subtopics }
                        val done = subs.count { topics[it.id]?.completed == true }
                        val pct = if (subs.isNotEmpty()) ((done.toDouble() / subs.size) * 100.0).roundToInt() else 0

                        Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "Zone ${zone.number} · ${zone.shortTitle}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f).padding(end = spacing.md),
                                )
                                Text(
                                    text = "$done/${subs.size} ($pct%)",
                                    style = DroidQuestTheme.mono.micro,
                                    color = if (pct == 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            CalmProgressBar(
                                progress = pct / 100f,
                                height = sizing.progressBarThin,
                                minVisibleFraction = if (done > 0) 0.04f else 0f,
                            )
                        }
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(spacing.cardPadding),
                    verticalArrangement = Arrangement.spacedBy(spacing.base),
                ) {
                    Text(
                        text = "RANK LADDER",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val rankCols = if (isWide) 4 else 2
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                        RoadmapCatalog.rankTiers.chunked(rankCols).forEach { rowTiers ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                            ) {
                                rowTiers.forEach { tier ->
                                    val reached = totalXp >= tier.minXp
                                    Surface(
                                        color = if (reached) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLowest,
                                        shape = DroidQuestCornerShapes.TactileButton,
                                        border = BorderStroke(
                                            borders.hairline,
                                            if (reached) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant,
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .alpha(if (reached) 1f else 0.55f),
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.base),
                                            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = "Lv.${tier.level}",
                                                    style = DroidQuestTheme.mono.micro,
                                                    color = if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Text(
                                                    text = "${formatXpNumber(tier.minXp)} XP",
                                                    style = DroidQuestTheme.mono.micro,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                            Text(
                                                text = tier.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                    }
                                }
                                repeat(rankCols - rowTiers.size) {
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun StatTileCard(
    icon: ImageVector,
    eyebrow: String,
    value: String,
    caption: String,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(spacing.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(sizing.iconSm),
                )
                Text(
                    text = eyebrow,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }
            Text(
                text = value,
                style = DroidQuestTheme.mono.kpiLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun HeatmapLegendSwatch(color: Color) {
    val sizing = DroidQuestTheme.sizing
    Box(
        modifier = Modifier
            .size(sizing.heatmapLegendSwatch)
            .clip(DroidQuestCornerShapes.MicroBadge)
            .background(color),
    )
}
