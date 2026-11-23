package com.lrds.androidroadapp

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.data.HapticKind
import com.lrds.androidroadapp.data.ToastTone
import com.lrds.androidroadapp.designsystem.components.CelebrationConfettiOverlay
import com.lrds.androidroadapp.designsystem.components.FloatingBottomNavBar
import com.lrds.androidroadapp.designsystem.components.FloatingCelebrationToast
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.di.ensureKoinStarted
import com.lrds.androidroadapp.domain.curriculum.RoadmapCatalog
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

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    App(onThemeChanged = {})
}

@Composable
fun App(
    onThemeChanged: (isDark: Boolean) -> Unit,
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
        )
    }
}

@Composable
fun DroidQuestAppContent(
    viewModel: DroidQuestViewModel,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
) {
    val state by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val ext = DroidQuestTheme.colors
    var selectedZoneId by remember { mutableStateOf<ZoneId?>(ZoneId.EMERALD) }

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

    // Scroll to top cleanly when switching tabs
    LaunchedEffect(state.activeTab) {
        listState.scrollToItem(0)
    }

    // Precompute zone item indices in LazyColumn for smooth Zone Teleport navigation
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
        // Index 0 = HeroDashboard, Index 1 = ZoneTeleportBar
        var currentIndex = 2
        for ((zone, _, visibleQuests) in trailSections) {
            map[zone.id] = currentIndex
            currentIndex += 1 + visibleQuests.size + 1 // header + quest rows + bottom spacer
        }
        map
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        val isWide = maxWidth >= 820.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Compact Top Header + Unified Telemetry Strip on Trail
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
                    coroutineScope.launch { listState.animateScrollToItem(0) }
                },
            )

            // Main Scrollable Experience
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxSize(),
                contentPadding = PaddingValues(bottom = if (isWide) 48.dp else 112.dp),
            ) {
                when (state.activeTab) {
                    ActiveTab.TRAIL -> {
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
                                        coroutineScope.launch { listState.animateScrollToItem(target) }
                                    }
                                },
                                modifier = Modifier.padding(bottom = 12.dp),
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
                                    modifier = Modifier.padding(bottom = 8.dp),
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
                                Spacer(Modifier.height(20.dp))
                            }
                        }
                    }

                    ActiveTab.CHECKLIST -> {
                        item(key = "checklist-view") {
                            ChecklistContent(
                                topics = state.progress.topics,
                                highYieldOnly = state.highYieldOnly,
                                onToggleHighYield = { viewModel.onIntent(DroidQuestIntent.ToggleHighYield) },
                                onToggleTopic = { viewModel.onIntent(DroidQuestIntent.ToggleTopicComplete(it)) },
                                onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                modifier = Modifier.padding(top = 14.dp),
                            )
                        }
                    }

                    ActiveTab.REVIEWS -> {
                        item(key = "review-queue-view") {
                            ReviewQueueContent(
                                dueNow = state.dueNow,
                                upcoming = state.upcoming,
                                onRecordReview = { id, rating ->
                                    viewModel.onIntent(DroidQuestIntent.RecordReview(id, rating))
                                },
                                onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                modifier = Modifier.padding(top = 14.dp),
                            )
                        }
                    }

                    ActiveTab.TROPHIES -> {
                        item(key = "trophies-view") {
                            TrophiesContent(
                                topics = state.progress.topics,
                                heatmapDays = state.heatmapDays,
                                totalXp = state.totalXp,
                                streak = state.streak,
                                totalReviewsCompleted = state.totalReviewsCompleted,
                                onSelectQuest = { viewModel.onIntent(DroidQuestIntent.SelectQuest(it)) },
                                modifier = Modifier.padding(top = 14.dp),
                            )
                        }
                    }
                }
            }
        }

        // Mobile Floating Pill Bottom Navigation Bar
        if (!isWide) {
            FloatingBottomNavBar(
                activeTab = state.activeTab,
                dueReviewCount = state.dueNow.size,
                onTabChange = { viewModel.onIntent(DroidQuestIntent.SelectTab(it)) },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }

        // Slide-over / Bottom Sheet Quest Detail
        val selectedQuest = state.selectedQuest
        if (selectedQuest != null) {
            QuestDetailSheet(
                quest = selectedQuest,
                topics = state.progress.topics,
                note = state.progress.questNotes[selectedQuest.id].orEmpty(),
                customLinks = state.progress.customLinks[selectedQuest.id].orEmpty(),
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

        // Settings & JSON Backup Dialog
        if (state.settingsOpen) {
            SettingsDialog(
                startDate = state.progress.startDate,
                themeMode = state.progress.themeMode,
                saveStatus = state.saveStatus,
                storageLabel = state.storageLabel,
                dailyReminderEnabled = state.progress.dailyReminderEnabled,
                reminderHour = state.progress.reminderHour,
                studyReminderEnabled = state.progress.studyReminderEnabled,
                studyReminderDays = state.progress.studyReminderDays,
                studyReminderHour = state.progress.studyReminderHour,
                studyReminderMinute = state.progress.studyReminderMinute,
                supportsDailyReminders = state.supportsDailyReminders,
                supportsNativeFilePicker = state.supportsNativeFilePicker,
                onClose = { viewModel.onIntent(DroidQuestIntent.SetSettingsOpen(false)) },
                onSetThemeMode = { viewModel.onIntent(DroidQuestIntent.SetThemeMode(it)) },
                onUpdateStartDate = { viewModel.onIntent(DroidQuestIntent.UpdateStartDate(it)) },
                onSetDailyReminder = { enabled, hour ->
                    viewModel.onIntent(DroidQuestIntent.SetDailyReminder(enabled, hour))
                },
                onSetStudySchedule = { enabled, days, hour, minute ->
                    viewModel.onIntent(DroidQuestIntent.SetStudySchedule(enabled, days, hour, minute))
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
                    start = 18.dp,
                    end = 18.dp,
                    bottom = if (isWide) 18.dp else 88.dp,
                ),
        )
    }
}
