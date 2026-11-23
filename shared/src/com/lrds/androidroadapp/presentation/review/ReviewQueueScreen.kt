package com.lrds.androidroadapp.presentation.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.engine.SpacedRepetitionEngine
import com.lrds.androidroadapp.domain.model.ReviewDueItem
import com.lrds.androidroadapp.domain.model.ReviewRating
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ReviewQueueContent(
    dueNow: List<ReviewDueItem>,
    upcoming: List<ReviewDueItem>,
    onRecordReview: (String, ReviewRating) -> Unit,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors
    val expandedCards = remember { mutableStateMapOf<String, Boolean>() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Concise Monochrome Review Summary Card
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = DroidQuestCornerShapes.ZoneBanner,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "SPACED REPETITION · ${SpacedRepetitionEngine.REVIEW_INTERVALS_DAYS.joinToString(" → ") { "${it}d" }}",
                            style = DroidQuestTheme.mono.micro,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shape = DroidQuestCornerShapes.MicroBadge,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        ) {
                            Text(
                                text = "+25 XP",
                                style = DroidQuestTheme.mono.micro,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "${dueNow.size}",
                                style = DroidQuestTheme.mono.kpiLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = "Due Now",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 3.dp),
                            )
                        }
                        Text(
                            text = "${upcoming.size} upcoming",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp),
                        )
                    }
                }
            }

            // DUE NOW ACTIVE-RECALL DECK
            if (dueNow.isEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = DroidQuestCornerShapes.QuestCard,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = DroidQuestIcons.CheckCircle2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(34.dp),
                        )
                        Text(
                            text = "All caught up",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Complete topics on the Trail or pin tricky topics from any Quest to build your active-recall deck.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            } else {
                dueNow.forEachIndexed { idx, item ->
                    val status = SpacedRepetitionEngine.formatReviewStatus(item.progress)
                    val showAnswers = expandedCards[item.subtopic.id] == true

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = DroidQuestCornerShapes.ZoneBanner,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            // Meta Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "ZONE ${item.zone.number} · ${status.label.uppercase()} · ${idx + 1}/${dueNow.size}",
                                    style = DroidQuestTheme.mono.micro,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(DroidQuestCornerShapes.MicroBadge)
                                        .clickable { onSelectQuest(item.quest.id) }
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
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
                                        modifier = Modifier.size(13.dp),
                                    )
                                }
                            }

                            // Topic Title & Description
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

                            // Recessed Active-Recall Prompt Box
                            if (item.quest.interviewQuestions.isNotEmpty()) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                    shape = DroidQuestCornerShapes.QuestCard,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        item.quest.interviewQuestions.forEachIndexed { qIdx, q ->
                                            if (qIdx > 0) {
                                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                            }
                                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(DroidQuestCornerShapes.TactileButton)
                                                .clickable { expandedCards[item.subtopic.id] = !showAnswers },
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(vertical = 9.dp, horizontal = 12.dp),
                                                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    text = if (showAnswers) "Hide Key Points" else "Reveal Key Points",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                                Icon(
                                                    imageVector = if (showAnswers) DroidQuestIcons.ChevronUp else DroidQuestIcons.ChevronDown,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(15.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Stage & 4-Column Calm Interval Dispatch Row
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

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                ReviewRatingTile(
                                    title = "Forgot",
                                    interval = "1d",
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    titleColor = MaterialTheme.colorScheme.onSurface,
                                    subColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    onClick = { onRecordReview(item.subtopic.id, ReviewRating.FORGOT) },
                                    modifier = Modifier.weight(1f),
                                )
                                ReviewRatingTile(
                                    title = "Hard",
                                    interval = "2d",
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                                    titleColor = MaterialTheme.colorScheme.onSurface,
                                    subColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    onClick = { onRecordReview(item.subtopic.id, ReviewRating.HARD) },
                                    modifier = Modifier.weight(1f),
                                )
                                ReviewRatingTile(
                                    title = "Good",
                                    interval = "Next",
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    borderColor = MaterialTheme.colorScheme.primary,
                                    titleColor = MaterialTheme.colorScheme.onPrimary,
                                    subColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                                    onClick = { onRecordReview(item.subtopic.id, ReviewRating.GOOD) },
                                    modifier = Modifier.weight(1f),
                                )
                                ReviewRatingTile(
                                    title = "Easy",
                                    interval = "Jump",
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    borderColor = MaterialTheme.colorScheme.outline,
                                    titleColor = MaterialTheme.colorScheme.primary,
                                    subColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    onClick = { onRecordReview(item.subtopic.id, ReviewRating.EASY) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }

            // UPCOMING REVIEWS LIST
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Text(
                            text = "Completed topics automatically appear here with their next review date.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                        )
                    }
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = DroidQuestCornerShapes.QuestCard,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    ) {
                        Column {
                            upcoming.forEachIndexed { idx, item ->
                                if (idx > 0) {
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                }
                                val status = SpacedRepetitionEngine.formatReviewStatus(item.progress)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSelectQuest(item.quest.id) }
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f).padding(end = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
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
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
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
    Surface(
        color = containerColor,
        shape = DroidQuestCornerShapes.TactileButton,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .clip(DroidQuestCornerShapes.TactileButton)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
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
