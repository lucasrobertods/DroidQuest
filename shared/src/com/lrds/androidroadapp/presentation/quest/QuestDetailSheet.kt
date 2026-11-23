package com.lrds.androidroadapp.presentation.quest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.CustomLink
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.TopicProgress
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalLayoutApi::class, ExperimentalTime::class)
@Composable
fun QuestDetailSheet(
    quest: QuestModule,
    topics: Map<String, TopicProgress>,
    note: String,
    customLinks: List<CustomLink>,
    onClose: () -> Unit,
    onToggleTopic: (String) -> Unit,
    onRecordReview: (String, ReviewRating) -> Unit,
    onToggleFlagReview: (String) -> Unit,
    onUpdateNote: (String, String) -> Unit,
    onAddCustomLink: (String, String, String, ResourceType) -> Unit,
    onRemoveCustomLink: (String, String) -> Unit,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val zone = RoadmapCatalog.getZoneById(quest.zoneId)

    val doneCount = quest.subtopics.count { topics[it.id]?.completed == true }
    val totalCount = quest.subtopics.size
    val pct = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * 100.0).roundToInt() else 0

    val revealedQuestions = remember(quest.id) { mutableStateMapOf<Int, Boolean>() }
    var showLinkForm by remember(quest.id) { mutableStateOf(false) }
    var newLinkTitle by remember(quest.id) { mutableStateOf("") }
    var newLinkUrl by remember(quest.id) { mutableStateOf("") }
    var newLinkType by remember(quest.id) { mutableStateOf(ResourceType.DOCS) }

    val curatedLinks = remember(quest.id) {
        quest.subtopics.flatMap { it.resources }.distinctBy { it.url }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim),
    ) {
        val isWide = maxWidth >= sizing.sheetWideBreakpoint

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClose),
        )

        val panelShape = if (isWide) DroidQuestCornerShapes.ZoneBanner else DroidQuestCornerShapes.SheetTop
        val panelAlignment = if (isWide) Alignment.CenterEnd else Alignment.BottomCenter

        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = panelShape,
            border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
            shadowElevation = sizing.elevationModal,
            modifier = Modifier
                .align(panelAlignment)
                .then(
                    if (isWide) {
                        Modifier
                            .fillMaxHeight()
                            .widthIn(max = sizing.sheetMaxWidth)
                            .fillMaxWidth(0.85f)
                            .padding(spacing.lg)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.92f)
                            .statusBarsPadding()
                    }
                )
                .imePadding(),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (!isWide) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.base),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(sizing.dragHandleWidth)
                                .height(sizing.dragHandleHeight)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outline),
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.section, vertical = spacing.xl),
                    verticalArrangement = Arrangement.spacedBy(spacing.base),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).padding(end = spacing.lg),
                            verticalArrangement = Arrangement.spacedBy(spacing.xs),
                        ) {
                            val eyebrow = buildString {
                                append("ZONE ${zone.number} · ${quest.dayRange.uppercase()}")
                                if (quest.highYield) append(" · HIGH-YIELD")
                                if (quest.isBoss) append(" · BOSS")
                            }
                            Text(
                                text = eyebrow,
                                style = DroidQuestTheme.mono.micro,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = quest.title,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }

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
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(sizing.iconMd),
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "$doneCount of $totalCount subtopics mastered",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "$pct%",
                            style = DroidQuestTheme.mono.badge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    CalmProgressBar(
                        progress = pct / 100f,
                        height = sizing.progressBarMedium,
                        minVisibleFraction = if (doneCount > 0) 0.04f else 0f,
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.section, vertical = spacing.section)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(spacing.sheetSection),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
                        SectionLabelRow(
                            left = "SUBTOPICS",
                            right = "$doneCount/$totalCount Done",
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = DroidQuestCornerShapes.QuestCard,
                            border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Column {
                                quest.subtopics.forEachIndexed { idx, sub ->
                                    if (idx > 0) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                    val prog = topics[sub.id]
                                    val completed = prog?.completed == true
                                    val reviewStatus = SpacedRepetitionEngine.formatReviewStatus(prog)

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onToggleTopic(sub.id) }
                                            .padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
                                        verticalArrangement = Arrangement.spacedBy(spacing.md),
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
                                            verticalAlignment = Alignment.Top,
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(top = spacing.xxs)
                                                    .size(sizing.checkCircleSize)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (completed) MaterialTheme.colorScheme.primaryContainer
                                                        else MaterialTheme.colorScheme.surfaceContainerLowest
                                                    )
                                                    .border(
                                                        borders.medium,
                                                        if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                                        CircleShape,
                                                    ),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                if (completed) {
                                                    Icon(
                                                        imageVector = DroidQuestIcons.Check,
                                                        contentDescription = "Completed",
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
                                                    color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                    textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None,
                                                )
                                                Text(
                                                    text = sub.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }

                                            if (completed) {
                                                val isPinned = prog.flaggedForReview
                                                Box(
                                                    modifier = Modifier
                                                        .size(sizing.ghostButtonSize)
                                                        .clip(DroidQuestCornerShapes.MicroBadge)
                                                        .clickable { onToggleFlagReview(sub.id) },
                                                    contentAlignment = Alignment.Center,
                                                ) {
                                                    Icon(
                                                        imageVector = DroidQuestIcons.Pin,
                                                        contentDescription = if (isPinned) "Unpin review" else "Pin to review",
                                                        tint = if (isPinned) MaterialTheme.colorScheme.primary else ext.inkSubtle,
                                                        modifier = Modifier.size(sizing.iconMd),
                                                    )
                                                }
                                            }
                                        }

                                        if (completed && reviewStatus.isDue) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(start = sizing.ghostButtonSize),
                                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                            ) {
                                                QuickReviewChip("Forgot", false) { onRecordReview(sub.id, ReviewRating.FORGOT) }
                                                QuickReviewChip("Hard", false) { onRecordReview(sub.id, ReviewRating.HARD) }
                                                QuickReviewChip("Good", true) { onRecordReview(sub.id, ReviewRating.GOOD) }
                                                QuickReviewChip("Easy", false) { onRecordReview(sub.id, ReviewRating.EASY) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "STUDY LINKS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(
                                text = if (showLinkForm) "Close" else "+ Add link",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(DroidQuestCornerShapes.MicroBadge)
                                    .clickable { showLinkForm = !showLinkForm }
                                    .padding(horizontal = spacing.sm, vertical = spacing.xxs),
                            )
                        }

                        AnimatedVisibility(visible = showLinkForm) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = DroidQuestCornerShapes.QuestCard,
                                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
                            ) {
                                Column(
                                    modifier = Modifier.padding(spacing.cardPaddingCompact),
                                    verticalArrangement = Arrangement.spacedBy(spacing.base),
                                ) {
                                    OutlinedTextField(
                                        value = newLinkTitle,
                                        onValueChange = { newLinkTitle = it },
                                        placeholder = { Text("Title (e.g. DSA Course - Trees)") },
                                        singleLine = true,
                                        colors = questTextFieldColors(),
                                        shape = DroidQuestCornerShapes.TactileButton,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    OutlinedTextField(
                                        value = newLinkUrl,
                                        onValueChange = { newLinkUrl = it },
                                        placeholder = { Text("https://...") },
                                        singleLine = true,
                                        colors = questTextFieldColors(),
                                        shape = DroidQuestCornerShapes.TactileButton,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                                        verticalArrangement = Arrangement.spacedBy(spacing.sm),
                                    ) {
                                        ResourceType.entries.forEach { type ->
                                            val visual = ext.resource(type)
                                            val active = newLinkType == type
                                            Surface(
                                                color = if (active) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLowest,
                                                shape = DroidQuestCornerShapes.MicroBadge,
                                                border = BorderStroke(
                                                    borders.hairline,
                                                    if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f) else MaterialTheme.colorScheme.outlineVariant,
                                                ),
                                                modifier = Modifier
                                                    .clip(DroidQuestCornerShapes.MicroBadge)
                                                    .clickable { newLinkType = type },
                                            ) {
                                                Text(
                                                    text = visual.label,
                                                    style = DroidQuestTheme.mono.micro,
                                                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
                                                )
                                            }
                                        }
                                    }
                                    PrimaryActionButton(
                                        onClick = {
                                            if (newLinkTitle.isNotBlank() && newLinkUrl.isNotBlank()) {
                                                onAddCustomLink(quest.id, newLinkTitle, newLinkUrl, newLinkType)
                                                newLinkTitle = ""
                                                newLinkUrl = ""
                                                showLinkForm = false
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = spacing.cardPaddingCompact, vertical = spacing.base),
                                    ) {
                                        Text("Save Link", style = MaterialTheme.typography.labelLarge)
                                    }
                                }
                            }
                        }

                        if (curatedLinks.isNotEmpty() || customLinks.isNotEmpty()) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = DroidQuestCornerShapes.QuestCard,
                                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                            ) {
                                Column {
                                    curatedLinks.forEachIndexed { idx, res ->
                                        if (idx > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                        val resVisual = ext.resource(res.type)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onOpenUrl(res.url) }
                                                .padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(spacing.base),
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = DroidQuestCornerShapes.MicroBadge,
                                                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                                            ) {
                                                Text(
                                                    text = resVisual.label,
                                                    style = DroidQuestTheme.mono.micro,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
                                                )
                                            }
                                            Text(
                                                text = res.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Icon(
                                                imageVector = DroidQuestIcons.ExternalLink,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(sizing.iconSm),
                                            )
                                        }
                                    }

                                    customLinks.forEachIndexed { idx, link ->
                                        if (curatedLinks.isNotEmpty() || idx > 0) {
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                        }
                                        val visual = ext.resource(link.type)
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onOpenUrl(link.url) }
                                                .padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(spacing.base),
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = DroidQuestCornerShapes.MicroBadge,
                                                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                                            ) {
                                                Text(
                                                    text = visual.label,
                                                    style = DroidQuestTheme.mono.micro,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
                                                )
                                            }
                                            Text(
                                                text = link.title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Icon(
                                                imageVector = DroidQuestIcons.Trash2,
                                                contentDescription = "Remove link",
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier
                                                    .size(sizing.iconMd)
                                                    .clickable { onRemoveCustomLink(quest.id, link.id) },
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (quest.interviewQuestions.isNotEmpty()) {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
                            SectionLabelRow(
                                left = "INTERVIEW Q&A · ${quest.interviewQuestions.size}",
                                right = "Senior Bar",
                            )

                            quest.interviewQuestions.forEachIndexed { idx, iq ->
                                val expanded = revealedQuestions[idx] == true
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = DroidQuestCornerShapes.QuestCard,
                                    border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { revealedQuestions[idx] = !expanded }
                                                .padding(spacing.cardPaddingCompact),
                                            horizontalArrangement = Arrangement.spacedBy(spacing.base),
                                            verticalAlignment = Alignment.Top,
                                        ) {
                                            Text(
                                                text = "Q${idx + 1}",
                                                style = DroidQuestTheme.mono.badge,
                                                color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(top = spacing.xxs),
                                            )
                                            Text(
                                                text = iq.question,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.weight(1f),
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                                            ) {
                                                Text(
                                                    text = if (expanded) "Hide" else "Reveal",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Icon(
                                                    imageVector = if (expanded) DroidQuestIcons.ChevronUp else DroidQuestIcons.ChevronDown,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(sizing.iconMd),
                                                )
                                            }
                                        }

                                        AnimatedVisibility(visible = expanded) {
                                            Column {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                                                        .padding(spacing.cardPaddingCompact),
                                                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                                                ) {
                                                    Text(
                                                        text = "CORE MENTAL MODEL",
                                                        style = DroidQuestTheme.mono.micro,
                                                        color = MaterialTheme.colorScheme.primary,
                                                    )
                                                    Text(
                                                        text = iq.keyPoints,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = ext.inkSecondary,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
                        SectionLabelRow(
                            left = "QUEST NOTES",
                            right = "Auto-saved",
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { onUpdateNote(quest.id, it) },
                            placeholder = {
                                Text(
                                    "Write key takeaways, mental models, or real interview questions...",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            },
                            colors = questTextFieldColors(),
                            shape = DroidQuestCornerShapes.QuestCard,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = sizing.notesMinHeight),
                        )
                    }

                    Spacer(Modifier.height(spacing.lg))
                }
            }
        }
    }
}

@Composable
private fun SectionLabelRow(
    left: String,
    right: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = left,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = right,
            style = DroidQuestTheme.mono.micro,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun QuickReviewChip(
    label: String,
    isPrimary: Boolean,
    onClick: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    Surface(
        color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(
            borders.hairline,
            if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = Modifier
            .clip(DroidQuestCornerShapes.MicroBadge)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = spacing.base, vertical = spacing.sm),
        )
    }
}

@Composable
internal fun questTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = DroidQuestTheme.colors.inkSubtle,
    unfocusedPlaceholderColor = DroidQuestTheme.colors.inkSubtle,
    cursorColor = MaterialTheme.colorScheme.primary,
)
