package com.lrds.androidroadapp.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Semantic spacing tokens for DroidQuest's Minimal Monochrome Design System.
 */
@Immutable
data class DroidQuestSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 6.dp,
    val md: Dp = 8.dp,
    val base: Dp = 10.dp,
    val lg: Dp = 12.dp,
    val xl: Dp = 14.dp,
    val xxl: Dp = 16.dp,
    val section: Dp = 18.dp,
    val sheetSection: Dp = 22.dp,
    val screenHorizontal: Dp = 16.dp,
    val cardPadding: Dp = 16.dp,
    val cardPaddingCompact: Dp = 14.dp,
    val cardPaddingSpacious: Dp = 18.dp,
    val dialogPadding: Dp = 20.dp,
    val emptyStatePadding: Dp = 28.dp,
    val navContentBottomPaddingMobile: Dp = 112.dp,
    val navContentBottomPaddingWide: Dp = 48.dp,
    val toastBottomOffsetMobile: Dp = 88.dp,
    val toastBottomOffsetWide: Dp = 18.dp,
)

/**
 * Semantic sizing, breakpoint, and elevation tokens.
 */
@Immutable
data class DroidQuestSizing(
    val maxContentWidth: Dp = 1040.dp,
    val wideBreakpoint: Dp = 820.dp,
    val mediumBreakpoint: Dp = 560.dp,
    val sheetWideBreakpoint: Dp = 780.dp,
    val sheetMaxWidth: Dp = 640.dp,
    val dialogMaxWidth: Dp = 520.dp,
    val dialogMaxHeight: Dp = 600.dp,
    val navPillMaxWidth: Dp = 460.dp,
    val iconXs: Dp = 12.dp,
    val iconSm: Dp = 14.dp,
    val iconMd: Dp = 16.dp,
    val iconLg: Dp = 18.dp,
    val iconXl: Dp = 20.dp,
    val iconEmptyState: Dp = 34.dp,
    val appBadgeSize: Dp = 34.dp,
    val ghostButtonSize: Dp = 34.dp,
    val iconBoxSize: Dp = 38.dp,
    val trophyIconBoxSize: Dp = 42.dp,
    val checkCircleSize: Dp = 22.dp,
    val trailRailWidth: Dp = 26.dp,
    val trailNodeSize: Dp = 22.dp,
    val trailPendingNodeSize: Dp = 18.dp,
    val trailActiveDotSize: Dp = 8.dp,
    val dragHandleWidth: Dp = 36.dp,
    val dragHandleHeight: Dp = 4.dp,
    val progressBarThin: Dp = 4.dp,
    val progressBarMedium: Dp = 5.dp,
    val progressBarStandard: Dp = 6.dp,
    val heatmapCellHeight: Dp = 34.dp,
    val heatmapLegendSwatch: Dp = 11.dp,
    val notesMinHeight: Dp = 110.dp,
    val pasteBoxMinHeight: Dp = 100.dp,
    val pasteBoxMaxHeight: Dp = 160.dp,
    val elevationNavPill: Dp = 14.dp,
    val elevationToast: Dp = 12.dp,
    val elevationModal: Dp = 24.dp,
)

/**
 * Semantic hairline border widths.
 */
@Immutable
data class DroidQuestBorders(
    val hairline: Dp = 1.dp,
    val medium: Dp = 1.5.dp,
    val emphasis: Dp = 2.dp,
)

/**
 * Semantic motion durations (milliseconds) for transitions & micro-interactions.
 */
@Immutable
data class DroidQuestMotion(
    val pressMs: Int = 80,
    val fastMs: Int = 180,
    val standardMs: Int = 240,
    val sheetMs: Int = 280,
    val progressMs: Int = 450,
    val celebrationMs: Int = 1150,
)

val DefaultDroidQuestSpacing = DroidQuestSpacing()
val DefaultDroidQuestSizing = DroidQuestSizing()
val DefaultDroidQuestBorders = DroidQuestBorders()
val DefaultDroidQuestMotion = DroidQuestMotion()
