package com.lrds.androidroadapp.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LocalDroidQuestColors = staticCompositionLocalOf { DefaultDroidQuestExtendedColors }
val LocalDroidQuestSpacing = staticCompositionLocalOf { DefaultDroidQuestSpacing }
val LocalDroidQuestSizing = staticCompositionLocalOf { DefaultDroidQuestSizing }
val LocalDroidQuestBorders = staticCompositionLocalOf { DefaultDroidQuestBorders }
val LocalDroidQuestMotion = staticCompositionLocalOf { DefaultDroidQuestMotion }

val LocalDroidQuestMono = staticCompositionLocalOf {
    DroidQuestMonoTypography(
        kpiLarge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp),
        metric = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp),
        badge = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp),
        micro = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 10.sp),
    )
}

object DroidQuestTheme {
    val colors: DroidQuestExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalDroidQuestColors.current

    val mono: DroidQuestMonoTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalDroidQuestMono.current

    val spacing: DroidQuestSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalDroidQuestSpacing.current

    val sizing: DroidQuestSizing
        @Composable
        @ReadOnlyComposable
        get() = LocalDroidQuestSizing.current

    val borders: DroidQuestBorders
        @Composable
        @ReadOnlyComposable
        get() = LocalDroidQuestBorders.current

    val motion: DroidQuestMotion
        @Composable
        @ReadOnlyComposable
        get() = LocalDroidQuestMotion.current
}

@Composable
fun DroidQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val typography = droidQuestTypography()
    val monoTypography = droidQuestMonoTypography()
    val colorScheme = if (darkTheme) DroidQuestDarkColorScheme else DroidQuestLightColorScheme
    val extendedColors = if (darkTheme) DefaultDroidQuestExtendedColors else LightDroidQuestExtendedColors

    CompositionLocalProvider(
        LocalDroidQuestColors provides extendedColors,
        LocalDroidQuestMono provides monoTypography,
        LocalDroidQuestSpacing provides DefaultDroidQuestSpacing,
        LocalDroidQuestSizing provides DefaultDroidQuestSizing,
        LocalDroidQuestBorders provides DefaultDroidQuestBorders,
        LocalDroidQuestMotion provides DefaultDroidQuestMotion,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = DroidQuestShapes,
            content = content,
        )
    }
}
