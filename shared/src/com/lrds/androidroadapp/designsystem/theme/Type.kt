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

    return Typography(
        displaySmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            lineHeight = 36.sp,
            letterSpacing = (-0.6).sp,
        ),
        headlineLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            letterSpacing = (-0.4).sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 26.sp,
            letterSpacing = (-0.3).sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = (-0.2).sp,
        ),
        titleLarge = TextStyle(
            fontFamily = display,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.15).sp,
        ),
        titleMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            letterSpacing = (-0.1).sp,
        ),
        titleSmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 18.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            lineHeight = 19.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 17.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.3.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = body,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            lineHeight = 13.sp,
            letterSpacing = 0.7.sp,
        ),
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
