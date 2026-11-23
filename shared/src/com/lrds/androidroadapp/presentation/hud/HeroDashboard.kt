package com.lrds.androidroadapp.presentation.hud

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.components.SecondaryTonalButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.PillarStat
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ReviewDueItem
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.presentation.ActiveTab
import com.lrds.androidroadapp.presentation.StableList
import com.lrds.androidroadapp.presentation.StableMap

private const val WIDE_READINESS_WEIGHT = 1.2f

@Composable
fun HeroDashboard(
    completedCount: Int,
    totalCount: Int,
    overallPct: Int,
    daysLeft: Int,
    dailyPace: Double,
    pillarStats: StableList<PillarStat>,
    nextRecommendedQuest: QuestModule?,
    topics: StableMap<String, TopicProgress>,
    topDueReview: ReviewDueItem?,
    dueReviewCount: Int,
    onTabChange: (ActiveTab) -> Unit,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.screenHorizontal, vertical = spacing.xl),
        contentAlignment = Alignment.Center,
    ) {
        val isWide = maxWidth >= sizing.wideBreakpoint

        Column(
            modifier = Modifier.widthIn(max = sizing.maxContentWidth).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xl),
                    verticalAlignment = Alignment.Top,
                ) {
                    HeroReadinessCard(
                        completedCount = completedCount,
                        totalCount = totalCount,
                        overallPct = overallPct,
                        daysLeft = daysLeft,
                        dailyPace = dailyPace,
                        pillarStats = pillarStats,
                        modifier = Modifier.weight(WIDE_READINESS_WEIGHT),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.lg),
                    ) {
                        if (dueReviewCount > 0) {
                            ReviewsDueRowCard(
                                dueReviewCount = dueReviewCount,
                                topDueReview = topDueReview,
                                onOpenReviews = { onTabChange(ActiveTab.REVIEWS) },
                            )
                        }
                        if (nextRecommendedQuest != null) {
                            NextQuestActionCard(
                                quest = nextRecommendedQuest,
                                topics = topics,
                                onSelectQuest = onSelectQuest,
                            )
                        }
                    }
                }
            } else {
                HeroReadinessCard(
                    completedCount = completedCount,
                    totalCount = totalCount,
                    overallPct = overallPct,
                    daysLeft = daysLeft,
                    dailyPace = dailyPace,
                    pillarStats = pillarStats,
                )
                if (dueReviewCount > 0) {
                    ReviewsDueRowCard(
                        dueReviewCount = dueReviewCount,
                        topDueReview = topDueReview,
                        onOpenReviews = { onTabChange(ActiveTab.REVIEWS) },
                    )
                }
                if (nextRecommendedQuest != null) {
                    NextQuestActionCard(
                        quest = nextRecommendedQuest,
                        topics = topics,
                        onSelectQuest = onSelectQuest,
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroReadinessCard(
    completedCount: Int,
    totalCount: Int,
    overallPct: Int,
    daysLeft: Int,
    dailyPace: Double,
    pillarStats: StableList<PillarStat>,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.ZoneBanner,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth(),
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
                    text = "100-DAY SPRINT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${daysLeft}d left · ${dailyPace}/day",
                    style = DroidQuestTheme.mono.badge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(spacing.base)) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(spacing.base),
                ) {
                    Text(
                        text = "$overallPct%",
                        style = DroidQuestTheme.mono.kpiLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "$completedCount of $totalCount topics mastered",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = spacing.xs),
                    )
                }

                CalmProgressBar(
                    progress = overallPct / 100f,
                    height = sizing.progressBarStandard,
                )
            }

            HeroPillarBreakdownRow(pillarStats = pillarStats)
        }
    }
}

@Composable
private fun HeroPillarBreakdownRow(pillarStats: StableList<PillarStat>) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.base),
    ) {
        pillarStats.forEach { stat ->
            val shortName = when (stat.pillar) {
                PillarId.ANDROID -> "Android"
                PillarId.DSA -> "DSA"
                PillarId.SYSTEM_DESIGN -> "Sys Des"
                PillarId.CAREER -> "Career"
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = shortName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${stat.pct}%",
                        style = DroidQuestTheme.mono.micro,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                CalmProgressBar(
                    progress = stat.pct / 100f,
                    height = sizing.progressBarThin,
                    minVisibleFraction = if (stat.done > 0) 0.08f else 0f,
                )
            }
        }
    }
}

@Composable
private fun ReviewsDueRowCard(
    dueReviewCount: Int,
    topDueReview: ReviewDueItem?,
    onOpenReviews: () -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = DroidQuestCornerShapes.TactileButton,
                border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.size(sizing.iconBoxSize),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = DroidQuestIcons.Brain,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(sizing.iconLg),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xxs),
            ) {
                Text(
                    text = "$dueReviewCount Review${if (dueReviewCount == 1) "" else "s"} Due",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = topDueReview?.subtopic?.title ?: "Refresh completed topics",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            SecondaryTonalButton(
                onClick = onOpenReviews,
                contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.md),
            ) {
                Text(
                    text = "Review",
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = DroidQuestIcons.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(sizing.iconSm),
                )
            }
        }
    }
}

@Composable
private fun NextQuestActionCard(
    quest: QuestModule,
    topics: StableMap<String, TopicProgress>,
    onSelectQuest: (String) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val doneSubs = quest.subtopics.count { topics[it.id]?.completed == true }
    val totalSubs = quest.subtopics.size
    val eyebrow = buildString {
        append("NEXT QUEST · ")
        append(quest.dayRange.uppercase())
        if (quest.highYield) append(" · HIGH-YIELD")
    }

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacing.base),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = eyebrow,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(end = spacing.md),
                )
                Text(
                    text = "$doneSubs of $totalSubs subtopics",
                    style = DroidQuestTheme.mono.micro,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = quest.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            PrimaryActionButton(
                onClick = { onSelectQuest(quest.id) },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = spacing.cardPadding, vertical = spacing.lg),
            ) {
                Text(
                    text = "Continue Quest",
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = DroidQuestIcons.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(sizing.iconMd),
                )
            }
        }
    }
}
