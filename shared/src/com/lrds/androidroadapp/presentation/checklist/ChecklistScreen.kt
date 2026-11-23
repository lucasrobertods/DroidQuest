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
import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.SubTopic
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.ZoneId
import com.lrds.androidroadapp.presentation.ChecklistStatusFilter
import com.lrds.androidroadapp.presentation.StableList
import com.lrds.androidroadapp.presentation.StableMap
import com.lrds.androidroadapp.presentation.quest.questTextFieldColors
import com.lrds.androidroadapp.presentation.toStableList
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ChecklistContent(
    topics: StableMap<String, TopicProgress>,
    highYieldOnly: Boolean,
    onToggleHighYield: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
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
            matchesQuestFilter(
                quest = quest,
                topics = topics,
                pillarFilter = pillarFilter,
                statusFilter = statusFilter,
                highYieldOnly = highYieldOnly,
                query = query,
            )
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
            ChecklistSearchField(
                search = search,
                totalCount = totalCount,
                onSearchChange = { search = it },
            )

            ChecklistStatusFilterRow(
                statusFilter = statusFilter,
                totalCount = totalCount,
                todoTotalCount = todoTotalCount,
                doneTotalCount = doneTotalCount,
                highYieldOnly = highYieldOnly,
                onSelectStatus = { statusFilter = it },
                onToggleHighYield = onToggleHighYield,
            )

            ChecklistPillarFilterRow(
                pillarFilter = pillarFilter,
                onSelectPillar = { pillarFilter = it },
            )

            RoadmapCatalog.zones.forEach { zone ->
                val questsInZone = filteredQuests.filter { it.zoneId == zone.id }
                if (questsInZone.isEmpty()) return@forEach

                val collapsed = collapsedZones[zone.id] == true
                ChecklistZoneSectionCard(
                    zone = zone,
                    questsInZone = questsInZone.toStableList(),
                    topics = topics,
                    statusFilter = statusFilter,
                    query = query,
                    collapsed = collapsed,
                    onToggleCollapse = { collapsedZones[zone.id] = !collapsed },
                    onToggleTopic = onToggleTopic,
                    onSelectQuest = onSelectQuest,
                )
            }

            if (filteredQuests.isEmpty()) {
                ChecklistEmptyStateCard()
            }

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

private fun QuestModule.isHighYieldRelevant(): Boolean =
    highYield || isBoss || subtopics.any { it.highYield }

@OptIn(ExperimentalTime::class)
private fun matchesStatusFilter(
    statusFilter: ChecklistStatusFilter,
    progress: TopicProgress?,
): Boolean {
    val isDone = progress?.completed == true
    val isDue = SpacedRepetitionEngine.formatReviewStatus(progress).isDue
    return when (statusFilter) {
        ChecklistStatusFilter.ALL -> true
        ChecklistStatusFilter.TODO -> !isDone
        ChecklistStatusFilter.DONE -> isDone
        ChecklistStatusFilter.REVIEW -> isDue
    }
}

private fun matchesSearchQuery(
    quest: QuestModule,
    subtopic: SubTopic,
    query: String,
    includeSubtitleAndTag: Boolean,
): Boolean {
    val baseMatch = query.isEmpty() ||
        quest.title.lowercase().contains(query) ||
        subtopic.title.lowercase().contains(query) ||
        subtopic.description.lowercase().contains(query)
    val extraMatch = includeSubtitleAndTag && (
        quest.subtitle.lowercase().contains(query) ||
            (subtopic.courseTag?.lowercase()?.contains(query) == true)
        )
    return baseMatch || extraMatch
}

@OptIn(ExperimentalTime::class)
private fun matchesQuestFilter(
    quest: QuestModule,
    topics: Map<String, TopicProgress>,
    pillarFilter: PillarId?,
    statusFilter: ChecklistStatusFilter,
    highYieldOnly: Boolean,
    query: String,
): Boolean {
    val pillarMatches = pillarFilter == null || quest.pillar == pillarFilter
    val highYieldMatches = !highYieldOnly || quest.isHighYieldRelevant()
    return pillarMatches && highYieldMatches && quest.subtopics.any { sub ->
        matchesStatusFilter(statusFilter, topics[sub.id]) &&
            matchesSearchQuery(quest, sub, query, includeSubtitleAndTag = true)
    }
}

@Composable
private fun ChecklistSearchField(
    search: String,
    totalCount: Int,
    onSearchChange: (String) -> Unit,
) {
    val sizing = DroidQuestTheme.sizing

    OutlinedTextField(
        value = search,
        onValueChange = onSearchChange,
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
                        .clickable { onSearchChange("") },
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
}

@Composable
private fun ChecklistStatusFilterRow(
    statusFilter: ChecklistStatusFilter,
    totalCount: Int,
    todoTotalCount: Int,
    doneTotalCount: Int,
    highYieldOnly: Boolean,
    onSelectStatus: (ChecklistStatusFilter) -> Unit,
    onToggleHighYield: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val statuses = listOf(
        ChecklistStatusFilter.ALL to "All ($totalCount)",
        ChecklistStatusFilter.TODO to "Todo ($todoTotalCount)",
        ChecklistStatusFilter.DONE to "Done ($doneTotalCount)",
        ChecklistStatusFilter.REVIEW to "Due Review",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        statuses.forEach { (filter, label) ->
            MonochromeFilterChip(
                label = label,
                selected = statusFilter == filter,
                onClick = { onSelectStatus(filter) },
            )
        }
        MonochromeFilterChip(
            label = "High-Yield",
            selected = highYieldOnly,
            onClick = onToggleHighYield,
        )
    }
}

@Composable
private fun ChecklistPillarFilterRow(
    pillarFilter: PillarId?,
    onSelectPillar: (PillarId?) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing

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
            onClick = { onSelectPillar(null) },
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
                onClick = { onSelectPillar(pillar) },
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ChecklistZoneSectionCard(
    zone: AdventureZone,
    questsInZone: StableList<QuestModule>,
    topics: StableMap<String, TopicProgress>,
    statusFilter: ChecklistStatusFilter,
    query: String,
    collapsed: Boolean,
    onToggleCollapse: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onSelectQuest: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val allZoneSubs = RoadmapCatalog.quests.filter { it.zoneId == zone.id }.flatMap { it.subtopics }
    val doneZoneSubs = allZoneSubs.count { topics[it.id]?.completed == true }
    val zoneFraction = if (allZoneSubs.isNotEmpty()) doneZoneSubs.toFloat() / allZoneSubs.size else 0f
    val chevronIcon = if (collapsed) DroidQuestIcons.ChevronRight else DroidQuestIcons.ChevronDown

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleCollapse)
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
                            imageVector = chevronIcon,
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
                        matchesStatusFilter(statusFilter, topics[sub.id]) &&
                            matchesSearchQuery(quest, sub, query, includeSubtitleAndTag = false)
                    }
                    visibleSubs.forEach { sub ->
                        ChecklistSubtopicRow(
                            quest = quest,
                            subtopic = sub,
                            progress = topics[sub.id],
                            onToggleTopic = { onToggleTopic(sub.id) },
                            onSelectQuest = { onSelectQuest(quest.id) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun ChecklistSubtopicRow(
    quest: QuestModule,
    subtopic: SubTopic,
    progress: TopicProgress?,
    onToggleTopic: () -> Unit,
    onSelectQuest: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val isDone = progress?.completed == true
    val rev = SpacedRepetitionEngine.formatReviewStatus(progress)
    val titleColor = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
    val metaColor = if (isDone && rev.isDue) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleTopic)
            .padding(horizontal = spacing.cardPadding, vertical = spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        ChecklistCheckCircle(isDone = isDone)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = subtopic.title,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor,
                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            val meta = buildList {
                add("Quest ${quest.order}")
                if (subtopic.highYield) add("High-Yield")
                if (isDone && rev.isDue) add(rev.label)
            }.joinToString(" · ")
            Text(
                text = meta,
                style = MaterialTheme.typography.bodySmall,
                color = metaColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Box(
            modifier = Modifier
                .size(sizing.ghostButtonSize)
                .clip(CircleShape)
                .clickable(onClick = onSelectQuest),
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

@Composable
private fun ChecklistCheckCircle(isDone: Boolean) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val checkBg = if (isDone) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val checkBorder = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(sizing.checkCircleSize)
            .clip(CircleShape)
            .background(checkBg)
            .border(width = borders.medium, color = checkBorder, shape = CircleShape),
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
}

@Composable
private fun ChecklistEmptyStateCard() {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

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

@Composable
private fun MonochromeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val bgColor = if (selected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val textColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bgColor,
        shape = DroidQuestCornerShapes.HudPill,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = Modifier
            .clip(DroidQuestCornerShapes.HudPill)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
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
    val bgColor = if (selected) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent
    val textColor = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = textColor,
        modifier = Modifier
            .clip(DroidQuestCornerShapes.MicroBadge)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.base, vertical = spacing.sm),
    )
}
