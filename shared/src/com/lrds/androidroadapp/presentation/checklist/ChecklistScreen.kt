package com.lrds.androidroadapp.presentation.checklist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.ZoneId
import com.lrds.androidroadapp.presentation.ChecklistStatusFilter
import com.lrds.androidroadapp.presentation.quest.questTextFieldColors
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ChecklistContent(
    topics: Map<String, TopicProgress>,
    highYieldOnly: Boolean,
    onToggleHighYield: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    var search by remember { mutableStateOf("") }
    var pillarFilter by remember { mutableStateOf<PillarId?>(null) }
    var statusFilter by remember { mutableStateOf(ChecklistStatusFilter.ALL) }
    val collapsedZones = remember { mutableStateMapOf<ZoneId, Boolean>() }

    val allSubs = remember { RoadmapCatalog.quests.flatMap { it.subtopics } }
    val totalCount = allSubs.size
    val doneTotalCount = allSubs.count { topics[it.id]?.completed == true }
    val todoTotalCount = totalCount - doneTotalCount

    val query = search.trim().lowercase()
    val filteredQuests = remember(search, pillarFilter, statusFilter, highYieldOnly, topics) {
        RoadmapCatalog.quests.filter { quest ->
            if (pillarFilter != null && quest.pillar != pillarFilter) return@filter false
            if (highYieldOnly && !quest.highYield && !quest.isBoss && quest.subtopics.none { it.highYield }) {
                return@filter false
            }

            quest.subtopics.any { sub ->
                val prog = topics[sub.id]
                val done = prog?.completed == true
                val due = SpacedRepetitionEngine.formatReviewStatus(prog).isDue
                if (statusFilter == ChecklistStatusFilter.TODO && done) return@any false
                if (statusFilter == ChecklistStatusFilter.DONE && !done) return@any false
                if (statusFilter == ChecklistStatusFilter.REVIEW && !due) return@any false
                if (query.isEmpty()) return@any true
                quest.title.lowercase().contains(query) ||
                    quest.subtitle.lowercase().contains(query) ||
                    sub.title.lowercase().contains(query) ||
                    sub.description.lowercase().contains(query) ||
                    (sub.courseTag?.lowercase()?.contains(query) == true)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = sizing.maxContentWidth).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = {
                    Text(
                        text = "Search $totalCount topics...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = DroidQuestIcons.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(sizing.iconLg),
                    )
                },
                trailingIcon = if (search.isNotEmpty()) {
                    {
                        Icon(
                            imageVector = DroidQuestIcons.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(sizing.iconMd)
                                .clickable { search = "" },
                        )
                    }
                } else {
                    null
                },
                singleLine = true,
                colors = questTextFieldColors(),
                shape = DroidQuestCornerShapes.QuestCard,
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val statuses = listOf(
                    ChecklistStatusFilter.ALL to "All ($totalCount)",
                    ChecklistStatusFilter.TODO to "Todo ($todoTotalCount)",
                    ChecklistStatusFilter.DONE to "Done ($doneTotalCount)",
                    ChecklistStatusFilter.REVIEW to "Due Review",
                )
                statuses.forEach { (filter, label) ->
                    MonochromeFilterChip(
                        label = label,
                        selected = statusFilter == filter,
                        onClick = { statusFilter = filter },
                    )
                }
                MonochromeFilterChip(
                    label = "High-Yield",
                    selected = highYieldOnly,
                    onClick = onToggleHighYield,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                QuietPillarTab(
                    label = "All Pillars",
                    selected = pillarFilter == null,
                    onClick = { pillarFilter = null },
                )
                PillarId.entries.forEach { pillar ->
                    val label = when (pillar) {
                        PillarId.ANDROID -> "Android"
                        PillarId.DSA -> "DSA"
                        PillarId.SYSTEM_DESIGN -> "Sys Design"
                        PillarId.CAREER -> "Career"
                    }
                    QuietPillarTab(
                        label = label,
                        selected = pillarFilter == pillar,
                        onClick = { pillarFilter = pillar },
                    )
                }
            }

            RoadmapCatalog.zones.forEach { zone ->
                val questsInZone = filteredQuests.filter { it.zoneId == zone.id }
                if (questsInZone.isEmpty()) return@forEach

                val allZoneSubs = RoadmapCatalog.quests.filter { it.zoneId == zone.id }.flatMap { it.subtopics }
                val doneZoneSubs = allZoneSubs.count { topics[it.id]?.completed == true }
                val zoneFraction = if (allZoneSubs.isNotEmpty()) doneZoneSubs.toFloat() / allZoneSubs.size else 0f
                val collapsed = collapsedZones[zone.id] == true

                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = DroidQuestCornerShapes.QuestCard,
                    border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Column {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { collapsedZones[zone.id] = !collapsed }
                                .padding(horizontal = spacing.cardPadding, vertical = spacing.cardPaddingCompact),
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                                ) {
                                    Text(
                                        text = "$doneZoneSubs/${allZoneSubs.size}",
                                        style = DroidQuestTheme.mono.badge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                    Icon(
                                        imageVector = if (collapsed) DroidQuestIcons.ChevronRight else DroidQuestIcons.ChevronDown,
                                        contentDescription = if (collapsed) "Expand zone" else "Collapse zone",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(sizing.iconMd),
                                    )
                                }
                            }
                            CalmProgressBar(
                                progress = zoneFraction,
                                height = sizing.progressBarThin,
                                minVisibleFraction = if (doneZoneSubs > 0) 0.04f else 0f,
                            )
                        }

                        if (!collapsed) {
                            questsInZone.forEach { quest ->
                                val visibleSubs = quest.subtopics.filter { sub ->
                                    val prog = topics[sub.id]
                                    val isDone = prog?.completed == true
                                    val rev = SpacedRepetitionEngine.formatReviewStatus(prog)
                                    if (statusFilter == ChecklistStatusFilter.TODO && isDone) return@filter false
                                    if (statusFilter == ChecklistStatusFilter.DONE && !isDone) return@filter false
                                    if (statusFilter == ChecklistStatusFilter.REVIEW && !rev.isDue) return@filter false
                                    if (query.isNotEmpty() &&
                                        !quest.title.lowercase().contains(query) &&
                                        !sub.title.lowercase().contains(query) &&
                                        !sub.description.lowercase().contains(query)
                                    ) {
                                        return@filter false
                                    }
                                    true
                                }

                                visibleSubs.forEach { sub ->
                                    val prog = topics[sub.id]
                                    val isDone = prog?.completed == true
                                    val rev = SpacedRepetitionEngine.formatReviewStatus(prog)

                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onToggleTopic(sub.id) }
                                            .padding(horizontal = spacing.cardPadding, vertical = spacing.lg),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(sizing.checkCircleSize)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isDone) MaterialTheme.colorScheme.primaryContainer
                                                    else MaterialTheme.colorScheme.surfaceContainerLowest
                                                )
                                                .border(
                                                    width = borders.medium,
                                                    color = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                    shape = CircleShape,
                                                ),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            if (isDone) {
                                                Icon(
                                                    imageVector = DroidQuestIcons.Check,
                                                    contentDescription = "Done",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(sizing.iconXs),
                                                )
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                                        ) {
                                            Text(
                                                text = sub.title,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                            val meta = buildList {
                                                add("Quest ${quest.order}")
                                                if (sub.highYield) add("High-Yield")
                                                if (isDone && rev.isDue) add(rev.label)
                                            }.joinToString(" · ")
                                            Text(
                                                text = meta,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isDone && rev.isDue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(sizing.ghostButtonSize)
                                                .clip(CircleShape)
                                                .clickable { onSelectQuest(quest.id) },
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Icon(
                                                imageVector = DroidQuestIcons.ChevronRight,
                                                contentDescription = "Open quest details",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(sizing.iconMd),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (filteredQuests.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = DroidQuestCornerShapes.QuestCard,
                    border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(spacing.emptyStatePadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text(
                            text = "No matching topics",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Try clearing your search or switching back to All.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun MonochromeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    Surface(
        color = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.HudPill,
        border = BorderStroke(
            borders.hairline,
            if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier
            .clip(DroidQuestCornerShapes.HudPill)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = spacing.lg, vertical = spacing.sm),
        )
    }
}

@Composable
private fun QuietPillarTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(DroidQuestCornerShapes.MicroBadge)
            .background(
                if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.base, vertical = spacing.sm),
    )
}
