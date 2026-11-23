package com.lrds.androidroadapp.presentation.trail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.ZoneId
import com.lrds.androidroadapp.presentation.StableMap
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

private const val HIGH_YIELD_SWITCH_SCALE = 0.75f
private const val PERCENT_SCALE = 100.0

@Composable
fun ZoneTeleportBar(
    topics: StableMap<String, TopicProgress>,
    selectedZoneId: ZoneId?,
    highYieldOnly: Boolean,
    onToggleHighYield: () -> Unit,
    onTeleportToZone: (ZoneId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal, vertical = spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = sizing.maxContentWidth).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            ZoneTeleportHeaderRow(
                highYieldOnly = highYieldOnly,
                onToggleHighYield = onToggleHighYield,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoadmapCatalog.zones.forEach { zone ->
                    val zoneSubs = RoadmapCatalog.quests
                        .filter { it.zoneId == zone.id }
                        .flatMap { it.subtopics }
                    val doneSubs = zoneSubs.count { topics[it.id]?.completed == true }
                    val pct = if (zoneSubs.isNotEmpty()) {
                        ((doneSubs.toDouble() / zoneSubs.size) * PERCENT_SCALE).roundToInt()
                    } else {
                        0
                    }
                    val isSelected = selectedZoneId == zone.id || (selectedZoneId == null && zone.number == 1)
                    ZoneTeleportPill(
                        zone = zone,
                        pct = pct,
                        isSelected = isSelected,
                        onClick = { onTeleportToZone(zone.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ZoneTeleportHeaderRow(
    highYieldOnly: Boolean,
    onToggleHighYield: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "ZONES",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier
                .clip(DroidQuestCornerShapes.FullPill)
                .clickable(onClick = onToggleHighYield)
                .padding(start = spacing.sm),
        ) {
            val labelColor = if (highYieldOnly) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = "High-Yield",
                style = MaterialTheme.typography.bodySmall,
                color = labelColor,
            )
            Switch(
                checked = highYieldOnly,
                onCheckedChange = { onToggleHighYield() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                modifier = Modifier.scale(HIGH_YIELD_SWITCH_SCALE).height(sizing.trailRailWidth),
            )
        }
    }
}

@Composable
private fun ZoneTeleportPill(
    zone: AdventureZone,
    pct: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val shortWord = zone.shortTitle.substringBefore(" ")
    val surfaceColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val titleColor = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    val pctColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = surfaceColor,
        shape = DroidQuestCornerShapes.HudPill,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = Modifier
            .clip(DroidQuestCornerShapes.HudPill)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            Text(
                text = "${zone.number} $shortWord",
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
            )
            Text(
                text = "$pct%",
                style = DroidQuestTheme.mono.badge,
                color = pctColor,
            )
        }
    }
}

@Composable
fun ZoneBannerHeader(
    zone: AdventureZone,
    doneZoneSubs: Int,
    totalZoneSubs: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val zonePct = if (totalZoneSubs > 0) doneZoneSubs.toFloat() / totalZoneSubs.toFloat() else 0f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = DroidQuestCornerShapes.QuestCard,
            border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .widthIn(max = sizing.maxContentWidth)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = spacing.cardPadding, vertical = spacing.cardPaddingCompact),
                verticalArrangement = Arrangement.spacedBy(spacing.base),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Zone ${zone.number} · ${zone.shortTitle}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = spacing.md),
                    )
                    Text(
                        text = "$doneZoneSubs/$totalZoneSubs",
                        style = DroidQuestTheme.mono.badge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                CalmProgressBar(
                    progress = zonePct,
                    height = sizing.progressBarMedium,
                    minVisibleFraction = if (doneZoneSubs > 0) 0.04f else 0f,
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
fun TrailQuestNodeRow(
    quest: QuestModule,
    indexInZone: Int,
    topics: StableMap<String, TopicProgress>,
    isRecommended: Boolean,
    isLastInZone: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val totalSubs = quest.subtopics.size
    val completedSubs = quest.subtopics.count { topics[it.id]?.completed == true }
    val isCompleted = totalSubs > 0 && completedSubs == totalSubs
    val isHighlighted = isRecommended || (completedSubs > 0 && !isCompleted)
    val dueCount = quest.subtopics.count { SpacedRepetitionEngine.formatReviewStatus(topics[it.id]).isDue }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = sizing.maxContentWidth)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TrailTimelineRailNode(
                isFirstInZone = indexInZone == 0,
                isLastInZone = isLastInZone,
                isCompleted = isCompleted,
                isHighlighted = isHighlighted,
                isBoss = quest.isBoss,
            )

            TrailQuestCard(
                quest = quest,
                completedSubs = completedSubs,
                totalSubs = totalSubs,
                dueCount = dueCount,
                isCompleted = isCompleted,
                isHighlighted = isHighlighted,
                onSelect = onSelect,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun TrailTimelineRailNode(
    isFirstInZone: Boolean,
    isLastInZone: Boolean,
    isCompleted: Boolean,
    isHighlighted: Boolean,
    isBoss: Boolean,
) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val railColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val railStrokeWidth = borders.medium

    Box(
        modifier = Modifier
            .width(sizing.trailRailWidth)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxHeight().width(borders.emphasis)) {
            val topY = if (isFirstInZone) size.height / 2f else 0f
            val bottomY = if (isLastInZone) size.height / 2f else size.height
            if (bottomY > topY) {
                drawLine(
                    color = railColor,
                    start = Offset(size.width / 2f, topY),
                    end = Offset(size.width / 2f, bottomY),
                    strokeWidth = railStrokeWidth.toPx(),
                )
            }
        }

        when {
            isCompleted -> TrailCompletedNode(primaryColor = primaryColor)
            isHighlighted -> TrailHighlightedNode(primaryColor = primaryColor)
            isBoss -> TrailBossNode()
            else -> Box(
                modifier = Modifier
                    .size(sizing.trailPendingNodeSize)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .border(borders.medium, MaterialTheme.colorScheme.outline, CircleShape),
            )
        }
    }
}

@Composable
private fun TrailCompletedNode(primaryColor: Color) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Box(
        modifier = Modifier
            .size(sizing.trailNodeSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(borders.medium, primaryColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = DroidQuestIcons.Check,
            contentDescription = "Completed",
            tint = primaryColor,
            modifier = Modifier.size(sizing.iconXs),
        )
    }
}

@Composable
private fun TrailHighlightedNode(primaryColor: Color) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Box(
        modifier = Modifier
            .size(sizing.trailNodeSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .border(borders.emphasis, primaryColor, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(sizing.trailActiveDotSize)
                .clip(CircleShape)
                .background(primaryColor),
        )
    }
}

@Composable
private fun TrailBossNode() {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Box(
        modifier = Modifier
            .size(sizing.trailNodeSize)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(borders.hairline, MaterialTheme.colorScheme.outline, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = DroidQuestIcons.Lock,
            contentDescription = "Boss Challenge",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(sizing.iconXs),
        )
    }
}

@Composable
private fun TrailQuestCard(
    quest: QuestModule,
    completedSubs: Int,
    totalSubs: Int,
    dueCount: Int,
    isCompleted: Boolean,
    isHighlighted: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val primaryColor = MaterialTheme.colorScheme.primary
    val pctFraction = if (totalSubs > 0) completedSubs.toFloat() / totalSubs else 0f
    val cardColor = if (isHighlighted) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val borderColor = if (isHighlighted) primaryColor.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant
    val countColor = if (isHighlighted || isCompleted) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = cardColor,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = modifier
            .padding(vertical = spacing.xs)
            .clip(DroidQuestCornerShapes.QuestCard)
            .clickable(onClick = onSelect),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = spacing.cardPadding, vertical = spacing.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TrailQuestTitleColumn(
                    quest = quest,
                    dueCount = dueCount,
                    isCompleted = isCompleted,
                    isHighlighted = isHighlighted,
                    modifier = Modifier.weight(1f).padding(end = spacing.base),
                )

                Text(
                    text = "$completedSubs/$totalSubs",
                    style = DroidQuestTheme.mono.badge,
                    color = countColor,
                )
            }

            if (isHighlighted && totalSubs > 0) {
                CalmProgressBar(
                    progress = pctFraction,
                    height = sizing.progressBarThin,
                    minVisibleFraction = if (completedSubs > 0) 0.05f else 0f,
                )
            }
        }
    }
}

@Composable
private fun TrailQuestTitleColumn(
    quest: QuestModule,
    dueCount: Int,
    isCompleted: Boolean,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val titleColor = when {
        isHighlighted -> MaterialTheme.colorScheme.onSurface
        isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> DroidQuestTheme.colors.inkSecondary
    }
    val metaParts = buildList {
        if (quest.isBoss) {
            add("Boss Challenge")
        } else if (quest.highYield) {
            add("High-Yield")
        }
        if (dueCount > 0) add("$dueCount due")
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.xxs),
    ) {
        Text(
            text = quest.title,
            style = MaterialTheme.typography.titleMedium,
            color = titleColor,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (metaParts.isNotEmpty() && !isCompleted) {
            Text(
                text = metaParts.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
