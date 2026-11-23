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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.CustomLink
import com.lrds.androidroadapp.domain.model.InterviewQuestion
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.domain.model.StudyResource
import com.lrds.androidroadapp.domain.model.SubTopic
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.presentation.StableList
import com.lrds.androidroadapp.presentation.StableMap
import com.lrds.androidroadapp.presentation.toStableList
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime

private const val PERCENT_SCALE = 100.0
private const val WIDE_SHEET_WIDTH_FRACTION = 0.85f
private const val COMPACT_SHEET_HEIGHT_FRACTION = 0.92f

@OptIn(ExperimentalLayoutApi::class, ExperimentalTime::class)
@Composable
fun QuestDetailSheet(
    quest: QuestModule,
    topics: StableMap<String, TopicProgress>,
    note: String,
    customLinks: StableList<CustomLink>,
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
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val zone = RoadmapCatalog.getZoneById(quest.zoneId)
    val doneCount = quest.subtopics.count { topics[it.id]?.completed == true }
    val totalCount = quest.subtopics.size
    val pct = if (totalCount > 0) ((doneCount.toDouble() / totalCount) * PERCENT_SCALE).roundToInt() else 0
    val curatedLinks = remember(quest.id) {
        quest.subtopics.flatMap { it.resources }.distinctBy { it.url }.toStableList()
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim),
    ) {
        val isWide = maxWidth >= sizing.sheetWideBreakpoint
        Box(modifier = Modifier.fillMaxSize().clickable(onClick = onClose))
        QuestSheetContainer(
            isWide = isWide,
            modifier = Modifier.align(if (isWide) Alignment.CenterEnd else Alignment.BottomCenter),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                QuestSheetHeader(
                    quest = quest,
                    zone = zone,
                    doneCount = doneCount,
                    totalCount = totalCount,
                    pct = pct,
                    isWide = isWide,
                    onClose = onClose,
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.section, vertical = spacing.section)
                        .navigationBarsPadding(),
                    verticalArrangement = Arrangement.spacedBy(spacing.sheetSection),
                ) {
                    QuestSubtopicsSection(
                        quest = quest,
                        topics = topics,
                        doneCount = doneCount,
                        totalCount = totalCount,
                        onToggleTopic = onToggleTopic,
                        onRecordReview = onRecordReview,
                        onToggleFlagReview = onToggleFlagReview,
                    )

                    QuestStudyLinksSection(
                        questId = quest.id,
                        curatedLinks = curatedLinks,
                        customLinks = customLinks,
                        onAddCustomLink = onAddCustomLink,
                        onRemoveCustomLink = onRemoveCustomLink,
                        onOpenUrl = onOpenUrl,
                    )

                    if (quest.interviewQuestions.isNotEmpty()) {
                        val stableQuestions = remember(quest.id) { quest.interviewQuestions.toStableList() }
                        QuestInterviewQaSection(questId = quest.id, questions = stableQuestions)
                    }

                    QuestNotesSection(
                        note = note,
                        onNoteChange = { onUpdateNote(quest.id, it) },
                    )

                    Spacer(Modifier.height(spacing.lg))
                }
            }
        }
    }
}

@Composable
private fun QuestSheetContainer(
    isWide: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val panelShape = if (isWide) DroidQuestCornerShapes.ZoneBanner else DroidQuestCornerShapes.SheetTop
    val panelSizeModifier = if (isWide) {
        Modifier
            .fillMaxHeight()
            .widthIn(max = sizing.sheetMaxWidth)
            .fillMaxWidth(WIDE_SHEET_WIDTH_FRACTION)
            .padding(spacing.lg)
    } else {
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(COMPACT_SHEET_HEIGHT_FRACTION)
            .statusBarsPadding()
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        shape = panelShape,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
        shadowElevation = sizing.elevationModal,
        modifier = modifier
            .then(panelSizeModifier)
            .imePadding(),
        content = content,
    )
}

@Composable
private fun QuestSheetHeader(
    quest: QuestModule,
    zone: AdventureZone,
    doneCount: Int,
    totalCount: Int,
    pct: Int,
    isWide: Boolean,
    onClose: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val eyebrow = buildString {
        append("ZONE ${zone.number} · ${quest.dayRange.uppercase()}")
        if (quest.highYield) append(" · HIGH-YIELD")
        if (quest.isBoss) append(" · BOSS")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (!isWide) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = spacing.base),
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

                SheetCloseButton(onClose = onClose)
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
    }
}

@Composable
private fun SheetCloseButton(onClose: () -> Unit) {
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

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

@OptIn(ExperimentalTime::class)
@Composable
private fun QuestSubtopicsSection(
    quest: QuestModule,
    topics: StableMap<String, TopicProgress>,
    doneCount: Int,
    totalCount: Int,
    onToggleTopic: (String) -> Unit,
    onRecordReview: (String, ReviewRating) -> Unit,
    onToggleFlagReview: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
        SectionLabelRow(left = "SUBTOPICS", right = "$doneCount/$totalCount Done")

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
                    QuestSubtopicRow(
                        subtopic = sub,
                        progress = topics[sub.id],
                        onToggleTopic = { onToggleTopic(sub.id) },
                        onToggleFlagReview = { onToggleFlagReview(sub.id) },
                        onRecordReview = { rating -> onRecordReview(sub.id, rating) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun QuestSubtopicRow(
    subtopic: SubTopic,
    progress: TopicProgress?,
    onToggleTopic: () -> Unit,
    onToggleFlagReview: () -> Unit,
    onRecordReview: (ReviewRating) -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val completed = progress?.completed == true
    val reviewStatus = SpacedRepetitionEngine.formatReviewStatus(progress)
    val titleColor = if (completed) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleTopic)
            .padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
            verticalAlignment = Alignment.Top,
        ) {
            SubtopicCheckCircle(completed = completed)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xxs),
            ) {
                Text(
                    text = subtopic.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = titleColor,
                    textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None,
                )
                Text(
                    text = subtopic.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (completed) {
                val isPinned = progress.flaggedForReview
                Box(
                    modifier = Modifier
                        .size(sizing.ghostButtonSize)
                        .clip(DroidQuestCornerShapes.MicroBadge)
                        .clickable(onClick = onToggleFlagReview),
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
                modifier = Modifier.fillMaxWidth().padding(start = sizing.ghostButtonSize),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                QuickReviewChip("Forgot", false) { onRecordReview(ReviewRating.FORGOT) }
                QuickReviewChip("Hard", false) { onRecordReview(ReviewRating.HARD) }
                QuickReviewChip("Good", true) { onRecordReview(ReviewRating.GOOD) }
                QuickReviewChip("Easy", false) { onRecordReview(ReviewRating.EASY) }
            }
        }
    }
}

@Composable
private fun SubtopicCheckCircle(completed: Boolean) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val checkBg = if (completed) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val checkBorder = if (completed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .padding(top = spacing.xxs)
            .size(sizing.checkCircleSize)
            .clip(CircleShape)
            .background(checkBg)
            .border(borders.medium, checkBorder, CircleShape),
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuestStudyLinksSection(
    questId: String,
    curatedLinks: StableList<StudyResource>,
    customLinks: StableList<CustomLink>,
    onAddCustomLink: (String, String, String, ResourceType) -> Unit,
    onRemoveCustomLink: (String, String) -> Unit,
    onOpenUrl: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    var showLinkForm by remember(questId) { mutableStateOf(false) }

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
            AddCustomLinkForm(
                questId = questId,
                onSave = { title, url, type ->
                    onAddCustomLink(questId, title, url, type)
                    showLinkForm = false
                },
            )
        }

        if (curatedLinks.isNotEmpty() || customLinks.isNotEmpty()) {
            QuestLinksListCard(
                curatedLinks = curatedLinks,
                customLinks = customLinks,
                onOpenUrl = onOpenUrl,
                onRemoveCustomLink = { linkId -> onRemoveCustomLink(questId, linkId) },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddCustomLinkForm(
    questId: String,
    onSave: (title: String, url: String, type: ResourceType) -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    var newLinkTitle by remember(questId) { mutableStateOf("") }
    var newLinkUrl by remember(questId) { mutableStateOf("") }
    var newLinkType by remember(questId) { mutableStateOf(ResourceType.DOCS) }

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
                    ResourceTypeChip(
                        label = ext.resource(type).label,
                        active = newLinkType == type,
                        onClick = { newLinkType = type },
                    )
                }
            }
            PrimaryActionButton(
                onClick = {
                    if (newLinkTitle.isNotBlank() && newLinkUrl.isNotBlank()) {
                        onSave(newLinkTitle, newLinkUrl, newLinkType)
                        newLinkTitle = ""
                        newLinkUrl = ""
                    }
                },
                contentPadding = PaddingValues(horizontal = spacing.cardPaddingCompact, vertical = spacing.base),
            ) {
                Text("Save Link", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun ResourceTypeChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val bgColor = if (active) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLowest
    }
    val borderColor = if (active) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val labelColor = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = bgColor,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = Modifier
            .clip(DroidQuestCornerShapes.MicroBadge)
            .clickable(onClick = onClick),
    ) {
        Text(
            text = label,
            style = DroidQuestTheme.mono.micro,
            color = labelColor,
            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
        )
    }
}

@Composable
private fun QuestLinksListCard(
    curatedLinks: StableList<StudyResource>,
    customLinks: StableList<CustomLink>,
    onOpenUrl: (String) -> Unit,
    onRemoveCustomLink: (String) -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            curatedLinks.forEachIndexed { idx, res ->
                if (idx > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                StudyLinkRow(
                    badgeLabel = ext.resource(res.type).label,
                    badgeColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    title = res.title,
                    onClick = { onOpenUrl(res.url) },
                    trailingIcon = {
                        Icon(
                            imageVector = DroidQuestIcons.ExternalLink,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(sizing.iconSm),
                        )
                    },
                )
            }

            customLinks.forEachIndexed { idx, link ->
                if (curatedLinks.isNotEmpty() || idx > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                StudyLinkRow(
                    badgeLabel = ext.resource(link.type).label,
                    badgeColor = MaterialTheme.colorScheme.primary,
                    title = link.title,
                    onClick = { onOpenUrl(link.url) },
                    trailingIcon = {
                        Icon(
                            imageVector = DroidQuestIcons.Trash2,
                            contentDescription = "Remove link",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(sizing.iconMd)
                                .clickable { onRemoveCustomLink(link.id) },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun StudyLinkRow(
    badgeLabel: String,
    badgeColor: Color,
    title: String,
    onClick: () -> Unit,
    trailingIcon: @Composable () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                text = badgeLabel,
                style = DroidQuestTheme.mono.micro,
                color = badgeColor,
                modifier = Modifier.padding(horizontal = spacing.sm, vertical = spacing.xs),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        trailingIcon()
    }
}

@Composable
private fun QuestInterviewQaSection(
    questId: String,
    questions: StableList<InterviewQuestion>,
) {
    val spacing = DroidQuestTheme.spacing
    val revealedQuestions = remember(questId) { mutableStateMapOf<Int, Boolean>() }

    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
        SectionLabelRow(
            left = "INTERVIEW Q&A · ${questions.size}",
            right = "Senior Bar",
        )

        questions.forEachIndexed { idx, iq ->
            val expanded = revealedQuestions[idx] == true
            InterviewQuestionCard(
                index = idx + 1,
                question = iq,
                expanded = expanded,
                onToggle = { revealedQuestions[idx] = !expanded },
            )
        }
    }
}

@Composable
private fun InterviewQuestionCard(
    index: Int,
    question: InterviewQuestion,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val numberColor = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val chevronIcon = if (expanded) DroidQuestIcons.ChevronUp else DroidQuestIcons.ChevronDown

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(spacing.cardPaddingCompact),
                horizontalArrangement = Arrangement.spacedBy(spacing.base),
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "Q$index",
                    style = DroidQuestTheme.mono.badge,
                    color = numberColor,
                    modifier = Modifier.padding(top = spacing.xxs),
                )
                Text(
                    text = question.question,
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
                        imageVector = chevronIcon,
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
                            text = question.keyPoints,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ext.inkSecondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestNotesSection(
    note: String,
    onNoteChange: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
        SectionLabelRow(left = "QUEST NOTES", right = "Auto-saved")
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
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
    val bgColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isPrimary) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val borderColor = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Surface(
        color = bgColor,
        contentColor = contentColor,
        shape = DroidQuestCornerShapes.MicroBadge,
        border = BorderStroke(borders.hairline, borderColor),
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
