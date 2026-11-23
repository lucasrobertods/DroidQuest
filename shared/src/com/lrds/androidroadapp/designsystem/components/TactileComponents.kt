package com.lrds.androidroadapp.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
    height: Dp = 6.dp,
    minVisibleFraction: Float = 0.03f,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = if (clamped > 0f) maxOf(clamped, minVisibleFraction) else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
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
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    content: @Composable RowScope.() -> Unit,
) {
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
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            content = content,
        )
    }
}

/**
 * Calm dark neutral tonal button with subtle 1dp hairline border.
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
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 9.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        contentColor = contentColor,
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .clip(shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(contentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            content = content,
        )
    }
}

/**
 * Floating Pill Bottom Navigation Bar (approved Stitch mobile spec).
 */
@Composable
fun FloatingBottomNavBar(
    activeTab: ActiveTab,
    dueReviewCount: Int,
    onTabChange: (ActiveTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ext = DroidQuestTheme.colors
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
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            color = ext.navPillSurface.copy(alpha = 0.96f),
            shape = DroidQuestCornerShapes.FullPill,
            border = BorderStroke(1.dp, ext.navPillBorder),
            shadowElevation = 14.dp,
            modifier = Modifier
                .widthIn(max = 460.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 7.dp),
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
                            .padding(vertical = 5.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = tint,
                                    modifier = Modifier.size(20.dp),
                                )
                                if (tab == ActiveTab.REVIEWS && dueReviewCount > 0) {
                                    Surface(
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape,
                                        modifier = Modifier.offset(x = 8.dp, y = (-5).dp),
                                    ) {
                                        Text(
                                            text = if (dueReviewCount > 99) "99+" else dueReviewCount.toString(),
                                            style = DroidQuestTheme.mono.micro,
                                            color = MaterialTheme.colorScheme.background,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        )
                                    }
                                }
                            }
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

/**
 * Refined clean button retaining signature compatibility for existing callers.
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
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressOffset by animateDpAsState(
        targetValue = if (pressed && enabled && ledgeHeight > 0.dp) 1.dp else 0.dp,
        animationSpec = tween(durationMillis = 80),
    )

    Surface(
        color = if (enabled) containerColor else containerColor.copy(alpha = 0.5f),
        contentColor = contentColor,
        shape = shape,
        border = if (borderColor != Color.Transparent) BorderStroke(1.dp, borderColor) else null,
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
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
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
    height: Dp = 6.dp,
    minVisibleFraction: Float = 0.03f,
) {
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = if (clamped > 0f) maxOf(clamped, minVisibleFraction) else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
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
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
        border = if (borderColor != Color.Transparent) BorderStroke(1.dp, borderColor) else null,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
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
    val progress = remember(triggerId) { Animatable(0f) }
    val particles = remember(triggerId) {
        val rng = Random(triggerId)
        val palette = listOf(
            Color(0xFF22C55E),
            Color(0xFF4ADE80),
            Color(0xFF86EFAC),
            Color(0xFFF4F6FA),
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
        progress.animateTo(1f, tween(durationMillis = 1150, easing = LinearEasing))
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
    val accentColor = when (tone) {
        ToastTone.ROSE -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.primary
    }

    AnimatedVisibility(
        visible = !message.isNullOrBlank(),
        enter = fadeIn(tween(180)) + slideInVertically { it / 2 },
        exit = fadeOut(tween(220)) + slideOutVertically { it / 2 },
        modifier = modifier,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.96f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            shape = DroidQuestCornerShapes.QuestCard,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector = DroidQuestIcons.CheckCircle2,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp),
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
