package com.lrds.androidroadapp.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lrds.androidroadapp.data.ToastTone
import com.lrds.androidroadapp.designsystem.icons.DroidQuestIcons
import com.lrds.androidroadapp.designsystem.theme.DroidQuestCornerShapes
import com.lrds.androidroadapp.designsystem.theme.DroidQuestTheme
import com.lrds.androidroadapp.domain.model.ZoneId
import com.lrds.androidroadapp.presentation.ActiveTab
import com.lrds.androidroadapp.resources.Res
import com.lrds.androidroadapp.resources.zone_1_emerald
import com.lrds.androidroadapp.resources.zone_2_sapphire
import com.lrds.androidroadapp.resources.zone_3_ruby
import com.lrds.androidroadapp.resources.zone_4_golden
import com.lrds.androidroadapp.resources.zone_5_cosmic
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun zoneBannerDrawable(zoneId: ZoneId): DrawableResource = when (zoneId) {
    ZoneId.EMERALD -> Res.drawable.zone_1_emerald
    ZoneId.SAPPHIRE -> Res.drawable.zone_2_sapphire
    ZoneId.RUBY -> Res.drawable.zone_3_ruby
    ZoneId.GOLDEN -> Res.drawable.zone_4_golden
    ZoneId.COSMIC -> Res.drawable.zone_5_cosmic
}

/**
 * Clean monochrome flat progress bar with single-accent Emerald fill.
 */
@Composable
fun CalmProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    height: Dp = DroidQuestTheme.sizing.progressBarStandard,
    minVisibleFraction: Float = 0.03f,
) {
    val motion = DroidQuestTheme.motion
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = if (clamped > 0f) maxOf(clamped, minVisibleFraction) else 0f,
        animationSpec = tween(durationMillis = motion.progressMs, easing = FastOutSlowInEasing),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(DroidQuestCornerShapes.FullPill)
            .background(trackColor),
    ) {
        if (animated > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .clip(DroidQuestCornerShapes.FullPill)
                    .background(fillColor),
            )
        }
    }
}

/**
 * Solid Emerald primary CTA button (the single high-emphasis action per screen).
 */
@Composable
fun PrimaryActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = DroidQuestCornerShapes.TactileButton,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = DroidQuestTheme.spacing.cardPadding,
        vertical = DroidQuestTheme.spacing.lg,
    ),
    content: @Composable RowScope.() -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    Surface(
        color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = shape,
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterHorizontally),
            content = content,
        )
    }
}

/**
 * Calm neutral tonal button with subtle hairline border.
 */
@Composable
fun SecondaryTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    shape: Shape = DroidQuestCornerShapes.TactileButton,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = DroidQuestTheme.spacing.cardPaddingCompact,
        vertical = DroidQuestTheme.spacing.base,
    ),
    content: @Composable RowScope.() -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    Surface(
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        contentColor = contentColor,
        shape = shape,
        border = BorderStroke(borders.hairline, borderColor),
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.sm, Alignment.CenterHorizontally),
            content = content,
        )
    }
}

/**
 * Revolut-style Floating Pill Bottom Navigation Bar.
 * Remains pinned and reachable on scroll while compacting smoothly to icon-only on scroll-down
 * and restoring text labels on scroll-up.
 */
@Composable
fun FloatingBottomNavBar(
    activeTab: ActiveTab,
    dueReviewCount: Int,
    compact: Boolean,
    onTabChange: (ActiveTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val motion = DroidQuestTheme.motion

    val outerHorizontalPadding by animateDpAsState(
        targetValue = if (compact) spacing.navContentBottomPaddingWide else spacing.section,
        animationSpec = tween(durationMillis = motion.standardMs, easing = FastOutSlowInEasing),
    )
    val pillVerticalPadding by animateDpAsState(
        targetValue = if (compact) spacing.sm else spacing.md,
        animationSpec = tween(durationMillis = motion.standardMs, easing = FastOutSlowInEasing),
    )
    val itemVerticalPadding by animateDpAsState(
        targetValue = if (compact) spacing.xs else spacing.sm,
        animationSpec = tween(durationMillis = motion.standardMs, easing = FastOutSlowInEasing),
    )

    val destinations = listOf(
        Triple(ActiveTab.TRAIL, "Trail", DroidQuestIcons.Map),
        Triple(ActiveTab.CHECKLIST, "Checklist", DroidQuestIcons.ListChecks),
        Triple(ActiveTab.REVIEWS, "Reviews", DroidQuestIcons.Brain),
        Triple(ActiveTab.TROPHIES, "Trophies", DroidQuestIcons.Trophy),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = outerHorizontalPadding, vertical = spacing.lg),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = ext.navPillSurface.copy(alpha = 0.96f),
            shape = DroidQuestCornerShapes.FullPill,
            border = BorderStroke(borders.hairline, ext.navPillBorder),
            shadowElevation = sizing.elevationNavPill,
            modifier = Modifier
                .widthIn(max = sizing.navPillMaxWidth)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md, vertical = pillVerticalPadding),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                destinations.forEach { (tab, label, icon) ->
                    val selected = activeTab == tab
                    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(DroidQuestCornerShapes.FullPill)
                            .clickable(role = Role.Tab) { onTabChange(tab) }
                            .padding(vertical = itemVerticalPadding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacing.xxs),
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = tint,
                                    modifier = Modifier.size(sizing.iconXl),
                                )
                                if (tab == ActiveTab.REVIEWS && dueReviewCount > 0) {
                                    Surface(
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape,
                                        modifier = Modifier.offset(x = spacing.md, y = -spacing.xs),
                                    ) {
                                        Text(
                                            text = if (dueReviewCount > 99) "99+" else dueReviewCount.toString(),
                                            style = DroidQuestTheme.mono.micro,
                                            color = MaterialTheme.colorScheme.background,
                                            modifier = Modifier.padding(horizontal = spacing.xs, vertical = borders.hairline),
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = !compact,
                                enter = fadeIn(tween(motion.fastMs)) + expandVertically(tween(motion.fastMs)),
                                exit = fadeOut(tween(motion.fastMs)) + shrinkVertically(tween(motion.fastMs)),
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = tint,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Refined clean button retaining signature compatibility.
 */
@Composable
fun Tactile3DButton(
    onClick: () -> Unit,
    containerColor: Color,
    ledgeColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Transparent,
    shape: Shape = DroidQuestCornerShapes.TactileButton,
    ledgeHeight: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = DroidQuestTheme.spacing.cardPadding,
        vertical = DroidQuestTheme.spacing.base,
    ),
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val spacing = DroidQuestTheme.spacing
    val borders = DroidQuestTheme.borders
    val motion = DroidQuestTheme.motion
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (pressed && enabled && ledgeHeight > 0.dp) borders.hairline else 0.dp,
        animationSpec = tween(durationMillis = motion.pressMs),
    )

    Surface(
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        contentColor = contentColor,
        shape = shape,
        border = if (borderColor != Color.Transparent) BorderStroke(borders.hairline, borderColor) else null,
        modifier = modifier
            .offset(y = pressOffset)
            .clip(shape)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterHorizontally),
            content = content,
        )
    }
}

@Composable
fun GradientProgressBar(
    progress: Float,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    borderColor: Color = Color.Transparent,
    height: Dp = DroidQuestTheme.sizing.progressBarStandard,
    minVisibleFraction: Float = 0.03f,
) {
    val motion = DroidQuestTheme.motion
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = if (clamped > 0f) maxOf(clamped, minVisibleFraction) else 0f,
        animationSpec = tween(durationMillis = motion.progressMs, easing = FastOutSlowInEasing),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(DroidQuestCornerShapes.FullPill)
            .background(trackColor),
    ) {
        if (animated > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animated)
                    .clip(DroidQuestCornerShapes.FullPill)
                    .background(
                        if (colors.size >= 2) Brush.horizontalGradient(colors)
                        else Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary))
                    ),
            )
        }
    }
}

@Composable
fun MicroPill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Transparent,
    icon: ImageVector? = null,
    shape: Shape = DroidQuestCornerShapes.MicroBadge,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        border = if (borderColor != Color.Transparent) BorderStroke(borders.hairline, borderColor) else null,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.xs),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(sizing.iconXs),
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }
    }
}

private data class ConfettiParticle(
    val angleRad: Float,
    val speed: Float,
    val radius: Float,
    val color: Color,
    val drift: Float,
)

@Composable
fun CelebrationConfettiOverlay(
    triggerId: Long,
    isBoss: Boolean,
    modifier: Modifier = Modifier,
) {
    if (triggerId == 0L) return
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val motion = DroidQuestTheme.motion
    val progress = remember(triggerId) { Animatable(0f) }
    val particles = remember(triggerId, primary, onSurface) {
        val rng = Random(triggerId)
        val palette = listOf(
            primary,
            primary.copy(alpha = 0.8f),
            primary.copy(alpha = 0.55f),
            onSurface.copy(alpha = 0.75f),
        )
        List(if (isBoss) 56 else 32) {
            ConfettiParticle(
                angleRad = (rng.nextFloat() * 2f * PI).toFloat(),
                speed = 180f + rng.nextFloat() * 400f,
                radius = 3.5f + rng.nextFloat() * 5f,
                color = palette[rng.nextInt(palette.size)],
                drift = (rng.nextFloat() - 0.5f) * 120f,
            )
        }
    }

    LaunchedEffect(triggerId) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = motion.celebrationMs, easing = LinearEasing))
    }

    val t = progress.value
    if (t < 1f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val origin = Offset(size.width * 0.5f, size.height * 0.35f)
            val alpha = (1f - t).coerceIn(0f, 1f)
            for (p in particles) {
                val x = origin.x + cos(p.angleRad) * p.speed * t + p.drift * t * t
                val y = origin.y + sin(p.angleRad) * p.speed * t + 420f * t * t
                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius * (1f - 0.35f * t),
                    center = Offset(x, y),
                )
            }
        }
    }
}

@Composable
fun FloatingCelebrationToast(
    message: String?,
    tone: ToastTone,
    modifier: Modifier = Modifier,
) {
    val spacing = DroidQuestTheme.spacing
    val sizing = DroidQuestTheme.sizing
    val borders = DroidQuestTheme.borders
    val motion = DroidQuestTheme.motion
    val accentColor = when (tone) {
        ToastTone.ROSE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = fadeIn(tween(motion.fastMs)) + slideInVertically(tween(motion.fastMs)) { it / 2 },
        exit = fadeOut(tween(motion.fastMs)) + slideOutVertically(tween(motion.fastMs)) { it / 2 },
        modifier = modifier,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.96f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = DroidQuestCornerShapes.QuestCard,
            border = BorderStroke(borders.hairline, MaterialTheme.colorScheme.outline),
            shadowElevation = sizing.elevationToast,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = spacing.cardPaddingCompact, vertical = spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.base),
            ) {
                Icon(
                    imageVector = DroidQuestIcons.CheckCircle2,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(sizing.iconMd),
                )
                Text(
                    text = message.orEmpty(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
