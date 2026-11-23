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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
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
    val bossQuests = RoadmapCatalog.quests.filter { it.isBoss }
    val activeDaysCount = heatmapDays.count { it.score > 0 }
    val unlockedBossCount = bossQuests.count { boss ->
        boss.subtopics.isNotEmpty() && boss.subtopics.all { topics[it.id]?.completed == true }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        val isWide = maxWidth >= 820.dp
        val isMedium = maxWidth >= 560.dp

        Column(
            modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // 1. 3 Equal Monochrome Key Stat Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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

            // 2. 28-Day Study Consistency Heatmap (Monochrome + Emerald Only)
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.ZoneBanner,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
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
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        heatmapDays.chunked(cols).forEach { week ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
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
                                        modifier = Modifier.weight(1f).height(34.dp),
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
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
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

            // 3. Boss Trophies List (Clean Monochrome + Emerald Unlocked State)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "BOSS TROPHIES · $unlockedBossCount/${bossQuests.size} UNLOCKED",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    bossQuests.forEach { boss ->
                        val zone = RoadmapCatalog.getZoneById(boss.zoneId)
                        val doneSubs = boss.subtopics.count { topics[it.id]?.completed == true }
                        val totalSubs = boss.subtopics.size
                        val defeated = totalSubs > 0 && doneSubs == totalSubs

                        Surface(
                            color = if (defeated) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
                            shape = DroidQuestCornerShapes.QuestCard,
                            border = BorderStroke(
                                1.dp,
                                if (defeated) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(DroidQuestCornerShapes.QuestCard)
                                .clickable { onSelectQuest(boss.id) },
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Surface(
                                    color = if (defeated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = DroidQuestCornerShapes.TactileButton,
                                    border = BorderStroke(
                                        1.dp,
                                        if (defeated) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                                    ),
                                    modifier = Modifier.size(42.dp),
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (defeated) DroidQuestIcons.Trophy else DroidQuestIcons.Lock,
                                            contentDescription = null,
                                            tint = if (defeated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
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
                                        1.dp,
                                        if (defeated) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                                    ),
                                ) {
                                    Text(
                                        text = if (defeated) "UNLOCKED" else "$doneSubs/$totalSubs",
                                        style = DroidQuestTheme.mono.micro,
                                        color = if (defeated) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Zone Mastery Progress Summary
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                )
                                Text(
                                    text = "$done/${subs.size} ($pct%)",
                                    style = DroidQuestTheme.mono.micro,
                                    color = if (pct == 100) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            CalmProgressBar(
                                progress = pct / 100f,
                                height = 4.dp,
                                minVisibleFraction = if (done > 0) 0.04f else 0f,
                            )
                        }
                    }
                }
            }

            // 5. Senior Engineer Rank Ladder
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = "RANK LADDER",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val rankCols = when {
                        isWide -> 4
                        isMedium -> 2
                        else -> 2
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoadmapCatalog.rankTiers.chunked(rankCols).forEach { rowTiers ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                rowTiers.forEach { tier ->
                                    val reached = totalXp >= tier.minXp
                                    Surface(
                                        color = if (reached) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLowest,
                                        shape = DroidQuestCornerShapes.TactileButton,
                                        border = BorderStroke(
                                            1.dp,
                                            if (reached) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outlineVariant,
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .alpha(if (reached) 1f else 0.55f),
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                            verticalArrangement = Arrangement.spacedBy(2.dp),
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

            Spacer(Modifier.height(12.dp))
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
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp),
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
    Box(
        modifier = Modifier
            .size(11.dp)
            .clip(DroidQuestCornerShapes.MicroBadge)
            .background(color),
    )
}
