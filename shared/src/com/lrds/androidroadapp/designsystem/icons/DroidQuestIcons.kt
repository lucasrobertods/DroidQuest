package com.lrds.androidroadapp.designsystem.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Crisp 24x24 Lucide-inspired rounded-stroke vector icon set built directly in Compose.
 * Zero dependency bloat across Android, iOS, Desktop JVM, and Web Wasm.
 */
object DroidQuestIcons {
    private val stroke = SolidColor(Color.White)

    private fun lucide(
        name: String,
        strokeWidth: Float = 2f,
        block: ImageVector.Builder.(strokeWidth: Float) -> Unit,
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply { block(strokeWidth) }.build()

    private fun ImageVector.Builder.strokePath(
        strokeWidth: Float = 2f,
        builder: PathBuilder.() -> Unit,
    ) {
        path(
            fill = null,
            stroke = stroke,
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = builder,
        )
    }

    private fun ImageVector.Builder.circleStroke(
        cx: Float,
        cy: Float,
        r: Float,
        strokeWidth: Float = 2f,
    ) {
        strokePath(strokeWidth) {
            moveTo(cx + r, cy)
            arcTo(r, r, 0f, isMoreThanHalf = true, isPositiveArc = false, cx - r, cy)
            arcTo(r, r, 0f, isMoreThanHalf = true, isPositiveArc = false, cx + r, cy)
            close()
        }
    }

    private fun ImageVector.Builder.rectStroke(
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        r: Float = 2f,
        strokeWidth: Float = 2f,
    ) {
        strokePath(strokeWidth) {
            moveTo(x + r, y)
            lineTo(x + w - r, y)
            if (r > 0f) arcTo(r, r, 0f, false, true, x + w, y + r)
            lineTo(x + w, y + h - r)
            if (r > 0f) arcTo(r, r, 0f, false, true, x + w - r, y + h)
            lineTo(x + r, y + h)
            if (r > 0f) arcTo(r, r, 0f, false, true, x, y + h - r)
            lineTo(x, y + r)
            if (r > 0f) arcTo(r, r, 0f, false, true, x + r, y)
            close()
        }
    }

    val Sparkles: ImageVector by lazy {
        lucide("Sparkles") {
            strokePath {
                moveTo(12f, 3f)
                lineTo(13.8f, 8.2f)
                lineTo(19f, 10f)
                lineTo(13.8f, 11.8f)
                lineTo(12f, 17f)
                lineTo(10.2f, 11.8f)
                lineTo(5f, 10f)
                lineTo(10.2f, 8.2f)
                close()
                moveTo(19f, 15f)
                lineTo(19.9f, 17.1f)
                lineTo(22f, 18f)
                lineTo(19.9f, 18.9f)
                lineTo(19f, 21f)
                lineTo(18.1f, 18.9f)
                lineTo(16f, 18f)
                lineTo(18.1f, 17.1f)
                close()
            }
        }
    }

    val Flame: ImageVector by lazy {
        lucide("Flame") {
            strokePath {
                moveTo(12f, 2.5f)
                curveTo(14.8f, 6.2f, 18.5f, 9f, 18.5f, 14f)
                arcTo(6.5f, 6.5f, 0f, false, true, 5.5f, 14f)
                curveTo(5.5f, 10.6f, 7.6f, 8.4f, 8.8f, 6.8f)
                curveTo(9.1f, 8.5f, 10.1f, 9.4f, 10.8f, 9.7f)
                curveTo(10.4f, 7f, 11f, 4.6f, 12f, 2.5f)
                close()
            }
        }
    }

    val CalendarClock: ImageVector by lazy {
        lucide("CalendarClock") {
            rectStroke(3f, 4f, 18f, 17f, 2.5f)
            strokePath {
                moveTo(8f, 2f); lineTo(8f, 6f)
                moveTo(16f, 2f); lineTo(16f, 6f)
                moveTo(3f, 9.5f); lineTo(21f, 9.5f)
                moveTo(12f, 13f); lineTo(12f, 15.5f); lineTo(14f, 16.8f)
            }
        }
    }

    val Bell: ImageVector by lazy {
        lucide("Bell") {
            strokePath {
                moveTo(6f, 9f)
                arcTo(6f, 6f, 0f, false, true, 18f, 9f)
                curveTo(18f, 14.5f, 20f, 16f, 20f, 16f)
                lineTo(4f, 16f)
                curveTo(4f, 16f, 6f, 14.5f, 6f, 9f)
                moveTo(10.3f, 20f)
                arcTo(2f, 2f, 0f, false, false, 13.7f, 20f)
            }
        }
    }

    val BellRing: ImageVector by lazy {
        lucide("BellRing") {
            strokePath {
                moveTo(6f, 9f)
                arcTo(6f, 6f, 0f, false, true, 18f, 9f)
                curveTo(18f, 14.5f, 20f, 16f, 20f, 16f)
                lineTo(4f, 16f)
                curveTo(4f, 16f, 6f, 14.5f, 6f, 9f)
                moveTo(10.3f, 20f)
                arcTo(2f, 2f, 0f, false, false, 13.7f, 20f)
                moveTo(3.5f, 5.5f); lineTo(5.5f, 7f)
                moveTo(20.5f, 5.5f); lineTo(18.5f, 7f)
            }
        }
    }

    val HardDrive: ImageVector by lazy {
        lucide("HardDrive") {
            rectStroke(3f, 13f, 18f, 7f, 2f)
            strokePath {
                moveTo(5.5f, 13f); lineTo(8f, 5f); lineTo(16f, 5f); lineTo(18.5f, 13f)
                moveTo(7f, 16.5f); lineTo(7.1f, 16.5f)
                moveTo(11f, 16.5f); lineTo(11.1f, 16.5f)
            }
        }
    }

    val Volume2: ImageVector by lazy {
        lucide("Volume2") {
            strokePath {
                moveTo(11f, 5f); lineTo(6f, 9f); lineTo(2f, 9f); lineTo(2f, 15f); lineTo(6f, 15f); lineTo(11f, 19f); close()
                moveTo(15.5f, 8.5f); curveTo(17.5f, 10.5f, 17.5f, 13.5f, 15.5f, 15.5f)
                moveTo(18.5f, 5.5f); curveTo(22f, 9f, 22f, 15f, 18.5f, 18.5f)
            }
        }
    }

    val VolumeX: ImageVector by lazy {
        lucide("VolumeX") {
            strokePath {
                moveTo(11f, 5f); lineTo(6f, 9f); lineTo(2f, 9f); lineTo(2f, 15f); lineTo(6f, 15f); lineTo(11f, 19f); close()
                moveTo(22f, 9f); lineTo(16f, 15f)
                moveTo(16f, 9f); lineTo(22f, 15f)
            }
        }
    }

    val Settings: ImageVector by lazy {
        lucide("Settings") {
            circleStroke(12f, 12f, 3.2f)
            strokePath {
                moveTo(12f, 2.5f); lineTo(12f, 5f)
                moveTo(12f, 19f); lineTo(12f, 21.5f)
                moveTo(4.9f, 4.9f); lineTo(6.7f, 6.7f)
                moveTo(17.3f, 17.3f); lineTo(19.1f, 19.1f)
                moveTo(2.5f, 12f); lineTo(5f, 12f)
                moveTo(19f, 12f); lineTo(21.5f, 12f)
                moveTo(4.9f, 19.1f); lineTo(6.7f, 17.3f)
                moveTo(17.3f, 6.7f); lineTo(19.1f, 4.9f)
            }
        }
    }

    val Map: ImageVector by lazy {
        lucide("Map") {
            strokePath {
                moveTo(3f, 6f); lineTo(9f, 3.5f); lineTo(15f, 6f); lineTo(21f, 3.5f)
                lineTo(21f, 18f); lineTo(15f, 20.5f); lineTo(9f, 18f); lineTo(3f, 20.5f); close()
                moveTo(9f, 3.5f); lineTo(9f, 18f)
                moveTo(15f, 6f); lineTo(15f, 20.5f)
            }
        }
    }

    val ListChecks: ImageVector by lazy {
        lucide("ListChecks") {
            strokePath {
                moveTo(3f, 7f); lineTo(5f, 9f); lineTo(8.5f, 5.5f)
                moveTo(12f, 7f); lineTo(21f, 7f)
                moveTo(3f, 15f); lineTo(5f, 17f); lineTo(8.5f, 13.5f)
                moveTo(12f, 15f); lineTo(21f, 15f)
            }
        }
    }

    val Brain: ImageVector by lazy {
        lucide("Brain") {
            strokePath {
                moveTo(12f, 4f); lineTo(12f, 20f)
                moveTo(12f, 5f); curveTo(9.5f, 3.5f, 6f, 4.8f, 5.5f, 7.5f)
                curveTo(3.5f, 8.2f, 3f, 11.2f, 4.8f, 12.8f)
                curveTo(3.5f, 15f, 5f, 18.5f, 8f, 18.8f)
                curveTo(9.5f, 20f, 11.2f, 19.8f, 12f, 19f)
                moveTo(12f, 5f); curveTo(14.5f, 3.5f, 18f, 4.8f, 18.5f, 7.5f)
                curveTo(20.5f, 8.2f, 21f, 11.2f, 19.2f, 12.8f)
                curveTo(20.5f, 15f, 19f, 18.5f, 16f, 18.8f)
                curveTo(14.5f, 20f, 12.8f, 19.8f, 12f, 19f)
            }
        }
    }

    val Trophy: ImageVector by lazy {
        lucide("Trophy") {
            strokePath {
                moveTo(6f, 4f); lineTo(18f, 4f); lineTo(18f, 10f)
                arcTo(6f, 6f, 0f, false, true, 6f, 10f); close()
                moveTo(6f, 6f); lineTo(3f, 6f); lineTo(3f, 9f); arcTo(3f, 3f, 0f, false, false, 6f, 12f)
                moveTo(18f, 6f); lineTo(21f, 6f); lineTo(21f, 9f); arcTo(3f, 3f, 0f, false, true, 18f, 12f)
                moveTo(12f, 16f); lineTo(12f, 20f)
                moveTo(8f, 20f); lineTo(16f, 20f)
            }
        }
    }

    val Zap: ImageVector by lazy {
        lucide("Zap") {
            strokePath {
                moveTo(13f, 2f); lineTo(4f, 14f); lineTo(11f, 14f); lineTo(10f, 22f); lineTo(20f, 10f); lineTo(13f, 10f); close()
            }
        }
    }

    val Compass: ImageVector by lazy {
        lucide("Compass") {
            circleStroke(12f, 12f, 9.5f)
            strokePath {
                moveTo(16f, 8f); lineTo(14f, 14f); lineTo(8f, 16f); lineTo(10f, 10f); close()
            }
        }
    }

    val ArrowRight: ImageVector by lazy {
        lucide("ArrowRight") {
            strokePath {
                moveTo(5f, 12f); lineTo(19f, 12f)
                moveTo(13f, 6f); lineTo(19f, 12f); lineTo(13f, 18f)
            }
        }
    }

    val ChevronRight: ImageVector by lazy {
        lucide("ChevronRight") {
            strokePath { moveTo(9f, 6f); lineTo(15f, 12f); lineTo(9f, 18f) }
        }
    }

    val ChevronDown: ImageVector by lazy {
        lucide("ChevronDown") {
            strokePath { moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f) }
        }
    }

    val ChevronUp: ImageVector by lazy {
        lucide("ChevronUp") {
            strokePath { moveTo(6f, 15f); lineTo(12f, 9f); lineTo(18f, 15f) }
        }
    }

    val Check: ImageVector by lazy {
        lucide("Check", strokeWidth = 2.8f) { sw ->
            strokePath(sw) { moveTo(4.5f, 12.5f); lineTo(9.5f, 17.5f); lineTo(19.5f, 6.5f) }
        }
    }

    val CheckCircle2: ImageVector by lazy {
        lucide("CheckCircle2") {
            circleStroke(12f, 12f, 9.5f)
            strokePath { moveTo(8.5f, 12.5f); lineTo(11f, 15f); lineTo(16f, 9.5f) }
        }
    }

    val Award: ImageVector by lazy {
        lucide("Award") {
            circleStroke(12f, 9f, 5.5f)
            strokePath {
                moveTo(8.5f, 14f); lineTo(7f, 21.5f); lineTo(12f, 19f); lineTo(17f, 21.5f); lineTo(15.5f, 14f)
            }
        }
    }

    val Clock: ImageVector by lazy {
        lucide("Clock") {
            circleStroke(12f, 12f, 9.5f)
            strokePath { moveTo(12f, 6.5f); lineTo(12f, 12f); lineTo(16f, 14f) }
        }
    }

    val Crown: ImageVector by lazy {
        lucide("Crown") {
            strokePath {
                moveTo(3f, 18f); lineTo(4.5f, 8f); lineTo(9f, 12f); lineTo(12f, 5.5f); lineTo(15f, 12f); lineTo(19.5f, 8f); lineTo(21f, 18f); close()
            }
        }
    }

    val Swords: ImageVector by lazy {
        lucide("Swords") {
            strokePath {
                moveTo(14.5f, 17.5f); lineTo(3f, 6f); lineTo(3f, 3f); lineTo(6f, 3f); lineTo(17.5f, 14.5f)
                moveTo(13f, 19f); lineTo(19f, 13f)
                moveTo(16f, 16f); lineTo(20f, 20f)
                moveTo(9.5f, 17.5f); lineTo(21f, 6f); lineTo(21f, 3f); lineTo(18f, 3f); lineTo(6.5f, 14.5f)
                moveTo(5f, 13f); lineTo(11f, 19f)
                moveTo(8f, 16f); lineTo(4f, 20f)
            }
        }
    }

    val Close: ImageVector by lazy {
        lucide("Close") {
            strokePath {
                moveTo(6f, 6f); lineTo(18f, 18f)
                moveTo(18f, 6f); lineTo(6f, 18f)
            }
        }
    }

    val ExternalLink: ImageVector by lazy {
        lucide("ExternalLink") {
            strokePath {
                moveTo(14f, 4f); lineTo(20f, 4f); lineTo(20f, 10f)
                moveTo(10f, 14f); lineTo(20f, 4f)
                moveTo(18f, 14f); lineTo(18f, 19f); lineTo(5f, 19f); lineTo(5f, 6f); lineTo(10f, 6f)
            }
        }
    }

    val Pin: ImageVector by lazy {
        lucide("Pin") {
            strokePath {
                moveTo(12f, 17f); lineTo(12f, 22f)
                moveTo(6f, 17f); lineTo(18f, 17f)
                moveTo(8f, 4f); lineTo(16f, 4f)
                moveTo(9f, 4f); lineTo(8f, 12f); lineTo(6f, 17f)
                moveTo(15f, 4f); lineTo(16f, 12f); lineTo(18f, 17f)
            }
        }
    }

    val Plus: ImageVector by lazy {
        lucide("Plus") {
            strokePath {
                moveTo(12f, 5f); lineTo(12f, 19f)
                moveTo(5f, 12f); lineTo(19f, 12f)
            }
        }
    }

    val Trash2: ImageVector by lazy {
        lucide("Trash2") {
            strokePath {
                moveTo(3f, 6f); lineTo(21f, 6f)
                moveTo(8f, 6f); lineTo(8f, 4f); lineTo(16f, 4f); lineTo(16f, 6f)
                moveTo(6f, 6f); lineTo(7f, 20f); lineTo(17f, 20f); lineTo(18f, 6f)
                moveTo(10f, 11f); lineTo(10f, 16f)
                moveTo(14f, 11f); lineTo(14f, 16f)
            }
        }
    }

    val Search: ImageVector by lazy {
        lucide("Search") {
            circleStroke(11f, 11f, 7f)
            strokePath { moveTo(20.5f, 20.5f); lineTo(16f, 16f) }
        }
    }

    val Lock: ImageVector by lazy {
        lucide("Lock") {
            rectStroke(5f, 11f, 14f, 10f, 2f)
            strokePath {
                moveTo(8f, 11f); lineTo(8f, 7.5f)
                arcTo(4f, 4f, 0f, false, true, 16f, 7.5f); lineTo(16f, 11f)
            }
        }
    }

    val RotateCcw: ImageVector by lazy {
        lucide("RotateCcw") {
            strokePath {
                moveTo(3f, 12f); arcTo(9f, 9f, 0f, true, false, 6f, 5.5f)
                moveTo(3f, 4f); lineTo(3f, 9f); lineTo(8f, 9f)
            }
        }
    }

    val Download: ImageVector by lazy {
        lucide("Download") {
            strokePath {
                moveTo(12f, 4f); lineTo(12f, 15f)
                moveTo(7.5f, 10.5f); lineTo(12f, 15f); lineTo(16.5f, 10.5f)
                moveTo(4f, 19f); lineTo(20f, 19f)
            }
        }
    }

    val Upload: ImageVector by lazy {
        lucide("Upload") {
            strokePath {
                moveTo(12f, 15f); lineTo(12f, 4f)
                moveTo(7.5f, 8.5f); lineTo(12f, 4f); lineTo(16.5f, 8.5f)
                moveTo(4f, 19f); lineTo(20f, 19f)
            }
        }
    }

    val HelpCircle: ImageVector by lazy {
        lucide("HelpCircle") {
            circleStroke(12f, 12f, 9.5f)
            strokePath {
                moveTo(9.5f, 9.5f); curveTo(9.8f, 7.5f, 14.5f, 7.5f, 14.5f, 10f)
                curveTo(14.5f, 11.8f, 12f, 12f, 12f, 13.8f)
                moveTo(12f, 17.2f); lineTo(12.01f, 17.2f)
            }
        }
    }

    val StickyNote: ImageVector by lazy {
        lucide("StickyNote") {
            strokePath {
                moveTo(5f, 4f); lineTo(19f, 4f); lineTo(19f, 14f); lineTo(14f, 19f); lineTo(5f, 19f); close()
                moveTo(14f, 19f); lineTo(14f, 14f); lineTo(19f, 14f)
            }
        }
    }

    val Link2: ImageVector by lazy {
        lucide("Link2") {
            strokePath {
                moveTo(9f, 17f); lineTo(7f, 17f); arcTo(5f, 5f, 0f, false, true, 7f, 7f); lineTo(9f, 7f)
                moveTo(15f, 7f); lineTo(17f, 7f); arcTo(5f, 5f, 0f, false, true, 17f, 17f); lineTo(15f, 17f)
                moveTo(8f, 12f); lineTo(16f, 12f)
            }
        }
    }

    val Youtube: ImageVector by lazy {
        lucide("Youtube") {
            rectStroke(2.5f, 5.5f, 19f, 13f, 3.5f)
            strokePath { moveTo(10f, 9.5f); lineTo(15.5f, 12f); lineTo(10f, 14.5f); close() }
        }
    }

    val BookOpen: ImageVector by lazy {
        lucide("BookOpen") {
            strokePath {
                moveTo(2f, 5f); lineTo(9f, 5f); curveTo(11f, 5f, 12f, 6.2f, 12f, 8f); lineTo(12f, 19f)
                curveTo(11f, 17.8f, 9.5f, 17.5f, 2f, 17.5f); close()
                moveTo(22f, 5f); lineTo(15f, 5f); curveTo(13f, 5f, 12f, 6.2f, 12f, 8f); lineTo(12f, 19f)
                curveTo(13f, 17.8f, 14.5f, 17.5f, 22f, 17.5f); close()
            }
        }
    }

    val FileText: ImageVector by lazy {
        lucide("FileText") {
            strokePath {
                moveTo(6f, 3f); lineTo(14f, 3f); lineTo(18f, 7f); lineTo(18f, 21f); lineTo(6f, 21f); close()
                moveTo(14f, 3f); lineTo(14f, 7f); lineTo(18f, 7f)
                moveTo(9f, 12f); lineTo(15f, 12f)
                moveTo(9f, 16f); lineTo(15f, 16f)
            }
        }
    }

    val GraduationCap: ImageVector by lazy {
        lucide("GraduationCap") {
            strokePath {
                moveTo(2f, 10f); lineTo(12f, 5f); lineTo(22f, 10f); lineTo(12f, 15f); close()
                moveTo(6f, 12.5f); lineTo(6f, 17f); curveTo(9f, 19.5f, 15f, 19.5f, 18f, 17f); lineTo(18f, 12.5f)
                moveTo(22f, 10f); lineTo(22f, 16f)
            }
        }
    }

    val GitBranch: ImageVector by lazy {
        lucide("GitBranch") {
            circleStroke(7f, 6f, 2.5f)
            circleStroke(17f, 6f, 2.5f)
            circleStroke(7f, 18f, 2.5f)
            strokePath {
                moveTo(7f, 8.5f); lineTo(7f, 15.5f)
                moveTo(17f, 8.5f); curveTo(17f, 13f, 7f, 12f, 7f, 15.5f)
            }
        }
    }

    val Code: ImageVector by lazy {
        lucide("Code") {
            strokePath {
                moveTo(8f, 7f); lineTo(3f, 12f); lineTo(8f, 17f)
                moveTo(16f, 7f); lineTo(21f, 12f); lineTo(16f, 17f)
            }
        }
    }

    val Code2: ImageVector by lazy {
        lucide("Code2") {
            strokePath {
                moveTo(7f, 8f); lineTo(3f, 12f); lineTo(7f, 16f)
                moveTo(17f, 8f); lineTo(21f, 12f); lineTo(17f, 16f)
                moveTo(14f, 5f); lineTo(10f, 19f)
            }
        }
    }

    val Cpu: ImageVector by lazy {
        lucide("Cpu") {
            rectStroke(6f, 6f, 12f, 12f, 2f)
            rectStroke(9.5f, 9.5f, 5f, 5f, 0.8f)
            strokePath {
                moveTo(9f, 2.5f); lineTo(9f, 6f); moveTo(15f, 2.5f); lineTo(15f, 6f)
                moveTo(9f, 18f); lineTo(9f, 21.5f); moveTo(15f, 18f); lineTo(15f, 21.5f)
                moveTo(2.5f, 9f); lineTo(6f, 9f); moveTo(2.5f, 15f); lineTo(6f, 15f)
                moveTo(18f, 9f); lineTo(21.5f, 9f); moveTo(18f, 15f); lineTo(21.5f, 15f)
            }
        }
    }

    val Navigation: ImageVector by lazy {
        lucide("Navigation") {
            strokePath {
                moveTo(3f, 11f); lineTo(21f, 3f); lineTo(13f, 21f); lineTo(11f, 13f); close()
            }
        }
    }

    val Layers: ImageVector by lazy {
        lucide("Layers") {
            strokePath {
                moveTo(12f, 3f); lineTo(21f, 8f); lineTo(12f, 13f); lineTo(3f, 8f); close()
                moveTo(3f, 12.5f); lineTo(12f, 17.5f); lineTo(21f, 12.5f)
                moveTo(3f, 17f); lineTo(12f, 21.5f); lineTo(21f, 17f)
            }
        }
    }

    val Plug: ImageVector by lazy {
        lucide("Plug") {
            strokePath {
                moveTo(9f, 3f); lineTo(9f, 8f)
                moveTo(15f, 3f); lineTo(15f, 8f)
                moveTo(6f, 8f); lineTo(18f, 8f); lineTo(18f, 12f)
                arcTo(6f, 6f, 0f, false, true, 6f, 12f); close()
                moveTo(12f, 18f); lineTo(12f, 22f)
            }
        }
    }

    val Database: ImageVector by lazy {
        lucide("Database") {
            strokePath {
                moveTo(5f, 6f); curveTo(5f, 3.8f, 19f, 3.8f, 19f, 6f); curveTo(19f, 8.2f, 5f, 8.2f, 5f, 6f)
                moveTo(5f, 6f); lineTo(5f, 18f); curveTo(5f, 20.2f, 19f, 20.2f, 19f, 18f); lineTo(19f, 6f)
                moveTo(5f, 12f); curveTo(5f, 14.2f, 19f, 14.2f, 19f, 12f)
            }
        }
    }

    val Globe: ImageVector by lazy {
        lucide("Globe") {
            circleStroke(12f, 12f, 9.5f)
            strokePath {
                moveTo(2.5f, 12f); lineTo(21.5f, 12f)
                moveTo(12f, 2.5f); curveTo(15.5f, 6f, 15.5f, 18f, 12f, 21.5f)
                moveTo(12f, 2.5f); curveTo(8.5f, 6f, 8.5f, 18f, 12f, 21.5f)
            }
        }
    }

    val FlaskConical: ImageVector by lazy {
        lucide("FlaskConical") {
            strokePath {
                moveTo(9f, 3f); lineTo(15f, 3f)
                moveTo(10f, 3f); lineTo(10f, 9f); lineTo(4.5f, 19f); lineTo(19.5f, 19f); lineTo(14f, 9f); lineTo(14f, 3f)
                moveTo(7.5f, 14.5f); lineTo(16.5f, 14.5f)
            }
        }
    }

    val ShieldCheck: ImageVector by lazy {
        lucide("ShieldCheck") {
            strokePath {
                moveTo(12f, 2.5f); lineTo(20f, 6f); lineTo(20f, 12f)
                curveTo(20f, 17f, 16f, 20f, 12f, 21.5f)
                curveTo(8f, 20f, 4f, 17f, 4f, 12f); lineTo(4f, 6f); close()
                moveTo(8.5f, 12f); lineTo(11f, 14.5f); lineTo(15.5f, 9.5f)
            }
        }
    }

    val Languages: ImageVector by lazy {
        lucide("Languages") {
            strokePath {
                moveTo(4f, 6f); lineTo(13f, 6f)
                moveTo(8.5f, 4f); lineTo(8.5f, 6f)
                moveTo(11f, 6f); curveTo(10f, 11f, 7f, 14f, 4f, 16f)
                moveTo(6f, 9.5f); curveTo(7.5f, 12f, 9.5f, 14f, 12f, 15.5f)
                moveTo(13f, 20f); lineTo(17f, 10f); lineTo(21f, 20f)
                moveTo(14.5f, 17f); lineTo(19.5f, 17f)
            }
        }
    }

    val Gauge: ImageVector by lazy {
        lucide("Gauge") {
            strokePath {
                moveTo(4f, 17f); arcTo(9.5f, 9.5f, 0f, true, true, 20f, 17f)
                moveTo(12f, 14f); lineTo(16.5f, 9.5f)
            }
            circleStroke(12f, 14.5f, 1.5f)
        }
    }

    val Rocket: ImageVector by lazy {
        lucide("Rocket") {
            strokePath {
                moveTo(14f, 4f); curveTo(18f, 3.5f, 20.5f, 3.5f, 20.5f, 3.5f)
                curveTo(20.5f, 3.5f, 20.5f, 6f, 20f, 10f)
                lineTo(13f, 17f); lineTo(7f, 11f); close()
                moveTo(9f, 13f); lineTo(4.5f, 13f); lineTo(3f, 16f); lineTo(7f, 16f)
                moveTo(11f, 15f); lineTo(11f, 19.5f); lineTo(8f, 21f); lineTo(8f, 17f)
            }
            circleStroke(15.5f, 8.5f, 1.4f)
        }
    }

    val FolderGit2: ImageVector by lazy {
        lucide("FolderGit2") {
            strokePath {
                moveTo(3f, 6f); lineTo(9f, 6f); lineTo(11f, 8.5f); lineTo(21f, 8.5f); lineTo(21f, 19f); lineTo(3f, 19f); close()
            }
            circleStroke(9.5f, 14f, 1.6f)
            circleStroke(15.5f, 14f, 1.6f)
            strokePath { moveTo(11.1f, 14f); lineTo(13.9f, 14f) }
        }
    }

    val TrendingUp: ImageVector by lazy {
        lucide("TrendingUp") {
            strokePath {
                moveTo(3f, 17f); lineTo(9.5f, 10.5f); lineTo(13.5f, 14.5f); lineTo(21f, 7f)
                moveTo(15.5f, 7f); lineTo(21f, 7f); lineTo(21f, 12.5f)
            }
        }
    }

    val Boxes: ImageVector by lazy {
        lucide("Boxes") {
            rectStroke(3f, 13f, 7.5f, 7.5f, 1.5f)
            rectStroke(13.5f, 13f, 7.5f, 7.5f, 1.5f)
            rectStroke(8.2f, 3.5f, 7.5f, 7.5f, 1.5f)
        }
    }

    val TextCursorInput: ImageVector by lazy {
        lucide("TextCursorInput") {
            rectStroke(3f, 7f, 18f, 10f, 2f)
            strokePath {
                moveTo(10f, 4f); lineTo(10f, 20f)
                moveTo(8f, 4f); lineTo(12f, 4f)
                moveTo(8f, 20f); lineTo(12f, 20f)
            }
        }
    }

    val ArrowDownWideNarrow: ImageVector by lazy {
        lucide("ArrowDownWideNarrow") {
            strokePath {
                moveTo(6f, 4f); lineTo(6f, 20f)
                moveTo(3f, 17f); lineTo(6f, 20f); lineTo(9f, 17f)
                moveTo(12f, 6f); lineTo(21f, 6f)
                moveTo(12f, 12f); lineTo(18.5f, 12f)
                moveTo(12f, 18f); lineTo(16f, 18f)
            }
        }
    }

    val GitFork: ImageVector by lazy {
        lucide("GitFork") {
            circleStroke(7f, 6f, 2.2f)
            circleStroke(17f, 6f, 2.2f)
            circleStroke(12f, 18f, 2.2f)
            strokePath {
                moveTo(7f, 8.2f); curveTo(7f, 12f, 12f, 12f, 12f, 15.8f)
                moveTo(17f, 8.2f); curveTo(17f, 12f, 12f, 12f, 12f, 15.8f)
            }
        }
    }

    val Network: ImageVector by lazy {
        lucide("Network") {
            rectStroke(9.5f, 3f, 5f, 5f, 1.2f)
            rectStroke(3f, 16f, 5f, 5f, 1.2f)
            rectStroke(16f, 16f, 5f, 5f, 1.2f)
            strokePath {
                moveTo(12f, 8f); lineTo(12f, 12f)
                moveTo(5.5f, 16f); lineTo(5.5f, 12f); lineTo(18.5f, 12f); lineTo(18.5f, 16f)
            }
        }
    }

    val Terminal: ImageVector by lazy {
        lucide("Terminal") {
            strokePath {
                moveTo(4f, 17f); lineTo(10f, 11f); lineTo(4f, 5f)
                moveTo(12f, 19f); lineTo(20f, 19f)
            }
        }
    }

    val Blocks: ImageVector by lazy {
        lucide("Blocks") {
            rectStroke(14f, 3f, 7f, 7f, 1.5f)
            strokePath {
                moveTo(10f, 6f); lineTo(4f, 6f); lineTo(4f, 20f); lineTo(18f, 20f); lineTo(18f, 14f)
                moveTo(4f, 13f); lineTo(11f, 13f); lineTo(11f, 20f)
            }
        }
    }

    val Orbit: ImageVector by lazy {
        lucide("Orbit") {
            circleStroke(12f, 12f, 3f)
            circleStroke(19f, 5f, 1.8f)
            circleStroke(5f, 19f, 1.8f)
            strokePath {
                moveTo(17.5f, 7f); arcTo(8.2f, 8.2f, 0f, false, true, 7f, 17.5f)
                moveTo(6.5f, 17f); arcTo(8.2f, 8.2f, 0f, false, true, 17f, 6.5f)
            }
        }
    }

    val Calendar: ImageVector by lazy {
        lucide("Calendar") {
            rectStroke(3f, 4.5f, 18f, 16.5f, 2.5f)
            strokePath {
                moveTo(8f, 2.5f); lineTo(8f, 6f)
                moveTo(16f, 2.5f); lineTo(16f, 6f)
                moveTo(3f, 10f); lineTo(21f, 10f)
            }
        }
    }

    val Copy: ImageVector by lazy {
        lucide("Copy") {
            rectStroke(9f, 9f, 12f, 12f, 2f)
            strokePath {
                moveTo(5f, 15f); lineTo(4f, 15f); lineTo(4f, 4f); lineTo(15f, 4f); lineTo(15f, 5f)
            }
        }
    }

    val Sun: ImageVector by lazy {
        lucide("Sun") {
            circleStroke(12f, 12f, 4f)
            strokePath {
                moveTo(12f, 2.5f); lineTo(12f, 4.5f)
                moveTo(12f, 19.5f); lineTo(12f, 21.5f)
                moveTo(4.93f, 4.93f); lineTo(6.34f, 6.34f)
                moveTo(17.66f, 17.66f); lineTo(19.07f, 19.07f)
                moveTo(2.5f, 12f); lineTo(4.5f, 12f)
                moveTo(19.5f, 12f); lineTo(21.5f, 12f)
                moveTo(6.34f, 17.66f); lineTo(4.93f, 19.07f)
                moveTo(19.07f, 4.93f); lineTo(17.66f, 6.34f)
            }
        }
    }

    val Moon: ImageVector by lazy {
        lucide("Moon") {
            strokePath {
                moveTo(12f, 3f)
                arcTo(6.5f, 6.5f, 0f, isMoreThanHalf = false, isPositiveArc = false, 21f, 12f)
                arcTo(9f, 9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12f, 3f)
                close()
            }
        }
    }

    fun questIcon(name: String): ImageVector = when (name) {
        "Cpu" -> Cpu
        "Code2" -> Code2
        "Navigation" -> Navigation
        "Sparkles" -> Sparkles
        "Swords" -> Swords
        "Zap" -> Zap
        "Layers" -> Layers
        "Plug" -> Plug
        "Database" -> Database
        "Globe" -> Globe
        "Globe2" -> Globe
        "BellRing" -> BellRing
        "Flame" -> Flame
        "FlaskConical" -> FlaskConical
        "ShieldCheck" -> ShieldCheck
        "Languages" -> Languages
        "Gauge" -> Gauge
        "Rocket" -> Rocket
        "FolderGit2" -> FolderGit2
        "TrendingUp" -> TrendingUp
        "Boxes" -> Boxes
        "TextCursorInput" -> TextCursorInput
        "ArrowDownWideNarrow" -> ArrowDownWideNarrow
        "GitFork" -> GitFork
        "Network" -> Network
        "Terminal" -> Terminal
        "Compass" -> Compass
        "Blocks" -> Blocks
        "Orbit" -> Orbit
        "Crown" -> Crown
        "Trophy" -> Trophy
        else -> Sparkles
    }
}
