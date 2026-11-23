package com.lrds.androidroadapp.presentation.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.InterviewQuestion
import com.lrds.androidroadapp.domain.model.ReviewDueItem
import com.lrds.androidroadapp.domain.model.ReviewRating
import com.lrds.androidroadapp.presentation.StableList
import com.lrds.androidroadapp.presentation.toStableList
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ReviewQueueContent(
    dueNow: StableList<ReviewDueItem>,
    upcoming: StableList<ReviewDueItem>,
    onRecordReview: (String, ReviewRating) -> Unit,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val expandedCards = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = sizing.maxContentWidth).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.xxl),
        ) {
            ReviewSummaryCard(
                dueCount = dueNow.size,
                upcomingCount = upcoming.size,
            )

            if (dueNow.isEmpty()) {
                ReviewEmptyStateCard()
            } else {
                dueNow.forEachIndexed { idx, item ->
                    val showAnswers = expandedCards[item.subtopic.id] == true
                    DueReviewCard(
                        item = item,
                        position = idx + 1,
                        totalDue = dueNow.size,
                        showAnswers = showAnswers,
                        onToggleAnswers = { expandedCards[item.subtopic.id] = !showAnswers },
                        onSelectQuest = { onSelectQuest(item.quest.id) },
                        onRecordReview = { rating -> onRecordReview(item.subtopic.id, rating) },
                    )
                }
            }

            UpcomingReviewsSection(
                upcoming = upcoming,
                onSelectQuest = onSelectQuest,
            )

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun ReviewSummaryCard(
    dueCount: Int,
    upcomingCount: Int,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val intervalLadder = SpacedRepetitionEngine.REVIEW_INTERVALS_DAYS.joinToString(" → ") { "${it}d" }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.ZoneBanner,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(spacing.cardPaddingSpacious),
            verticalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "SPACED REPETITION · $intervalLadder",
                    style = DroidQuestTheme.mono.micro,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = spacing.md),
                )
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = DroidQuestCornerShapes.MicroBadge,
                    border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Text(
                        text = "+25 XP",
                        style = DroidQuestTheme.mono.micro,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    Text(
                        text = "$dueCount",
                        style = DroidQuestTheme.mono.kpiLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "Due Now",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = spacing.xs),
                    )
                }
                Text(
                    text = "$upcomingCount upcoming",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = spacing.xs),
                )
            }
        }
    }
}

@Composable
private fun ReviewEmptyStateCard() {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.emptyStatePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            Icon(
                imageVector = DroidQuestIcons.CheckCircle2,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(sizing.iconEmptyState),
            )
            Text(
                text = "All caught up",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Complete topics on the Trail or pin tricky topics from any Quest " +
                    "to build your active-recall deck.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun DueReviewCard(
    item: ReviewDueItem,
    position: Int,
    totalDue: Int,
    showAnswers: Boolean,
    onToggleAnswers: () -> Unit,
    onSelectQuest: () -> Unit,
    onRecordReview: (ReviewRating) -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val status = SpacedRepetitionEngine.formatReviewStatus(item.progress)

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = DroidQuestCornerShapes.ZoneBanner,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            DueReviewHeaderRow(
                metaText = "ZONE ${item.zone.number} · ${status.label.uppercase()} · $position/$totalDue",
                onSelectQuest = onSelectQuest,
            )

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = item.subtopic.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = item.subtopic.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = ext.inkSecondary,
                )
            }

            if (item.quest.interviewQuestions.isNotEmpty()) {
                val stableQuestions = remember(item.quest.id) { item.quest.interviewQuestions.toStableList() }
                ReviewInterviewQuestionsBox(
                    questions = stableQuestions,
                    showAnswers = showAnswers,
                    onToggleAnswers = onToggleAnswers,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "RATE RECALL",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "Stage ${item.progress.reviewStage}/5 · ${item.progress.reviewCount}×",
                    style = DroidQuestTheme.mono.micro,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            ReviewRatingRow(onRecordReview = onRecordReview)
        }
    }
}

@Composable
private fun DueReviewHeaderRow(
    metaText: String,
    onSelectQuest: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = metaText,
            style = DroidQuestTheme.mono.micro,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(end = spacing.md),
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            modifier = Modifier
                .clip(DroidQuestCornerShapes.MicroBadge)
                .clickable(onClick = onSelectQuest)
                .padding(horizontal = spacing.xs, vertical = spacing.xxs),
        ) {
            Text(
                text = "Open Quest",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Icon(
                imageVector = DroidQuestIcons.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(sizing.iconSm),
            )
        }
    }
}

@Composable
private fun ReviewInterviewQuestionsBox(
    questions: StableList<InterviewQuestion>,
    showAnswers: Boolean,
    onToggleAnswers: () -> Unit,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val toggleIcon = if (showAnswers) DroidQuestIcons.ChevronUp else DroidQuestIcons.ChevronDown

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(spacing.cardPaddingCompact),
            verticalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            questions.forEachIndexed { qIdx, q ->
                if (qIdx > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    AnimatedVisibility(visible = showAnswers) {
                        Text(
                            text = q.keyPoints,
                            style = MaterialTheme.typography.bodySmall,
                            color = ext.inkSecondary,
                        )
                    }
                }
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.TactileButton,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(DroidQuestCornerShapes.TactileButton)
                    .clickable(onClick = onToggleAnswers),
            ) {
                Row(
                    modifier = Modifier.padding(vertical = spacing.base, horizontal = spacing.lg),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (showAnswers) "Hide Key Points" else "Reveal Key Points",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Icon(
                        imageVector = toggleIcon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(sizing.iconMd),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewRatingRow(onRecordReview: (ReviewRating) -> Unit) {
    val spacing = DroidQuestTheme.spacing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        ReviewRatingTile(
            title = "Forgot",
            interval = "1d",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            titleColor = MaterialTheme.colorScheme.onSurface,
            subColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { onRecordReview(ReviewRating.FORGOT) },
            modifier = Modifier.weight(1f),
        )
        ReviewRatingTile(
            title = "Hard",
            interval = "2d",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            titleColor = MaterialTheme.colorScheme.onSurface,
            subColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { onRecordReview(ReviewRating.HARD) },
            modifier = Modifier.weight(1f),
        )
        ReviewRatingTile(
            title = "Good",
            interval = "Next",
            containerColor = MaterialTheme.colorScheme.primary,
            borderColor = MaterialTheme.colorScheme.primary,
            titleColor = MaterialTheme.colorScheme.onPrimary,
            subColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
            onClick = { onRecordReview(ReviewRating.GOOD) },
            modifier = Modifier.weight(1f),
        )
        ReviewRatingTile(
            title = "Easy",
            interval = "Jump",
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            borderColor = MaterialTheme.colorScheme.outline,
            titleColor = MaterialTheme.colorScheme.primary,
            subColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = { onRecordReview(ReviewRating.EASY) },
            modifier = Modifier.weight(1f),
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun UpcomingReviewsSection(
    upcoming: StableList<ReviewDueItem>,
    onSelectQuest: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders

    Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "UPCOMING REVIEWS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${upcoming.size} in queue",
                style = DroidQuestTheme.mono.micro,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (upcoming.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Text(
                    text = "Completed topics automatically appear here with their next review date.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(spacing.cardPadding),
                )
            }
        } else {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.QuestCard,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Column {
                    upcoming.forEachIndexed { idx, item ->
                        if (idx > 0) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                        UpcomingReviewRow(
                            item = item,
                            onSelectQuest = { onSelectQuest(item.quest.id) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun UpcomingReviewRow(
    item: ReviewDueItem,
    onSelectQuest: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val status = SpacedRepetitionEngine.formatReviewStatus(item.progress)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelectQuest)
            .padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = spacing.base),
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = item.subtopic.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.quest.title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = status.label,
            style = DroidQuestTheme.mono.micro,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ReviewRatingTile(
    title: String,
    interval: String,
    containerColor: Color,
    borderColor: Color,
    titleColor: Color,
    subColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    Surface(
        color = containerColor,
        shape = DroidQuestCornerShapes.TactileButton,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = modifier
            .clip(DroidQuestCornerShapes.TactileButton)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(vertical = spacing.base, horizontal = spacing.sm),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = titleColor,
                maxLines = 1,
            )
            Text(
                text = interval,
                style = DroidQuestTheme.mono.micro,
                color = subColor,
                maxLines = 1,
            )
        }
    }
}
