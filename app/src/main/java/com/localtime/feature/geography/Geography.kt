package com.localtime.feature.geography

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.localtime.core.astronomy.SolarCalculator
import com.localtime.core.model.GeoCoordinate
import com.localtime.core.model.DaylightState
import com.localtime.ui.components.LiquidGlassCard
import com.localtime.ui.components.Metric
import com.localtime.ui.components.SectionHeader
import java.time.LocalDate
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GeographyScreen(coordinate: GeoCoordinate, reduceMotion: Boolean) {
    val date = LocalDate.now()
    val dec = SolarCalculator.declinationDeg(date)
    val sun = SolarCalculator.sunriseSunset(date, coordinate.latitudeDeg)

    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader("EARTH LAB", "地理可视化", "把直射点、昼夜和晨昏线放进同一个观察框架。")

        LiquidGlassCard(Modifier.fillMaxWidth(), tint = MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.padding(16.dp)) {
                Text("昼夜与晨昏线", style = MaterialTheme.typography.titleMedium)
                EarthDiagram(coordinate = coordinate, declination = dec)
                Text("二维教学示意：亮区表示白昼，暗区表示黑夜，中间的弧线表示晨昏线。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LiquidGlassCard(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Metric("直射纬度", "%.2f°".format(dec), "≈ 太阳赤纬") } }
            LiquidGlassCard(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Metric("观察纬度", "%.2f°".format(coordinate.latitudeDeg), "当前位置") } }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("昼夜状态", style = MaterialTheme.typography.titleMedium)
                Text(daylightText(sun.state))
                if (sun.daylightMinutes != null) Text("昼长：${(sun.daylightMinutes / 60.0).toInt()}小时${(sun.daylightMinutes % 60).toInt()}分钟")
                Text("极昼/极夜并不是“算错了”，而是该日期和纬度的太阳高度全天不跨过地平线。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun EarthDiagram(coordinate: GeoCoordinate, declination: Double) {
    val land = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    val sea = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    val night = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.30f)
    val terminator = MaterialTheme.colorScheme.tertiary
    val observer = MaterialTheme.colorScheme.primary

    Canvas(Modifier.fillMaxWidth().height(290.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width * 0.38f, size.height * 0.40f)

        drawCircle(
            brush = Brush.radialGradient(listOf(land.copy(alpha = 0.34f), sea.copy(alpha = 0.10f))),
            radius = radius,
            center = center,
        )
        drawCircle(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f), radius = radius, center = center, style = Stroke(2f))

        val tilt = declination / 23.5 * 0.7
        val path = Path()
        for (i in 0..80) {
            val t = i / 80f
            val x = center.x - radius + 2 * radius * t
            val y = center.y + (sin((t - 0.5f) * Math.PI).toFloat() * radius * tilt)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, terminator, style = Stroke(5f))

        drawCircle(night, radius * 0.98f, Offset(center.x + radius * 0.22f, center.y + radius * 0.06f))
        drawCircle(color = observer, radius = 7f, center = Offset(
            center.x + (coordinate.longitudeDeg / 180.0 * radius).toFloat(),
            center.y - (coordinate.latitudeDeg / 90.0 * radius).toFloat(),
        ))
        drawCircle(color = MaterialTheme.colorScheme.tertiary, radius = 9f, center = Offset(center.x - radius * 0.30f, center.y - radius * 0.34f))
    }
}

private fun daylightText(state: DaylightState): String = when (state) {
    DaylightState.NORMAL -> "常规昼夜交替"
    DaylightState.POLAR_DAY -> "极昼"
    DaylightState.POLAR_NIGHT -> "极夜"
}
