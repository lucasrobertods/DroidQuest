package com.lrds.androidroadapp.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.lrds.androidroadapp.domain.model.PillarId
import com.lrds.androidroadapp.domain.model.ResourceType
import com.lrds.androidroadapp.domain.model.ZoneId

// ============================================================================
// Dark Mode — Minimal Monochrome Charcoal-Obsidian Surface Scale (Stitch Spec)
// ============================================================================
val MidnightAbyss = Color(0xFF0B0E14)        // Root canvas background
val MidnightLowest = Color(0xFF0E131D)       // Recessed inputs, tracks, code/prompt wells
val MidnightLow = Color(0xFF111620)          // Quiet secondary cards & collapsed rows
val MidnightContainer = Color(0xFF131822)    // Primary cards, hero dashboard, quest rows
val MidnightHigh = Color(0xFF181F2C)         // Elevated focal cards, tonal buttons, nav bar
val MidnightHighest = Color(0xFF20293A)      // Active neutral chips & hover states

val InkPrimary = Color(0xFFF4F6FA)           // Crisp off-white primary text
val InkSecondary = Color(0xFFB8C1D1)         // Readable cool-gray body copy
val InkMuted = Color(0xFF8A94A6)             // Metadata, captions, inactive nav
val InkSubtle = Color(0xFF5B6577)            // Placeholders, locked states
val HairlineStrong = Color(0xFF2B3548)       // Elevated outlines, floating nav, buttons
val HairlineSubtle = Color(0xFF1E2634)       // Default card borders & hairline dividers

// ============================================================================
// Light Mode — Minimal Monochrome Paper-Slate Surface Scale (Stitch Spec)
// ============================================================================
val PaperCanvas = Color(0xFFF8FAFC)          // Calm cool off-white canvas
val PaperLowest = Color(0xFFF1F5F9)          // Recessed search bar, tracks, prompt wells
val PaperLow = Color(0xFFF4F6FA)             // Quiet secondary surfaces
val PaperContainer = Color(0xFFFFFFFF)       // Crisp pure white primary cards
val PaperHigh = Color(0xFFFFFFFF)            // Elevated focal cards (Next Quest, active card)
val PaperHighest = Color(0xFFE2E8F0)         // Active neutral chip fill & hover states

val LightInkPrimary = Color(0xFF0F172A)      // Deep charcoal slate primary ink
val LightInkSecondary = Color(0xFF475569)    // Calm slate body copy
val LightInkMuted = Color(0xFF64748B)        // Eyebrows, subtitles, inactive nav
val LightInkSubtle = Color(0xFF94A3B8)       // Placeholders, locked states
val LightHairlineStrong = Color(0xFFCBD5E1)  // Elevated borders, floating nav, buttons
val LightHairlineSubtle = Color(0xFFE2E8F0)  // Delicate 1dp card borders & dividers

// ============================================================================
// Single Disciplined Primary Accent: Calm Emerald
// ============================================================================
val DroidEmerald = Color(0xFF22C55E)         // Dark mode primary accent
val DroidEmeraldLight = Color(0xFF4ADE80)
val DroidEmeraldSoft = Color(0xFF86EFAC)
val DroidEmeraldDeep = Color(0xFF051F10)     // High-contrast ink on solid Emerald buttons (dark)
val DroidEmeraldLedge = Color(0xFF168D42)
val DroidEmeraldContainer = Color(0xFF0E2419)

val LightEmerald = Color(0xFF16A34A)         // Light mode primary accent (WCAG AA+ on white)
val LightEmeraldDeep = Color(0xFFFFFFFF)     // Crisp white ink on solid Emerald CTA (light)
val LightEmeraldContainer = Color(0xFFECFDF5)
val LightOnEmeraldContainer = Color(0xFF15803D)

// Neutralized Secondary / Tertiary roles to preserve 90% monochrome calm
val RecallViolet = Color(0xFF94A3B8)
val RecallVioletLight = Color(0xFFCBD5E1)
val RecallVioletDeep = Color(0xFF0E131D)
val RecallVioletLedge = Color(0xFF232B3A)
val RecallVioletContainer = Color(0xFF181F2C)

val QuestGold = Color(0xFF22C55E)
val QuestGoldBright = Color(0xFF4ADE80)
val QuestGoldLight = Color(0xFFF4F6FA)
val QuestGoldDeep = Color(0xFF051F10)
val QuestGoldLedge = Color(0xFF168D42)
val QuestGoldContainer = Color(0xFF181F2C)

val BossCrimson = Color(0xFFE55365)          // Restrained destructive / reset accent (dark)
val BossCrimsonLight = Color(0xFFFCA5A5)
val BossCrimsonLedge = Color(0xFF9F1239)
val BossCrimsonContainer = Color(0xFF24141A)

val LightCrimson = Color(0xFFDC2626)         // Restrained destructive / reset accent (light)
val LightCrimsonContainer = Color(0xFFFEF2F2)
val LightOnCrimsonContainer = Color(0xFF991B1B)

val SkyTelemetry = Color(0xFF94A3B8)
val SkyTelemetryLedge = Color(0xFF232B3A)
val SkyTelemetryContainer = Color(0xFF131822)

val StreakFlame = Color(0xFF94A3B8)
val StreakFlameContainer = Color(0xFF131822)

val DroidQuestDarkColorScheme = darkColorScheme(
    primary = DroidEmerald,
    onPrimary = DroidEmeraldDeep,
    primaryContainer = DroidEmeraldContainer,
    onPrimaryContainer = DroidEmeraldSoft,

    secondary = RecallViolet,
    onSecondary = InkPrimary,
    secondaryContainer = RecallVioletContainer,
    onSecondaryContainer = InkSecondary,

    tertiary = QuestGold,
    onTertiary = QuestGoldDeep,
    tertiaryContainer = QuestGoldContainer,
    onTertiaryContainer = InkPrimary,

    error = BossCrimson,
    onError = Color(0xFFFFF1F2),
    errorContainer = BossCrimsonContainer,
    onErrorContainer = BossCrimsonLight,

    background = MidnightAbyss,
    onBackground = InkPrimary,
    surface = MidnightAbyss,
    onSurface = InkPrimary,
    surfaceVariant = MidnightHigh,
    onSurfaceVariant = InkMuted,

    surfaceContainerLowest = MidnightLowest,
    surfaceContainerLow = MidnightLow,
    surfaceContainer = MidnightContainer,
    surfaceContainerHigh = MidnightHigh,
    surfaceContainerHighest = MidnightHighest,

    outline = HairlineStrong,
    outlineVariant = HairlineSubtle,
    scrim = Color(0xD905070B),
)

val DroidQuestLightColorScheme = lightColorScheme(
    primary = LightEmerald,
    onPrimary = LightEmeraldDeep,
    primaryContainer = LightEmeraldContainer,
    onPrimaryContainer = LightOnEmeraldContainer,

    secondary = LightInkMuted,
    onSecondary = Color.White,
    secondaryContainer = PaperLowest,
    onSecondaryContainer = LightInkSecondary,

    tertiary = LightEmerald,
    onTertiary = Color.White,
    tertiaryContainer = LightEmeraldContainer,
    onTertiaryContainer = LightOnEmeraldContainer,

    error = LightCrimson,
    onError = Color.White,
    errorContainer = LightCrimsonContainer,
    onErrorContainer = LightOnCrimsonContainer,

    background = PaperCanvas,
    onBackground = LightInkPrimary,
    surface = PaperCanvas,
    onSurface = LightInkPrimary,
    surfaceVariant = PaperLowest,
    onSurfaceVariant = LightInkMuted,

    surfaceContainerLowest = PaperLowest,
    surfaceContainerLow = PaperLow,
    surfaceContainer = PaperContainer,
    surfaceContainerHigh = PaperHigh,
    surfaceContainerHighest = PaperHighest,

    outline = LightHairlineStrong,
    outlineVariant = LightHairlineSubtle,
    scrim = Color(0x660F172A),
)

@Immutable
data class ZoneVisualTokens(
    val primary: Color,
    val secondary: Color,
    val gradientEnd: Color,
    val border: Color,
    val softContainer: Color,
    val accentText: Color,
    val ledgeShadow: Color,
    val glow: Color,
    val onPrimaryBadge: Color = Color(0xFF051F10),
)

@Immutable
data class PillarVisualTokens(
    val accent: Color,
    val text: Color,
    val container: Color,
    val border: Color,
)

@Immutable
data class ResourceVisualTokens(
    val label: String,
    val text: Color,
    val container: Color,
    val border: Color,
)

@Immutable
data class DroidQuestExtendedColors(
    val zones: Map<ZoneId, ZoneVisualTokens>,
    val pillars: Map<PillarId, PillarVisualTokens>,
    val resources: Map<ResourceType, ResourceVisualTokens>,
    val emeraldLedge: Color = DroidEmeraldLedge,
    val violetLedge: Color = RecallVioletLedge,
    val goldBright: Color = DroidEmeraldLight,
    val goldLedge: Color = DroidEmeraldLedge,
    val crimsonLedge: Color = BossCrimsonLedge,
    val skyAccent: Color = InkMuted,
    val skyLedge: Color = HairlineStrong,
    val skyContainer: Color = MidnightContainer,
    val streakFlame: Color = InkMuted,
    val streakContainer: Color = MidnightContainer,
    val inkSecondary: Color = InkSecondary,
    val inkSubtle: Color = InkSubtle,
    val navPillSurface: Color = Color(0xFF141A24),
    val navPillBorder: Color = Color(0xFF263042),
    val heatmapZero: Color = Color(0xFF18202E),
    val heatmapLow: Color = Color(0xFF14452B),
    val heatmapMid: Color = Color(0xFF198744),
    val heatmapHigh: Color = Color(0xFF22C55E),
) {
    fun zone(id: ZoneId): ZoneVisualTokens = zones.getValue(id)
    fun pillar(id: PillarId): PillarVisualTokens = pillars.getValue(id)
    fun resource(type: ResourceType): ResourceVisualTokens = resources.getValue(type)
}

private val DarkZoneToken = ZoneVisualTokens(
    primary = DroidEmerald,
    secondary = DroidEmerald,
    gradientEnd = DroidEmerald,
    border = Color(0x5922C55E),
    softContainer = MidnightContainer,
    accentText = DroidEmerald,
    ledgeShadow = DroidEmeraldLedge,
    glow = Color(0x3322C55E),
    onPrimaryBadge = DroidEmeraldDeep,
)

private val LightZoneToken = ZoneVisualTokens(
    primary = LightEmerald,
    secondary = LightEmerald,
    gradientEnd = LightEmerald,
    border = Color(0x6616A34A),
    softContainer = PaperContainer,
    accentText = LightEmerald,
    ledgeShadow = LightOnEmeraldContainer,
    glow = Color(0x2616A34A),
    onPrimaryBadge = Color.White,
)

private val DarkPillarToken = PillarVisualTokens(
    accent = DroidEmerald,
    text = InkMuted,
    container = MidnightLowest,
    border = HairlineSubtle,
)

private val LightPillarToken = PillarVisualTokens(
    accent = LightEmerald,
    text = LightInkMuted,
    container = PaperLowest,
    border = LightHairlineSubtle,
)

private fun darkResourceToken(label: String) = ResourceVisualTokens(
    label = label,
    text = InkSecondary,
    container = MidnightHigh,
    border = HairlineSubtle,
)

private fun lightResourceToken(label: String) = ResourceVisualTokens(
    label = label,
    text = LightInkSecondary,
    container = PaperLowest,
    border = LightHairlineSubtle,
)

val DefaultDroidQuestExtendedColors = DroidQuestExtendedColors(
    zones = ZoneId.entries.associateWith { DarkZoneToken },
    pillars = PillarId.entries.associateWith { DarkPillarToken },
    resources = mapOf(
        ResourceType.YOUTUBE to darkResourceToken("YOUTUBE"),
        ResourceType.DOCS to darkResourceToken("DOCS"),
        ResourceType.PRACTICE to darkResourceToken("PRACTICE"),
        ResourceType.REPO to darkResourceToken("GITHUB"),
        ResourceType.ARTICLE to darkResourceToken("ARTICLE"),
        ResourceType.COURSE to darkResourceToken("COURSE"),
    ),
)

val LightDroidQuestExtendedColors = DroidQuestExtendedColors(
    zones = ZoneId.entries.associateWith { LightZoneToken },
    pillars = PillarId.entries.associateWith { LightPillarToken },
    resources = mapOf(
        ResourceType.YOUTUBE to lightResourceToken("YOUTUBE"),
        ResourceType.DOCS to lightResourceToken("DOCS"),
        ResourceType.PRACTICE to lightResourceToken("PRACTICE"),
        ResourceType.REPO to lightResourceToken("GITHUB"),
        ResourceType.ARTICLE to lightResourceToken("ARTICLE"),
        ResourceType.COURSE to lightResourceToken("COURSE"),
    ),
    emeraldLedge = LightOnEmeraldContainer,
    violetLedge = LightHairlineStrong,
    goldBright = LightEmerald,
    goldLedge = LightOnEmeraldContainer,
    crimsonLedge = LightOnCrimsonContainer,
    skyAccent = LightInkMuted,
    skyLedge = LightHairlineStrong,
    skyContainer = PaperContainer,
    streakFlame = LightInkMuted,
    streakContainer = PaperContainer,
    inkSecondary = LightInkSecondary,
    inkSubtle = LightInkSubtle,
    navPillSurface = Color(0xFFFFFFFF),
    navPillBorder = Color(0xFFCBD5E1),
    heatmapZero = Color(0xFFE2E8F0),
    heatmapLow = Color(0xFFBBF7D0),
    heatmapMid = Color(0xFF4ADE80),
    heatmapHigh = Color(0xFF16A34A),
)
