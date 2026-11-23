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
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.RankTier
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.presentation.HeatmapDay
import com.lrds.androidroadapp.presentation.StableList
import com.lrds.androidroadapp.presentation.StableMap
import com.lrds.androidroadapp.presentation.hud.formatXpNumber
import kotlin.math.roundToInt

private const val HEATMAP_COLS_MEDIUM = 14
private const val HEATMAP_COLS_COMPACT = 7
private const val HEATMAP_MID_MAX_SCORE = 3
private const val HEATMAP_HIGH_MIN_SCORE = 4
private const val PERCENT_SCALE = 100.0
private const val FULL_PERCENT = 100
private const val RANK_COLS_WIDE = 4
private const val RANK_COLS_DEFAULT = 2
private const val LOCKED_RANK_ALPHA = 0.55f

@Composable
fun TrophiesContent(
    topics: StableMap<String, TopicProgress>,
    heatmapDays: StableList<HeatmapDay>,
    totalXp: Int,
    streak: Int,
    totalReviewsCompleted: Int,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

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
            TrophiesStatsRow(
                totalXp = totalXp,
                streak = streak,
                totalReviewsCompleted = totalReviewsCompleted,
            )

            StudyConsistencyHeatmapCard(
                heatmapDays = heatmapDays,
                isMedium = isMedium,
            )

            BossTrophiesSection(
                topics = topics,
                onSelectQuest = onSelectQuest,
            )

            ZoneMasteryCard(topics = topics)

            RankLadderCard(
                totalXp = totalXp,
                isWide = isWide,
            )

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun TrophiesStatsRow(
    totalXp: Int,
    streak: Int,
    totalReviewsCompleted: Int,
) {
    val spacing = DroidQuestTheme.spacing

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
}

@Composable
private fun StudyConsistencyHeatmapCard(
    heatmapDays: StableList<HeatmapDay>,
    isMedium: Boolean,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val activeDaysCount = heatmapDays.count { it.score > 0 }
    val cols = if (isMedium) HEATMAP_COLS_MEDIUM else HEATMAP_COLS_COMPACT

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

            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                heatmapDays.chunked(cols).forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        week.forEach { day ->
                            HeatmapDayTile(day = day, modifier = Modifier.weight(1f))
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
}

@Composable
private fun HeatmapDayTile(
    day: HeatmapDay,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors
    val sizing = DroidQuestTheme.sizing
    val tileColor = when {
        day.score == 0 -> ext.heatmapZero
        day.score == 1 -> ext.heatmapLow
        day.score <= HEATMAP_MID_MAX_SCORE -> ext.heatmapMid
        else -> ext.heatmapHigh
    }
    val textColor = when {
        day.score >= HEATMAP_HIGH_MIN_SCORE -> MaterialTheme.colorScheme.onPrimary
        day.score > 0 -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = tileColor,
        shape = DroidQuestCornerShapes.MicroBadge,
        modifier = modifier.height(sizing.heatmapCellHeight),
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

@Composable
private fun BossTrophiesSection(
    topics: StableMap<String, TopicProgress>,
    onSelectQuest: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val bossQuests = RoadmapCatalog.quests.filter { it.isBoss }
    val unlockedBossCount = bossQuests.count { boss ->
        boss.subtopics.isNotEmpty() && boss.subtopics.all { topics[it.id]?.completed == true }
    }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
        Text(
            text = "BOSS TROPHIES · $unlockedBossCount/${bossQuests.size} UNLOCKED",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            bossQuests.forEach { boss ->
                val doneSubs = boss.subtopics.count { topics[it.id]?.completed == true }
                BossTrophyRow(
                    boss = boss,
                    doneSubs = doneSubs,
                    onClick = { onSelectQuest(boss.id) },
                )
            }
        }
    }
}

@Composable
private fun BossTrophyRow(
    boss: QuestModule,
    doneSubs: Int,
    onClick: () -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val zone = RoadmapCatalog.getZoneById(boss.zoneId)
    val totalSubs = boss.subtopics.size
    val defeated = totalSubs > 0 && doneSubs == totalSubs
    val rowBg = if (defeated) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val rowBorder = if (defeated) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val badgeBorder = if (defeated) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val statusBg = if (defeated) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val accentTint = if (defeated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = rowBg,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, rowBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clip(DroidQuestCornerShapes.QuestCard)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(spacing.cardPaddingCompact),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BossTrophyIcon(defeated = defeated, borderColor = badgeBorder, accentTint = accentTint)

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
                color = statusBg,
                shape = DroidQuestCornerShapes.MicroBadge,
                border = BorderStroke(borders.hairline, badgeBorder),
            ) {
                Text(
                    text = if (defeated) "UNLOCKED" else "$doneSubs/$totalSubs",
                    style = DroidQuestTheme.mono.micro,
                    color = accentTint,
                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                )
            }
        }
    }
}

@Composable
private fun BossTrophyIcon(
    defeated: Boolean,
    borderColor: Color,
    accentTint: Color,
) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val badgeBg = if (defeated) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Surface(
        color = badgeBg,
        shape = DroidQuestCornerShapes.TactileButton,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = Modifier.size(sizing.trophyIconBoxSize),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (defeated) DroidQuestIcons.Trophy else DroidQuestIcons.Lock,
                contentDescription = null,
                tint = accentTint,
                modifier = Modifier.size(sizing.iconXl),
            )
        }
    }
}

@Composable
private fun ZoneMasteryCard(topics: StableMap<String, TopicProgress>) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

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
                val pct = if (subs.isNotEmpty()) {
                    ((done.toDouble() / subs.size) * PERCENT_SCALE).roundToInt()
                } else {
                    0
                }
                val pctColor = if (pct == FULL_PERCENT) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

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
                            color = pctColor,
                        )
                    }
                    CalmProgressBar(
                        progress = pct / FULL_PERCENT.toFloat(),
                        height = sizing.progressBarThin,
                        minVisibleFraction = if (done > 0) 0.04f else 0f,
                    )
                }
            }
        }
    }
}

@Composable
private fun RankLadderCard(
    totalXp: Int,
    isWide: Boolean,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val rankCols = if (isWide) RANK_COLS_WIDE else RANK_COLS_DEFAULT

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
            Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                RoadmapCatalog.rankTiers.chunked(rankCols).forEach { rowTiers ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                    ) {
                        rowTiers.forEach { tier ->
                            RankTierTile(
                                tier = tier,
                                reached = totalXp >= tier.minXp,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(rankCols - rowTiers.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RankTierTile(
    tier: RankTier,
    reached: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val bgColor = if (reached) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val borderColor = if (reached) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val levelColor = if (reached) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bgColor,
        shape = DroidQuestCornerShapes.TactileButton,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = modifier.alpha(if (reached) 1f else LOCKED_RANK_ALPHA),
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
                    color = levelColor,
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
