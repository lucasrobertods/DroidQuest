package com.lrds.androidroadapp.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Refined Minimal Monochrome shape scale matching the approved Stitch spec.
 */
val DroidQuestShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // Micro tags, heatmap tiles, checkboxes
    small = RoundedCornerShape(12.dp),       // Action buttons, telemetry pills, inputs
    medium = RoundedCornerShape(16.dp),      // Quest cards, review cards, checklist rows
    large = RoundedCornerShape(20.dp),       // Hero readiness card, zone cards, modals
    extraLarge = RoundedCornerShape(24.dp),  // Quest Detail sheet & Settings dialog
)

object DroidQuestCornerShapes {
    val MicroBadge = RoundedCornerShape(8.dp)
    val TactileButton = RoundedCornerShape(12.dp)
    val HudPill = RoundedCornerShape(12.dp)
    val QuestCard = RoundedCornerShape(16.dp)
    val ZoneBanner = RoundedCornerShape(20.dp)
    val SheetTop = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val Dialog = RoundedCornerShape(22.dp)
    val FullPill = CircleShape
    val SteppingStoneNode = CircleShape
}
