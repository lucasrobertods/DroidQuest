package com.lrds.androidroadapp.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.lrds.androidroadapp.resources.Res
import com.lrds.androidroadapp.resources.jetbrains_mono_bold
import com.lrds.androidroadapp.resources.jetbrains_mono_medium
import com.lrds.androidroadapp.resources.nunito_bold
import com.lrds.androidroadapp.resources.nunito_extrabold
import com.lrds.androidroadapp.resources.nunito_regular
import com.lrds.androidroadapp.resources.nunito_semibold
import org.jetbrains.compose.resources.Font

@Immutable
data class DroidQuestMonoTypography(
    val kpiLarge: TextStyle,
    val metric: TextStyle,
    val badge: TextStyle,
    val micro: TextStyle,
)

@Composable
fun rememberDisplayFontFamily(): FontFamily = FontFamily(
    Font(Res.font.nunito_semibold, FontWeight.SemiBold),
    Font(Res.font.nunito_bold, FontWeight.Bold),
    Font(Res.font.nunito_extrabold, FontWeight.ExtraBold),
)

@Composable
fun rememberBodyFontFamily(): FontFamily = FontFamily(
    Font(Res.font.nunito_regular, FontWeight.Normal),
    Font(Res.font.nunito_semibold, FontWeight.SemiBold),
    Font(Res.font.nunito_bold, FontWeight.Bold),
    Font(Res.font.nunito_extrabold, FontWeight.ExtraBold),
)

@Composable
fun rememberMonoFontFamily(): FontFamily = FontFamily(
    Font(Res.font.jetbrains_mono_medium, FontWeight.Medium),
    Font(Res.font.jetbrains_mono_bold, FontWeight.Bold),
)

/**
 * Clean geometric sans + tabular monospace hierarchy matching the refined Stitch spec:
 * - Nunito (Headlines & UI copy): crisp, calm, high-legibility dark-mode reading
 * - JetBrains Mono (Telemetry & KPIs): tabular figures for %, Day X/100, XP, counts, intervals
 */
@Composable
fun droidQuestTypography(): Typography {
    val display = rememberDisplayFontFamily()
    val body = rememberBodyFontFamily()
    return buildDroidQuestTypography(display = display, body = body)
}

private fun buildDroidQuestTypography(display: FontFamily, body: FontFamily): Typography = Typography(
    displaySmall = textStyle(
        fontFamily = display,
        fontWeight = FontWeight.ExtraBold,
        fontSizeSp = 32,
        lineHeightSp = 36,
        letterSpacingSp = -0.6f,
    ),
    headlineLarge = textStyle(display, FontWeight.Bold, fontSizeSp = 24, lineHeightSp = 30, letterSpacingSp = -0.4f),
    headlineMedium = textStyle(display, FontWeight.Bold, fontSizeSp = 20, lineHeightSp = 26, letterSpacingSp = -0.3f),
    headlineSmall = textStyle(display, FontWeight.Bold, fontSizeSp = 18, lineHeightSp = 24, letterSpacingSp = -0.2f),
    titleLarge = textStyle(display, FontWeight.Bold, fontSizeSp = 16, lineHeightSp = 22, letterSpacingSp = -0.15f),
    titleMedium = textStyle(body, FontWeight.Bold, fontSizeSp = 15, lineHeightSp = 20, letterSpacingSp = -0.1f),
    titleSmall = textStyle(body, FontWeight.Bold, fontSizeSp = 13, lineHeightSp = 18),
    bodyLarge = textStyle(body, FontWeight.SemiBold, fontSizeSp = 14, lineHeightSp = 20),
    bodyMedium = textStyle(body, FontWeight.Normal, fontSizeSp = 13, lineHeightSp = 19),
    bodySmall = textStyle(body, FontWeight.Normal, fontSizeSp = 12, lineHeightSp = 17),
    labelLarge = textStyle(body, FontWeight.Bold, fontSizeSp = 13, lineHeightSp = 16, letterSpacingSp = 0f),
    labelMedium = textStyle(body, FontWeight.SemiBold, fontSizeSp = 11, lineHeightSp = 14, letterSpacingSp = 0.3f),
    labelSmall = textStyle(body, FontWeight.Bold, fontSizeSp = 10, lineHeightSp = 13, letterSpacingSp = 0.7f),
)

private fun textStyle(
    fontFamily: FontFamily,
    fontWeight: FontWeight,
    fontSizeSp: Int,
    lineHeightSp: Int,
    letterSpacingSp: Float? = null,
): TextStyle = if (letterSpacingSp != null) {
    TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSizeSp.sp,
        lineHeight = lineHeightSp.sp,
        letterSpacing = letterSpacingSp.sp,
    )
} else {
    TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = fontSizeSp.sp,
        lineHeight = lineHeightSp.sp,
    )
}

@Composable
fun droidQuestMonoTypography(): DroidQuestMonoTypography {
    val mono = rememberMonoFontFamily()
    return DroidQuestMonoTypography(
        kpiLarge = TextStyle(
            fontFamily = mono,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            letterSpacing = (-0.5).sp,
            fontFeatureSettings = "tnum",
        ),
        metric = TextStyle(
            fontFamily = mono,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontFeatureSettings = "tnum",
        ),
        badge = TextStyle(
            fontFamily = mono,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontFeatureSettings = "tnum",
        ),
        micro = TextStyle(
            fontFamily = mono,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            fontFeatureSettings = "tnum",
        ),
    )
}
