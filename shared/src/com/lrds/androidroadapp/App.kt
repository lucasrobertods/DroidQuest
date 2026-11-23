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
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.lrds.androidroadapp.data.HapticKind
import com.lrds.androidroadapp.data.ToastTone
import com.lrds.androidroadapp.designsystem.components.CelebrationConfettiOverlay
import com.lrds.androidroadapp.designsystem.components.FloatingBottomNavBar
import com.lrds.androidroadapp.designsystem.components.FloatingCelebrationToast
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.di.ensureKoinStarted
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
import com.lrds.androidroadapp.domain.model.AdventureZone
import com.lrds.androidroadapp.domain.model.QuestModule
import com.lrds.androidroadapp.domain.model.ThemeMode
import com.lrds.androidroadapp.domain.model.TopicProgress
import com.lrds.androidroadapp.domain.model.ZoneId
import com.lrds.androidroadapp.presentation.ActiveTab
import com.lrds.androidroadapp.presentation.DroidQuestEffect
import com.lrds.androidroadapp.presentation.DroidQuestIntent
import com.lrds.androidroadapp.presentation.DroidQuestUiState
import com.lrds.androidroadapp.presentation.DroidQuestViewModel
import com.lrds.androidroadapp.presentation.StableList
import com.lrds.androidroadapp.presentation.StableMap
import com.lrds.androidroadapp.presentation.checklist.ChecklistContent
import com.lrds.androidroadapp.presentation.hud.HeroDashboard
import com.lrds.androidroadapp.presentation.hud.TopHud
import com.lrds.androidroadapp.presentation.quest.QuestDetailSheet
import com.lrds.androidroadapp.presentation.review.ReviewQueueContent
import com.lrds.androidroadapp.presentation.settings.SettingsDialog
import com.lrds.androidroadapp.presentation.toStableList
import com.lrds.androidroadapp.presentation.toStableMap
import com.lrds.androidroadapp.presentation.toStableSet
import com.lrds.androidroadapp.presentation.trail.TrailQuestNodeRow
import com.lrds.androidroadapp.presentation.trail.ZoneBannerHeader
import com.lrds.androidroadapp.presentation.trail.ZoneTeleportBar
import com.lrds.androidroadapp.presentation.trophies.TrophiesContent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.koin.compose.viewmodel.koinViewModel

private const val NAV_SCROLL_COMPACT_THRESHOLD_PX = 18
private const val TAB_ENTER_SLIDE_DIVISOR = 16
private const val TAB_EXIT_SLIDE_DIVISOR = 24
private const val WIDE_SHEET_SLIDE_DIVISOR = 3
private const val DIALOG_ENTER_SCALE = 0.95f
private const val DIALOG_EXIT_SCALE = 0.96f

private data class TrailSection(
    val zone: AdventureZone,
    val rawZoneQuests: List<QuestModule>,
    val visibleQuests: List<QuestModule>,
)

@Composable
@DevelopmentEntryPoint
fun App() {
    App(onThemeChange = {})
}

@Preview
@Composable
private fun AppPreview() {
    App()
}

@Composable
fun App(
    onThemeChange: (isDark: Boolean) -> Unit,
    platformBackHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit = { _, _ -> },
) {
    remember { ensureKoinStarted() }
    DroidQuestAppRoot(
        onThemeChange = onThemeChange,
        platformBackHandler = platformBackHandler,
    )
}

@Composable
private fun DroidQuestAppRoot(
    onThemeChange: (isDark: Boolean) -> Unit,
    platformBackHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit,
    viewModel: DroidQuestViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val isDark = when (state.progress.themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    val currentOnThemeChange by rememberUpdatedState(onThemeChange)

    LaunchedEffect(isDark) {
        currentOnThemeChange(isDark)
    }

    DroidQuestTheme(darkTheme = isDark) {
        DroidQuestAppContent(
            state = state,
            effects = viewModel.effects,
            onIntent = viewModel::onIntent,
            onGetCurrentJson = viewModel::getCurrentBackupJson,
            isDarkTheme = isDark,
            platformBackHandler = platformBackHandler,
        )
    }
}

private data class TabListStates(
    val trail: LazyListState,
    val checklist: LazyListState,
    val reviews: LazyListState,
    val trophies: LazyListState,
) {
    fun forTab(tab: ActiveTab): LazyListState = when (tab) {
        ActiveTab.TRAIL -> trail
        ActiveTab.CHECKLIST -> checklist
        ActiveTab.REVIEWS -> reviews
        ActiveTab.TROPHIES -> trophies
    }
}

@Composable
private fun rememberTabListStates(): TabListStates {
    val trail = rememberLazyListState()
    val checklist = rememberLazyListState()
    val reviews = rememberLazyListState()
    val trophies = rememberLazyListState()
    return remember(trail, checklist, reviews, trophies) {
        TabListStates(trail = trail, checklist = checklist, reviews = reviews, trophies = trophies)
    }
}

@Composable
fun DroidQuestAppContent(
    state: DroidQuestUiState,
    effects: SharedFlow<DroidQuestEffect>,
    onIntent: (DroidQuestIntent) -> Unit,
    onGetCurrentJson: () -> String,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    platformBackHandler: @Composable (enabled: Boolean, onBack: () -> Unit) -> Unit = { _, _ -> },
) {
    val listStates = rememberTabListStates()
    val coroutineScope = rememberCoroutineScope()
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing

    var selectedZoneId by remember { mutableStateOf<ZoneId?>(ZoneId.EMERALD) }
    var isNavCompact by remember { mutableStateOf(false) }

    val canHandleBack = state.settingsOpen || state.selectedQuest != null || state.activeTab != ActiveTab.TRAIL
    platformBackHandler(canHandleBack) { handleBackPress(state, onIntent) }

    NavCompactScrollEffect(
        activeTab = state.activeTab,
        activeListState = listStates.forTab(state.activeTab),
        onCompactChange = { isNavCompact = it },
    )
    DroidQuestHapticEffect(effects = effects, soundEnabled = state.progress.soundEnabled)

    val trailSections = remember(state.highYieldOnly) { computeTrailSections(state.highYieldOnly).toStableList() }
    val zoneScrollIndices = remember(trailSections) { computeZoneScrollIndices(trailSections) }
    val stableTopics = remember(state.progress.topics) { state.progress.topics.toStableMap() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val isWide = maxWidth >= sizing.wideBreakpoint
        val listBottomPadding = if (isWide) {
            spacing.navContentBottomPaddingWide
        } else {
            spacing.navContentBottomPaddingMobile
        }

        Column(modifier = Modifier.fillMaxSize()) {
            DroidQuestTopBar(
                state = state,
                isDarkTheme = isDarkTheme,
                onIntent = onIntent,
                onGoHome = {
                    onIntent(DroidQuestIntent.SelectTab(ActiveTab.TRAIL))
                    coroutineScope.launch { listStates.trail.animateScrollToItem(0) }
                },
            )

            DroidQuestAnimatedTabContent(
                state = state,
                stableTopics = stableTopics,
                trailSections = trailSections,
                selectedZoneId = selectedZoneId,
                listStates = listStates,
                listBottomPadding = listBottomPadding,
                onIntent = onIntent,
                onTeleportToZone = { zoneId ->
                    selectedZoneId = zoneId
                    val target = zoneScrollIndices[zoneId]
                    if (target != null) {
                        coroutineScope.launch { listStates.trail.animateScrollToItem(target) }
                    }
                },
                modifier = Modifier.weight(1f).fillMaxSize(),
            )
        }

        DroidQuestOverlays(
            state = state,
            stableTopics = stableTopics,
            isWide = isWide,
            isNavCompact = isNavCompact,
            onIntent = onIntent,
            onGetCurrentJson = onGetCurrentJson,
        )
    }
}

private fun handleBackPress(
    state: DroidQuestUiState,
    onIntent: (DroidQuestIntent) -> Unit,
) {
    when {
        state.settingsOpen -> onIntent(DroidQuestIntent.SetSettingsOpen(false))
        state.selectedQuest != null -> onIntent(DroidQuestIntent.SelectQuest(null))
        state.activeTab != ActiveTab.TRAIL -> onIntent(DroidQuestIntent.SelectTab(ActiveTab.TRAIL))
    }
}

@Composable
private fun DroidQuestTopBar(
    state: DroidQuestUiState,
    isDarkTheme: Boolean,
    onIntent: (DroidQuestIntent) -> Unit,
    onGoHome: () -> Unit,
) {
    TopHud(
        totalXp = state.totalXp,
        rankInfo = state.rankInfo,
        streak = state.streak,
        currentDay = state.countdown.currentDay,
        targetDays = state.progress.targetDays,
        dueReviewCount = state.dueNow.size,
        soundEnabled = state.progress.soundEnabled,
        isDarkTheme = isDarkTheme,
        activeTab = state.activeTab,
        showTelemetryStrip = state.activeTab == ActiveTab.TRAIL,
        onSelectTab = { onIntent(DroidQuestIntent.SelectTab(it)) },
        onToggleTheme = {
            val next = if (isDarkTheme) ThemeMode.LIGHT else ThemeMode.DARK
            onIntent(DroidQuestIntent.SetThemeMode(next))
        },
        onToggleSound = { onIntent(DroidQuestIntent.ToggleSound) },
        onOpenSettings = { onIntent(DroidQuestIntent.SetSettingsOpen(true)) },
        onGoHome = onGoHome,
    )
}

@Composable
private fun NavCompactScrollEffect(
    activeTab: ActiveTab,
    activeListState: LazyListState,
    onCompactChange: (Boolean) -> Unit,
) {
    val currentOnCompactChange by rememberUpdatedState(onCompactChange)

    LaunchedEffect(activeTab) {
        currentOnCompactChange(false)
        var prevIndex = activeListState.firstVisibleItemIndex
        var prevOffset = activeListState.firstVisibleItemScrollOffset

        snapshotFlow {
            activeListState.firstVisibleItemIndex to activeListState.firstVisibleItemScrollOffset
        }.collect { (index, offset) ->
            val nextCompact = computeCompactState(index, offset, prevIndex, prevOffset)
            if (nextCompact != null) {
                currentOnCompactChange(nextCompact)
            }
            prevIndex = index
            prevOffset = offset
        }
    }
}

private fun computeCompactState(
    index: Int,
    offset: Int,
    prevIndex: Int,
    prevOffset: Int,
): Boolean? = when {
    index == 0 && offset < NAV_SCROLL_COMPACT_THRESHOLD_PX -> false
    index > prevIndex -> true
    index < prevIndex -> false
    offset - prevOffset > NAV_SCROLL_COMPACT_THRESHOLD_PX -> true
    offset - prevOffset < -NAV_SCROLL_COMPACT_THRESHOLD_PX -> false
    else -> null
}

@Composable
private fun DroidQuestHapticEffect(
    effects: SharedFlow<DroidQuestEffect>,
    soundEnabled: Boolean,
) {
    val haptics = LocalHapticFeedback.current
    val currentSoundEnabled by rememberUpdatedState(soundEnabled)

    LaunchedEffect(effects) {
        effects.collect { effect ->
            if (effect is DroidQuestEffect.TriggerHaptic && currentSoundEnabled) {
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

private fun computeTrailSections(highYieldOnly: Boolean): List<TrailSection> =
    RoadmapCatalog.zones.mapNotNull { zone ->
        val rawZoneQuests = RoadmapCatalog.quests.filter { it.zoneId == zone.id }
        val filtered = if (highYieldOnly) {
            rawZoneQuests.filter { q -> q.highYield || q.isBoss || q.subtopics.any { it.highYield } }
        } else {
            rawZoneQuests
        }
        if (filtered.isEmpty()) null else TrailSection(zone, rawZoneQuests, filtered)
    }

private fun computeZoneScrollIndices(trailSections: List<TrailSection>): Map<ZoneId, Int> {
    val map = mutableMapOf<ZoneId, Int>()
    var currentIndex = 2
    for (section in trailSections) {
        map[section.zone.id] = currentIndex
        currentIndex += 1 + section.visibleQuests.size + 1
    }
    return map
}

@Composable
private fun DroidQuestAnimatedTabContent(
    state: DroidQuestUiState,
    stableTopics: StableMap<String, TopicProgress>,
    trailSections: StableList<TrailSection>,
    selectedZoneId: ZoneId?,
    listStates: TabListStates,
    listBottomPadding: Dp,
    onIntent: (DroidQuestIntent) -> Unit,
    onTeleportToZone: (ZoneId) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val motion = DroidQuestTheme.motion

    AnimatedContent(
        targetState = state.activeTab,
        modifier = modifier,
        transitionSpec = {
            val forward = targetState.ordinal >= initialState.ordinal
            val enter = slideInHorizontally(tween(motion.standardMs, easing = FastOutSlowInEasing)) {
                if (forward) it / TAB_ENTER_SLIDE_DIVISOR else -it / TAB_ENTER_SLIDE_DIVISOR
            }
            val exit = slideOutHorizontally(tween(motion.fastMs, easing = FastOutSlowInEasing)) {
                if (forward) -it / TAB_EXIT_SLIDE_DIVISOR else it / TAB_EXIT_SLIDE_DIVISOR
            }
            (fadeIn(tween(motion.standardMs)) + enter) togetherWith (fadeOut(tween(motion.fastMs)) + exit)
        },
        label = "DroidQuestTabTransition",
    ) { tab ->
        when (tab) {
            ActiveTab.TRAIL -> TrailTabLazyColumn(
                state = state,
                stableTopics = stableTopics,
                trailSections = trailSections,
                selectedZoneId = selectedZoneId,
                listState = listStates.trail,
                bottomPadding = listBottomPadding,
                onIntent = onIntent,
                onTeleportToZone = onTeleportToZone,
            )
            ActiveTab.CHECKLIST -> SingleItemTabColumn(listStates.checklist, listBottomPadding, "checklist-view") {
                ChecklistContent(
                    topics = stableTopics,
                    highYieldOnly = state.highYieldOnly,
                    onToggleHighYield = { onIntent(DroidQuestIntent.ToggleHighYield) },
                    onToggleTopic = { onIntent(DroidQuestIntent.ToggleTopicComplete(it)) },
                    onSelectQuest = { onIntent(DroidQuestIntent.SelectQuest(it)) },
                    modifier = Modifier.padding(top = spacing.xl),
                )
            }
            ActiveTab.REVIEWS -> SingleItemTabColumn(listStates.reviews, listBottomPadding, "review-queue-view") {
                val stableDueNow = remember(state.dueNow) { state.dueNow.toStableList() }
                val stableUpcoming = remember(state.upcoming) { state.upcoming.toStableList() }
                ReviewQueueContent(
                    dueNow = stableDueNow,
                    upcoming = stableUpcoming,
                    onRecordReview = { id, rating -> onIntent(DroidQuestIntent.RecordReview(id, rating)) },
                    onSelectQuest = { onIntent(DroidQuestIntent.SelectQuest(it)) },
                    modifier = Modifier.padding(top = spacing.xl),
                )
            }
            ActiveTab.TROPHIES -> SingleItemTabColumn(listStates.trophies, listBottomPadding, "trophies-view") {
                val stableHeatmap = remember(state.heatmapDays) { state.heatmapDays.toStableList() }
                TrophiesContent(
                    topics = stableTopics,
                    heatmapDays = stableHeatmap,
                    totalXp = state.totalXp,
                    streak = state.streak,
                    totalReviewsCompleted = state.totalReviewsCompleted,
                    onSelectQuest = { onIntent(DroidQuestIntent.SelectQuest(it)) },
                    modifier = Modifier.padding(top = spacing.xl),
                )
            }
        }
    }
}

@Composable
private fun SingleItemTabColumn(
    listState: LazyListState,
    bottomPadding: Dp,
    itemKey: String,
    content: @Composable () -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomPadding),
    ) {
        item(key = itemKey) { content() }
    }
}

@Composable
private fun TrailTabLazyColumn(
    state: DroidQuestUiState,
    stableTopics: StableMap<String, TopicProgress>,
    trailSections: StableList<TrailSection>,
    selectedZoneId: ZoneId?,
    listState: LazyListState,
    bottomPadding: Dp,
    onIntent: (DroidQuestIntent) -> Unit,
    onTeleportToZone: (ZoneId) -> Unit,
) {
    val spacing = DroidQuestTheme.spacing

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomPadding),
    ) {
        item(key = "hero-dashboard") {
            val stablePillarStats = remember(state.pillarStats) { state.pillarStats.toStableList() }
            HeroDashboard(
                completedCount = state.completedCount,
                totalCount = state.totalCount,
                overallPct = state.overallPct,
                daysLeft = state.countdown.daysLeft,
                dailyPace = state.countdown.dailyPace,
                pillarStats = stablePillarStats,
                nextRecommendedQuest = state.nextRecommendedQuest,
                topics = stableTopics,
                topDueReview = state.dueNow.firstOrNull(),
                dueReviewCount = state.dueNow.size,
                onTabChange = { onIntent(DroidQuestIntent.SelectTab(it)) },
                onSelectQuest = { onIntent(DroidQuestIntent.SelectQuest(it)) },
            )
        }

        item(key = "zone-teleport-bar") {
            ZoneTeleportBar(
                topics = stableTopics,
                selectedZoneId = selectedZoneId,
                highYieldOnly = state.highYieldOnly,
                onToggleHighYield = { onIntent(DroidQuestIntent.ToggleHighYield) },
                onTeleportToZone = onTeleportToZone,
                modifier = Modifier.padding(bottom = spacing.lg),
            )
        }

        trailSections.forEach { section ->
            val allZoneSubs = section.rawZoneQuests.flatMap { it.subtopics }
            val doneZoneSubs = allZoneSubs.count { stableTopics[it.id]?.completed == true }

            item(key = "zone-banner-${section.zone.id.name}") {
                ZoneBannerHeader(
                    zone = section.zone,
                    doneZoneSubs = doneZoneSubs,
                    totalZoneSubs = allZoneSubs.size,
                    modifier = Modifier.padding(bottom = spacing.md),
                )
            }

            itemsIndexed(
                items = section.visibleQuests,
                key = { _, quest -> "trail-quest-${quest.id}" },
            ) { idx, quest ->
                TrailQuestNodeRow(
                    quest = quest,
                    indexInZone = idx,
                    topics = stableTopics,
                    isRecommended = quest.id == state.nextRecommendedQuest?.id,
                    isLastInZone = idx == section.visibleQuests.lastIndex,
                    onSelect = { onIntent(DroidQuestIntent.SelectQuest(quest.id)) },
                )
            }

            item(key = "zone-spacer-${section.zone.id.name}") {
                Spacer(Modifier.height(spacing.dialogPadding))
            }
        }
    }
}

@Composable
private fun BoxScope.DroidQuestOverlays(
    state: DroidQuestUiState,
    stableTopics: StableMap<String, TopicProgress>,
    isWide: Boolean,
    isNavCompact: Boolean,
    onIntent: (DroidQuestIntent) -> Unit,
    onGetCurrentJson: () -> String,
) {
    val spacing = DroidQuestTheme.spacing

    if (!isWide) {
        FloatingBottomNavBar(
            activeTab = state.activeTab,
            dueReviewCount = state.dueNow.size,
            compact = isNavCompact,
            onTabChange = { onIntent(DroidQuestIntent.SelectTab(it)) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    AnimatedQuestDetailOverlay(
        state = state,
        stableTopics = stableTopics,
        isWide = isWide,
        onIntent = onIntent,
    )

    AnimatedSettingsOverlay(
        state = state,
        onIntent = onIntent,
        onGetCurrentJson = onGetCurrentJson,
    )

    CelebrationConfettiOverlay(
        triggerId = state.celebration.id,
        isBoss = state.celebration.isBoss,
    )

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

@Composable
private fun AnimatedQuestDetailOverlay(
    state: DroidQuestUiState,
    stableTopics: StableMap<String, TopicProgress>,
    isWide: Boolean,
    onIntent: (DroidQuestIntent) -> Unit,
) {
    val motion = DroidQuestTheme.motion
    var lastSelectedQuest by remember { mutableStateOf<QuestModule?>(null) }
    if (state.selectedQuest != null) {
        lastSelectedQuest = state.selectedQuest
    }

    AnimatedVisibility(
        visible = state.selectedQuest != null,
        enter = fadeIn(tween(motion.fastMs)) + if (isWide) {
            slideInHorizontally(tween(motion.sheetMs, easing = FastOutSlowInEasing)) { it / WIDE_SHEET_SLIDE_DIVISOR }
        } else {
            slideInVertically(tween(motion.sheetMs, easing = FastOutSlowInEasing)) { it / 2 }
        },
        exit = fadeOut(tween(motion.fastMs)) + if (isWide) {
            slideOutHorizontally(tween(motion.fastMs, easing = FastOutSlowInEasing)) { it / WIDE_SHEET_SLIDE_DIVISOR }
        } else {
            slideOutVertically(tween(motion.fastMs, easing = FastOutSlowInEasing)) { it / 2 }
        },
    ) {
        val activeQuest = state.selectedQuest ?: lastSelectedQuest
        if (activeQuest != null) {
            val rawCustomLinks = state.progress.customLinks[activeQuest.id].orEmpty()
            val stableCustomLinks = remember(rawCustomLinks) { rawCustomLinks.toStableList() }
            QuestDetailSheet(
                quest = activeQuest,
                topics = stableTopics,
                note = state.progress.questNotes[activeQuest.id].orEmpty(),
                customLinks = stableCustomLinks,
                onClose = { onIntent(DroidQuestIntent.SelectQuest(null)) },
                onToggleTopic = { onIntent(DroidQuestIntent.ToggleTopicComplete(it)) },
                onRecordReview = { id, rating -> onIntent(DroidQuestIntent.RecordReview(id, rating)) },
                onToggleFlagReview = { onIntent(DroidQuestIntent.ToggleFlagForReview(it)) },
                onUpdateNote = { qid, note -> onIntent(DroidQuestIntent.UpdateQuestNote(qid, note)) },
                onAddCustomLink = { qid, title, url, type ->
                    onIntent(DroidQuestIntent.AddCustomLink(qid, title, url, type))
                },
                onRemoveCustomLink = { qid, lid -> onIntent(DroidQuestIntent.RemoveCustomLink(qid, lid)) },
                onOpenUrl = { url -> onIntent(DroidQuestIntent.OpenExternalUrl(url)) },
            )
        }
    }
}

@Composable
private fun AnimatedSettingsOverlay(
    state: DroidQuestUiState,
    onIntent: (DroidQuestIntent) -> Unit,
    onGetCurrentJson: () -> String,
) {
    val motion = DroidQuestTheme.motion

    AnimatedVisibility(
        visible = state.settingsOpen,
        enter = fadeIn(tween(motion.fastMs)) + scaleIn(
            initialScale = DIALOG_ENTER_SCALE,
            animationSpec = tween(motion.standardMs, easing = FastOutSlowInEasing),
        ),
        exit = fadeOut(tween(motion.fastMs)) + scaleOut(
            targetScale = DIALOG_EXIT_SCALE,
            animationSpec = tween(motion.fastMs, easing = FastOutSlowInEasing),
        ),
    ) {
        val stableStudyDays = remember(state.progress.studyReminderDays) {
            state.progress.studyReminderDays.toStableSet()
        }
        SettingsDialog(
            startDate = state.progress.startDate,
            themeMode = state.progress.themeMode,
            saveStatus = state.saveStatus,
            storageLabel = state.storageLabel,
            dailyReminderEnabled = state.progress.dailyReminderEnabled,
            reminderHour = state.progress.reminderHour,
            studyReminderEnabled = state.progress.studyReminderEnabled,
            studyReminderDays = stableStudyDays,
            studyReminderHour = state.progress.studyReminderHour,
            studyReminderMinute = state.progress.studyReminderMinute,
            supportsDailyReminders = state.supportsDailyReminders,
            supportsNativeFilePicker = state.supportsNativeFilePicker,
            onClose = { onIntent(DroidQuestIntent.SetSettingsOpen(false)) },
            onSetThemeMode = { onIntent(DroidQuestIntent.SetThemeMode(it)) },
            onUpdateStartDate = { onIntent(DroidQuestIntent.UpdateStartDate(it)) },
            onSetDailyReminder = { enabled, hour -> onIntent(DroidQuestIntent.SetDailyReminder(enabled, hour)) },
            onSetStudySchedule = { enabled, days, hour, minute ->
                onIntent(DroidQuestIntent.SetStudySchedule(enabled, days, hour, minute))
            },
            onExportJson = { onIntent(DroidQuestIntent.ExportBackupJson) },
            onPickJsonFile = { onIntent(DroidQuestIntent.PickBackupJsonFile) },
            onImportJsonRaw = { raw -> onIntent(DroidQuestIntent.ImportBackupJsonRaw(raw)) },
            onGetCurrentJson = onGetCurrentJson,
            onReset = { onIntent(DroidQuestIntent.ResetProgress) },
        )
    }
}
