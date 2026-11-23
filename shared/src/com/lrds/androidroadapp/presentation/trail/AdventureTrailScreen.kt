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
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.designsystem.theme.ZoneVisualTokens
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.ZoneId
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@Composable
fun ZoneTeleportBar(
    topics: Map<String, TopicProgress>,
    selectedZoneId: ZoneId?,
    highYieldOnly: Boolean,
    onToggleHighYield: () -> Unit,
    onTeleportToZone: (ZoneId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Single compact row: ZONES label on left + High-Yield switch on right
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
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(DroidQuestCornerShapes.FullPill)
                        .clickable(onClick = onToggleHighYield)
                        .padding(start = 6.dp),
                ) {
                    Text(
                        text = "High-Yield",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (highYieldOnly) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
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
                        modifier = Modifier.scale(0.75f).height(26.dp),
                    )
                }
            }

            // Horizontally scrollable monochrome zone pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RoadmapCatalog.zones.forEach { zone ->
                    val zoneSubs = RoadmapCatalog.quests
                        .filter { it.zoneId == zone.id }
                        .flatMap { it.subtopics }
                    val doneSubs = zoneSubs.count { topics[it.id]?.completed == true }
                    val pct = if (zoneSubs.isNotEmpty()) {
                        ((doneSubs.toDouble() / zoneSubs.size) * 100.0).roundToInt()
                    } else {
                        0
                    }
                    val isSelected = selectedZoneId == zone.id || (selectedZoneId == null && zone.number == 1)
                    val shortWord = zone.shortTitle.substringBefore(" ")

                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
                        shape = DroidQuestCornerShapes.HudPill,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                            else MaterialTheme.colorScheme.outlineVariant,
                        ),
                        modifier = Modifier
                            .clip(DroidQuestCornerShapes.HudPill)
                            .clickable { onTeleportToZone(zone.id) },
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "${zone.number} $shortWord",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = "$pct%",
                                style = DroidQuestTheme.mono.badge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
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
    val zonePct = if (totalZoneSubs > 0) {
        doneZoneSubs.toFloat() / totalZoneSubs.toFloat()
    } else {
        0f
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = DroidQuestCornerShapes.QuestCard,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .widthIn(max = 1040.dp)
                .fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
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
                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                    )
                    Text(
                        text = "$doneZoneSubs/$totalZoneSubs",
                        style = DroidQuestTheme.mono.badge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                CalmProgressBar(
                    progress = zonePct,
                    height = 5.dp,
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
    zoneVisual: ZoneVisualTokens,
    topics: Map<String, TopicProgress>,
    isRecommended: Boolean,
    isLastInZone: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val totalSubs = quest.subtopics.size
    val completedSubs = quest.subtopics.count { topics[it.id]?.completed == true }
    val pctFraction = if (totalSubs > 0) completedSubs.toFloat() / totalSubs else 0f
    val isCompleted = totalSubs > 0 && completedSubs == totalSubs
    val isInProgress = completedSubs > 0 && !isCompleted
    val isHighlighted = isRecommended || isInProgress
    val dueCount = quest.subtopics.count { SpacedRepetitionEngine.formatReviewStatus(topics[it.id]).isDue }

    val railColor = MaterialTheme.colorScheme.outlineVariant
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 1040.dp)
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left Vertical Timeline Rail + Crisp Status Node
            Box(
                modifier = Modifier
                    .width(26.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(modifier = Modifier.fillMaxHeight().width(2.dp)) {
                    val topY = if (indexInZone == 0) size.height / 2f else 0f
                    val bottomY = if (isLastInZone) size.height / 2f else size.height
                    if (bottomY > topY) {
                        drawLine(
                            color = railColor,
                            start = Offset(size.width / 2f, topY),
                            end = Offset(size.width / 2f, bottomY),
                            strokeWidth = 1.5.dp.toPx(),
                        )
                    }
                }

                when {
                    isCompleted -> {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(1.5.dp, primaryColor, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = DroidQuestIcons.Check,
                                contentDescription = "Completed",
                                tint = primaryColor,
                                modifier = Modifier.size(13.dp),
                            )
                        }
                    }

                    isHighlighted -> {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(2.dp, primaryColor, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(primaryColor),
                            )
                        }
                    }

                    quest.isBoss -> {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = DroidQuestIcons.Lock,
                                contentDescription = "Boss Challenge",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(11.dp),
                            )
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        )
                    }
                }
            }

            // Right Concise Monochrome Quest Row Card
            Surface(
                color = if (isHighlighted) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(
                    1.dp,
                    if (isHighlighted) primaryColor.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant,
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 5.dp)
                    .clip(DroidQuestCornerShapes.QuestCard)
                    .clickable(onClick = onSelect),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).padding(end = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = quest.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = when {
                                    isHighlighted -> MaterialTheme.colorScheme.onSurface
                                    isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> DroidQuestTheme.colors.inkSecondary
                                },
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )

                            val metaParts = buildList {
                                if (quest.isBoss) add("Boss Challenge")
                                else if (quest.highYield) add("High-Yield")
                                if (dueCount > 0) add("$dueCount due")
                            }
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

                        Text(
                            text = "$completedSubs/$totalSubs",
                            style = DroidQuestTheme.mono.badge,
                            color = if (isHighlighted || isCompleted) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (isHighlighted && totalSubs > 0) {
                        CalmProgressBar(
                            progress = pctFraction,
                            height = 4.dp,
                            minVisibleFraction = if (completedSubs > 0) 0.05f else 0f,
                        )
                    }
                }
            }
        }
    }
}
