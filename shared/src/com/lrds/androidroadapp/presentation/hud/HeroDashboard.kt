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
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.designsystem.components.CalmProgressBar
import com.lrds.androidroadapp.designsystem.components.PrimaryActionButton
import com.lrds.androidroadapp.designsystem.components.SecondaryTonalButton
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.PillarStat
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ReviewDueItem
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.presentation.ActiveTab

@Composable
fun HeroDashboard(
    completedCount: Int,
    totalCount: Int,
    overallPct: Int,
    daysLeft: Int,
    dailyPace: Double,
    pillarStats: List<PillarStat>,
    nextRecommendedQuest: QuestModule?,
    topics: Map<String, TopicProgress>,
    topDueReview: ReviewDueItem?,
    dueReviewCount: Int,
    onTabChange: (ActiveTab) -> Unit,
    onSelectQuest: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        val isWide = maxWidth >= 820.dp

        Column(
            modifier = Modifier.widthIn(max = 1040.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    HeroReadinessCard(
                        completedCount = completedCount,
                        totalCount = totalCount,
                        overallPct = overallPct,
                        daysLeft = daysLeft,
                        dailyPace = dailyPace,
                        pillarStats = pillarStats,
                        modifier = Modifier.weight(1.2f),
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
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
    pillarStats: List<PillarStat>,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.ZoneBanner,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth(),
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

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }

                CalmProgressBar(
                    progress = overallPct / 100f,
                    height = 6.dp,
                )
            }

            // Concise 4-column monochrome pillar breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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
                        verticalArrangement = Arrangement.spacedBy(5.dp),
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
                            height = 4.dp,
                            minVisibleFraction = if (stat.done > 0) 0.08f else 0f,
                        )
                    }
                }
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
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = DroidQuestCornerShapes.QuestCard,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = DroidQuestCornerShapes.TactileButton,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.size(38.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = DroidQuestIcons.Brain,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
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
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "Review",
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = DroidQuestIcons.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun NextQuestActionCard(
    quest: QuestModule,
    topics: Map<String, TopicProgress>,
    onSelectQuest: (String) -> Unit,
) {
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
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
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                )
                Text(
                    text = "$doneSubs of $totalSubs subtopics",
                    style = DroidQuestTheme.mono.micro,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Continue Quest",
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = DroidQuestIcons.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
