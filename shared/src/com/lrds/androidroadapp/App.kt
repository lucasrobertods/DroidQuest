package com.lrds.androidroadapp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import com.lrds.androidroadapp.data.HapticKind
import com.lrds.androidroadapp.data.ToastTone
import com.lrds.androidroadapp.designsystem.components.CelebrationConfettiOverlay
import com.lrds.androidroadapp.designsystem.components.FloatingBottomNavBar
import com.lrds.androidroadapp.designsystem.components.FloatingCelebrationToast
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.di.ensureKoinStarted
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.domain.model.ZoneId
import com.lrds.androidroadapp.presentation.ActiveTab
import com.lrds.androidroadapp.presentation.DroidQuestEffect
import com.lrds.androidroadapp.presentation.DroidQuestIntent
import com.lrds.androidroadapp.presentation.DroidQuestViewModel
import com.lrds.androidroadapp.presentation.checklist.ChecklistContent
import com.lrds.androidroadapp.presentation.hud.HeroDashboard
import com.lrds.androidroadapp.presentation.hud.TopHud
import com.lrds.androidroadapp.presentation.quest.QuestDetailSheet
import com.lrds.androidroadapp.presentation.review.ReviewQueueContent
import com.lrds.androidroadapp.presentation.settings.SettingsDialog
import com.lrds.androidroadapp.presentation.trail.TrailQuestNodeRow
import com.lrds.androidroadapp.presentation.trail.ZoneBannerHeader
import com.lrds.androidroadapp.presentation.trail.ZoneTeleportBar
import com.lrds.androidroadapp.presentation.trophies.TrophiesContent
import kotlinx.coroutines.launch
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.compose.viewmodel.koinViewModel

private const val NAV_SCROLL_COMPACT_THRESHOLD_PX = 18

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    App(onThemeChanged = {})
}

@Composable
fun App(
    onThemeChanged: (isDark: Boolean) -> Unit,
    platformBackHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit = { _, _ -> },
) {
    remember { ensureKoinStarted() }

    val viewModel: DroidQuestViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = when (state.progress.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    LaunchedEffect(isDark) {
        onThemeChanged(isDark)
    }

    DroidQuestTheme(darkTheme = isDark) {
        DroidQuestAppContent(
            viewModel = viewModel,
            isDarkTheme = isDark,
            platformBackHandler = platformBackHandler,
        )
    }
}

@Composable
fun DroidQuestAppContent(
    viewModel: DroidQuestViewModel,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    platformBackHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit = { _, _ -> },
) {
    val state by viewModel.uiState.collectAsState()
    val trailListState = rememberLazyListState()
    val checklistListState = rememberLazyListState()
    val reviewsListState = rememberLazyListState()
    val trophiesListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val motion = DroidQuestTheme.motion

    var selectedZoneId by remember { mutableStateOf<ZoneId?>(ZoneId.EMERALD) }
    var isNavCompact by remember { mutableStateOf(false) }

    val canHandleBack = state.settingsOpen || state.selectedQuest != null || state.activeTab != ActiveTab.TRAIL
    platformBackHandler(canHandleBack) {
        when {
            state.settingsOpen -> viewModel.onIntent(DroidQuestIntent.SetSettingsOpen(false))
            state.selectedQuest != null -> viewModel.onIntent(DroidQuestIntent.SelectQuest(null))
            state.activeTab != ActiveTab.TRAIL -> viewModel.onIntent(DroidQuestIntent.SelectTab(ActiveTab.TRAIL))
        }
    }

    // Retain last non-null quest so QuestDetailSheet animates out cleanly on close
    var lastSelectedQuest by remember { mutableStateOf<QuestModule?>(null) }
    if (state.selectedQuest != null) {
        lastSelectedQuest = state.selectedQuest
    }

    val activeListState: LazyListState = when (state.activeTab) {
        ActiveTab.TRAIL -> trailListState
        ActiveTab.CHECKLIST -> checklistListState
        ActiveTab.REVIEWS -> reviewsListState
        ActiveTab.TROPHIES -> trophiesListState
    }

    // Revolut-style scroll-direction tracking: compact floating pill on scroll-down, restore labels on scroll-up
    LaunchedEffect(state.activeTab) {
        isNavCompact = false
        var prevIndex = activeListState.firstVisibleItemIndex
        var prevOffset = activeListState.firstVisibleItemScrollOffset

        snapshotFlow {
            activeListState.firstVisibleItemIndex to activeListState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            if (index == 0 && offset < NAV_SCROLL_COMPACT_THRESHOLD_PX) {
                isNavCompact = false
            } else if (index > prevIndex) {
                isNavCompact = true
            } else if (index < prevIndex) {
                isNavCompact = false
            } else {
                val delta = offset - prevOffset
                if (delta > NAV_SCROLL_COMPACT_THRESHOLD_PX) {
                    isNavCompact = true
                } else if (delta < -NAV_SCROLL_COMPACT_THRESHOLD_PX) {
                    isNavCompact = false
                }
            }
            prevIndex = index
            prevOffset = offset
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is DroidQuestEffect.TriggerHaptic -> {
                    if (state.progress.soundEnabled) {
                        when (effect.kind) {
                            HapticKind.LIGHT -> haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            HapticKind.SUCCESS, HapticKind.CELEBRATION ->
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            HapticKind.NONE -> Unit
                        }
                    }
                }
            }
        }
    }

    val trailSections = remember(state.highYieldOnly) {
        RoadmapCatalog.zones.mapNotNull { zone ->
            val rawZoneQuests = RoadmapCatalog.quests.filter { it.zoneId == zone.id }
            val filtered = if (state.highYieldOnly) {
                rawZoneQuests.filter { q -> q.highYield || q.isBoss || q.subtopics.any { it.highYield } }
            } else {
                rawZoneQuests
            }
            if (filtered.isEmpty()) null else Triple(zone, rawZoneQuests, filtered)
        }
    }

    val zoneScrollIndices = remember(trailSections) {
        val map = mutableMapOf<ZoneId, Int>()
        var currentIndex = 2
        for ((zone, _, visibleQuests) in trailSections) {
            map[zone.id] = currentIndex
            currentIndex += 1 + visibleQuests.size + 1
        }
        map
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val isWide = maxWidth >= sizing.wideBreakpoint
        val listBottomPadding = if (isWide) spacing.navContentBottomPaddingWide else spacing.navContentBottomPaddingMobile

        Column(modifier = Modifier.fillMaxSize()) {
            TopHud(
                totalXp = state.totalXp,
                rankInfo = state.rankInfo,
                streak = state.streak,
                currentDay = state.countdown.currentDay,
                targetDays = state.progress.targetDays,
                dueReviewCount = state.dueNow.size,
                saveStatus = state.saveStatus,
                soundEnabled = state.progress.soundEnabled,
                isDarkTheme = isDarkTheme,
                activeTab = state.activeTab,
                showTelemetryStrip = state.activeTab == ActiveTab.TRAIL,
                onSelectTab = { viewModel.onIntent(DroidQuestIntent.SelectTab(it)) },
                onToggleTheme = {
                    val next = if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK
                    viewModel.onIntent(DroidQuestIntent.SetThemeMode(next))
                },
                onToggleSound = { viewModel.onIntent(DroidQuestIntent.ToggleSound) },
                onOpenSettings = { viewModel.onIntent(DroidQuestIntent.SetSettingsOpen(true)) },
                onGoHome = {
                    viewModel.onIntent(DroidQuestIntent.SelectTab(ActiveTab.TRAIL))
                    coroutineScope.launch { trailListState.animateScrollToItem(0) }
                },
            )

            // Smooth Directional Animated Tab Transitions
            AnimatedContent(
                targetState = state.activeTab,
                modifier = Modifier.weight(1f).fillMaxSize(),
                transitionSpec = {
                    val forward = targetState.ordinal >= initialState.ordinal
                    val enterSlide = slideInHorizontally(
                        animationSpec = tween(durationMillis = motion.standardMs, easing = FastOutSlowInEasing),
                        initialOffsetX = { width -> if (forward) width / 16 else -width / 16 },
                    )
                    val exitSlide = slideOutHorizontally(
                        animationSpec = tween(durationMillis = motion.fastMs, easing = FastOutSlowInEasing),
                        targetOffsetX = { width -> if (forward) -width / 24 else width / 24 },
                    )
                    (fadeIn(tween(motion.standardMs)) + enterSlide) togetherWith
                        (fadeOut(tween(motion.fastMs)) + exitSlide)
                },
                label = "DroidQuestTabTransition",
            ) { tab ->
                when (tab) {
                    ActiveTab.TRAIL -> {
                        LazyColumn(
                            state = trailListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = listBottomPadding),
                        ) {
                            item(key = "hero-dashboard") {
                                HeroDashboard(
                                    completedCount = state.completedCount,
                                    totalCount = state.totalCount,
                                    overallPct = state.overallPct,
                                    daysLeft = state.countdown.daysLeft,
                                    dailyPace = state.countdown.dailyPace,
                                    pillarStats = state.pillarStats,
                                    nextRecommendedQuest = state.nextRecommendedQuest,
                                    topics = state.progress.topics,
                                    topDueReview = state.dueNow.firstOrNull(),
                                    dueReviewCount = state.dueNow.size,
                                    onTabChange = { viewModel.onIntent(DroidQuestIntent.SelectTab(it)) },
                                    onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                )
                            }

                            item(key = "zone-teleport-bar") {
                                ZoneTeleportBar(
                                    topics = state.progress.topics,
                                    selectedZoneId = selectedZoneId,
                                    highYieldOnly = state.highYieldOnly,
                                    onToggleHighYield = { viewModel.onIntent(DroidQuestIntent.ToggleHighYield) },
                                    onTeleportToZone = { zoneId ->
                                        selectedZoneId = zoneId
                                        val target = zoneScrollIndices[zoneId]
                                        if (target != null) {
                                            coroutineScope.launch { trailListState.animateScrollToItem(target) }
                                        }
                                    },
                                    modifier = Modifier.padding(bottom = spacing.lg),
                                )
                            }

                            trailSections.forEach { (zone, rawZoneQuests, visibleQuests) ->
                                val allZoneSubs = rawZoneQuests.flatMap { it.subtopics }
                                val doneZoneSubs = allZoneSubs.count { state.progress.topics[it.id]?.completed == true }
                                val zoneVisual = ext.zone(zone.id)

                                item(key = "zone-banner-${zone.id.name}") {
                                    ZoneBannerHeader(
                                        zone = zone,
                                        doneZoneSubs = doneZoneSubs,
                                        totalZoneSubs = allZoneSubs.size,
                                        modifier = Modifier.padding(bottom = spacing.md),
                                    )
                                }

                                itemsIndexed(
                                    items = visibleQuests,
                                    key = { _, quest -> "trail-quest-${quest.id}" },
                                ) { idx, quest ->
                                    TrailQuestNodeRow(
                                        quest = quest,
                                        indexInZone = idx,
                                        zoneVisual = zoneVisual,
                                        topics = state.progress.topics,
                                        isRecommended = quest.id == state.nextRecommendedQuest?.id,
                                        isLastInZone = idx == visibleQuests.lastIndex,
                                        onSelect = { viewModel.onIntent(DroidQuestIntent.SelectQuest(quest.id)) },
                                    )
                                }

                                item(key = "zone-spacer-${zone.id.name}") {
                                    Spacer(Modifier.height(spacing.dialogPadding))
                                }
                            }
                        }
                    }

                    ActiveTab.CHECKLIST -> {
                        LazyColumn(
                            state = checklistListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = listBottomPadding),
                        ) {
                            item(key = "checklist-view") {
                                ChecklistContent(
                                    topics = state.progress.topics,
                                    highYieldOnly = state.highYieldOnly,
                                    onToggleHighYield = { viewModel.onIntent(DroidQuestIntent.ToggleHighYield) },
                                    onToggleTopic = { viewModel.onIntent(DroidQuestIntent.ToggleTopicComplete(it)) },
                                    onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                    modifier = Modifier.padding(top = spacing.xl),
                                )
                            }
                        }
                    }

                    ActiveTab.REVIEWS -> {
                        LazyColumn(
                            state = reviewsListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = listBottomPadding),
                        ) {
                            item(key = "review-queue-view") {
                                ReviewQueueContent(
                                    dueNow = state.dueNow,
                                    upcoming = state.upcoming,
                                    onRecordReview = { id, rating ->
                                        viewModel.onIntent(DroidQuestIntent.RecordReview(id, rating))
                                    },
                                    onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                    modifier = Modifier.padding(top = spacing.xl),
                                )
                            }
                        }
                    }

                    ActiveTab.TROPHIES -> {
                        LazyColumn(
                            state = trophiesListState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = listBottomPadding),
                        ) {
                            item(key = "trophies-view") {
                                TrophiesContent(
                                    topics = state.progress.topics,
                                    heatmapDays = state.heatmapDays,
                                    totalXp = state.totalXp,
                                    streak = state.streak,
                                    totalReviewsCompleted = state.totalReviewsCompleted,
                                    onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                    modifier = Modifier.padding(top = spacing.xl),
                                )
                            }
                        }
                    }
                }
            }
        }

        // Revolut-style Floating Pill Bottom Navigation Bar (Mobile)
        if (!isWide) {
            FloatingBottomNavBar(
                activeTab = state.activeTab,
                dueReviewCount = state.dueNow.size,
                compact = isNavCompact,
                onTabChange = { viewModel.onIntent(DroidQuestIntent.SelectTab(it)) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        // Animated Slide-Over / Bottom Sheet Quest Detail
        AnimatedVisibility(
            visible = state.selectedQuest != null,
            enter = fadeIn(tween(motion.fastMs)) + if (isWide) {
                slideInHorizontally(tween(motion.sheetMs, easing = FastOutSlowInEasing)) { it / 3 }
            } else {
                slideInVertically(tween(motion.sheetMs, easing = FastOutSlowInEasing)) { it / 2 }
            },
            exit = fadeOut(tween(motion.fastMs)) + if (isWide) {
                slideOutHorizontally(tween(motion.fastMs, easing = FastOutSlowInEasing)) { it / 3 }
            } else {
                slideOutVertically(tween(motion.fastMs, easing = FastOutSlowInEasing)) { it / 2 }
            },
        ) {
            val activeQuest = state.selectedQuest ?: lastSelectedQuest
            if (activeQuest != null) {
                QuestDetailSheet(
                    quest = activeQuest,
                    topics = state.progress.topics,
                    note = state.progress.questNotes[activeQuest.id].orEmpty(),
                    customLinks = state.progress.customLinks[activeQuest.id].orEmpty(),
                    onClose = { viewModel.onIntent(DroidQuestIntent.SelectQuest(null)) },
                    onToggleTopic = { viewModel.onIntent(DroidQuestIntent.ToggleTopicComplete(it)) },
                    onRecordReview = { id, rating -> viewModel.onIntent(DroidQuestIntent.RecordReview(id, rating)) },
                    onToggleFlagReview = { viewModel.onIntent(DroidQuestIntent.ToggleFlagForReview(it)) },
                    onUpdateNote = { qid, note -> viewModel.onIntent(DroidQuestIntent.UpdateQuestNote(qid, note)) },
                    onAddCustomLink = { qid, title, url, type ->
                        viewModel.onIntent(DroidQuestIntent.AddCustomLink(qid, title, url, type))
                    },
                    onRemoveCustomLink = { qid, lid ->
                        viewModel.onIntent(DroidQuestIntent.RemoveCustomLink(qid, lid))
                    },
                    onOpenUrl = { url -> viewModel.onIntent(DroidQuestIntent.OpenExternalUrl(url)) },
                )
            }
        }

        // Animated Settings & JSON Backup Dialog
        AnimatedVisibility(
            visible = state.settingsOpen,
            enter = fadeIn(tween(motion.fastMs)) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(motion.standardMs, easing = FastOutSlowInEasing),
            ),
            exit = fadeOut(tween(motion.fastMs)) + scaleOut(
                targetScale = 0.96f,
                animationSpec = tween(motion.fastMs, easing = FastOutSlowInEasing),
            ),
        ) {
            SettingsDialog(
                startDate = state.progress.startDate,
                themeMode = state.progress.themeMode,
                saveStatus = state.saveStatus,
                storageLabel = state.storageLabel,
                dailyReminderEnabled = state.progress.dailyReminderEnabled,
                reminderHour = state.progress.reminderHour,
                supportsDailyReminders = state.supportsDailyReminders,
                supportsNativeFilePicker = state.supportsNativeFilePicker,
                onClose = { viewModel.onIntent(DroidQuestIntent.SetSettingsOpen(false)) },
                onSetThemeMode = { viewModel.onIntent(DroidQuestIntent.SetThemeMode(it)) },
                onUpdateStartDate = { viewModel.onIntent(DroidQuestIntent.UpdateStartDate(it)) },
                onSetDailyReminder = { enabled, hour ->
                    viewModel.onIntent(DroidQuestIntent.SetDailyReminder(enabled, hour))
                },
                onExportJson = { viewModel.onIntent(DroidQuestIntent.ExportBackupJson) },
                onPickJsonFile = { viewModel.onIntent(DroidQuestIntent.PickBackupJsonFile) },
                onImportJsonRaw = { raw -> viewModel.onIntent(DroidQuestIntent.ImportBackupJsonRaw(raw)) },
                onGetCurrentJson = { viewModel.getCurrentBackupJson() },
                onReset = { viewModel.onIntent(DroidQuestIntent.ResetProgress) },
            )
        }

        // Confetti Celebration Particle Layer
        CelebrationConfettiOverlay(
            triggerId = state.celebration.id,
            isBoss = state.celebration.isBoss,
        )

        // Floating Celebration / Status Toast
        val toast = state.toast
        FloatingCelebrationToast(
            message = toast?.message,
            tone = toast?.tone ?: ToastTone.EMERALD,
            modifier = Modifier
                .align(if (isWide) Alignment.BottomEnd else Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(
                    start = spacing.section,
                    end = spacing.section,
                    bottom = if (isWide) spacing.toastBottomOffsetWide else spacing.toastBottomOffsetMobile,
                ),
        )
    }
}
