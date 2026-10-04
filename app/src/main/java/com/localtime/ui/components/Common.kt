package com.localtime.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localtime.core.model.SolarPosition
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    0f to tint.copy(alpha = 0.82f),
                    0.48f to tint.copy(alpha = 0.60f),
                    1f to tint.copy(alpha = 0.48f),
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f), shape),
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.24f),
                            Color.Transparent,
                        ),
                        radius = 320f,
                        center = Offset(0f, 0f),
                    )
                )
                .alpha(0.9f)
        )
        content()
    }
}

@Composable
fun GlassPill(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    content: @Composable BoxScope.() -> Unit,
) {
    val color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = if (selected) 0.86f else 0.40f))
            .border(1.dp, Color.White.copy(alpha = if (selected) 0.22f else 0.10f), RoundedCornerShape(18.dp)),
        content = content,
    )
}

@Composable
fun SectionHeader(
    eyebrow: String,
    title: String,
    detail: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            eyebrow.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.6.sp,
        )
        Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        if (detail != null) {
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun Metric(
    label: String,
    value: String,
    detail: String = "",
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
        if (icon != null) icon()
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
            if (detail.isNotBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun AnimatedNumber(value: String, modifier: Modifier = Modifier, reduceMotion: Boolean = false) {
    var previous by remember { mutableStateOf(value) }
    val pulseTarget = if (previous == value || reduceMotion) 1f else 1.02f
    val scale by animateFloatAsState(
        targetValue = pulseTarget,
        animationSpec = spring(stiffness = 700f),
        label = "numberScale",
    )
    if (previous != value) previous = value

    AnimatedContent(
        targetState = value,
        transitionSpec = {
            if (reduceMotion) fadeIn(tween(80)) togetherWith fadeOut(tween(80))
            else fadeIn(tween(140)) togetherWith fadeOut(tween(110))
        },
        label = "timeDigits",
        modifier = modifier,
    ) { shown ->
        Text(
            text = shown,
            modifier = Modifier.alpha(scale),
            fontSize = 48.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-1.5).sp,
        )
    }
}

@Composable
fun SolarArc(
    position: SolarPosition,
    trajectory: List<SolarPosition>,
    modifier: Modifier = Modifier,
    showLabels: Boolean = true,
    accent: Color = MaterialTheme.colorScheme.primary,
    sun: Color = MaterialTheme.colorScheme.tertiary,
) {
    val grid = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier.fillMaxWidth().height(235.dp)) {
        if (trajectory.isEmpty()) return@Canvas
        val baseline = size.height * 0.84f
        val radiusX = size.width * 0.41f
        val amplitude = size.height * 0.62f
        val denominator = max(trajectory.size - 1, 1)

        for (i in 0..4) {
            val y = baseline - amplitude * i / 4f
            drawLine(grid, Offset(size.width * 0.08f, y), Offset(size.width * 0.92f, y), 1.2f)
        }

        drawLine(grid, Offset(size.width * 0.08f, baseline), Offset(size.width * 0.92f, baseline), 2f)

        val path = Path()
        trajectory.forEachIndexed { index, point ->
            val x = size.width / 2f + (index.toFloat() / denominator - 0.5f) * radiusX * 2f
            val altitude = point.altitudeDeg.coerceIn(-10.0, 90.0)
            val y = baseline - ((altitude + 10.0) / 100.0 * amplitude).toFloat()
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, accent, style = Stroke(6f, cap = StrokeCap.Round))

        val current = trajectory.minByOrNull { abs(it.hourAngleDeg - position.hourAngleDeg) }
        if (current != null) {
            val index = trajectory.indexOf(current)
            val x = size.width / 2f + (index.toFloat() / denominator - 0.5f) * radiusX * 2f
            val altitude = current.altitudeDeg.coerceIn(-10.0, 90.0)
            val y = baseline - ((altitude + 10.0) / 100.0 * amplitude).toFloat()
            drawCircle(sun.copy(alpha = 0.13f), 32f, Offset(x, y))
            drawCircle(sun.copy(alpha = 0.20f), 21f, Offset(x, y))
            drawCircle(sun, 9f, Offset(x, y))
        }

        if (showLabels) {
            drawLine(textColor.copy(alpha = 0.45f), Offset(size.width * 0.08f, baseline - 6f), Offset(size.width * 0.08f, baseline + 6f), 1.5f)
            drawLine(textColor.copy(alpha = 0.45f), Offset(size.width * 0.92f, baseline - 6f), Offset(size.width * 0.92f, baseline + 6f), 1.5f)
        }
    }

    if (showLabels) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("日出", style = MaterialTheme.typography.labelMedium, color = textColor)
            Text("正午", style = MaterialTheme.typography.labelMedium, color = textColor)
            Text("日落", style = MaterialTheme.typography.labelMedium, color = textColor)
        }
    }
}

@Composable
fun EmptyStateCard(title: String, message: String, action: @Composable (() -> Unit)? = null) {
    LiquidGlassCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            action?.invoke()
        }
    }
}

@Composable
fun FadeSlide(visible: Boolean, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(260)) + slideInVertically(tween(300)) { it / 5 },
        exit = fadeOut(tween(180)),
    ) { content() }
}
