package com.localtime.feature.sky

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localtime.core.astronomy.SolarCalculator
import com.localtime.core.model.GeoCoordinate
import com.localtime.core.time.LocalTimeCalculator
import com.localtime.ui.components.LiquidGlassCard
import com.localtime.ui.components.Metric
import com.localtime.ui.components.SectionHeader
import com.localtime.ui.components.SolarArc
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun SkyScreen(coordinate: GeoCoordinate, reduceMotion: Boolean) {
    var date by remember { mutableStateOf(LocalDate.now()) }
    var minute by remember { mutableFloatStateOf(720f) }
    val position = SolarCalculator.solarPosition(date, minute.toDouble(), coordinate.latitudeDeg)
    val trajectory = remember(date, coordinate.latitudeDeg) { SolarCalculator.trajectory(date, coordinate.latitudeDeg) }
    val sun = SolarCalculator.sunriseSunset(date, coordinate.latitudeDeg)

    Column(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    MaterialTheme.colorScheme.background,
                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f),
                )
            )
        ).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SectionHeader("SKY LAB", "天空实验室", "拖动时间和日期，让太阳的运动成为一条可操作的轨迹。")

        LiquidGlassCard(Modifier.fillMaxWidth(), tint = if (position.altitudeDeg >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("模拟时间", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(LocalTimeCalculator.formatMinutes(minute.toDouble()), fontSize = 40.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                    Icon(Icons.Outlined.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(30.dp))
                }
                Text("太阳高度 ${position.altitudeDeg.roundToInt()}° · 方位 ${position.azimuthDeg.roundToInt()}° · 赤纬 ${"%.2f".format(position.declinationDeg)}°")
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                SolarArc(position, trajectory, Modifier.fillMaxWidth())
                Slider(
                    value = minute,
                    onValueChange = { minute = it },
                    valueRange = 0f..1439f,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !reduceMotion || true,
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("00:00", style = MaterialTheme.typography.labelSmall)
                    Text("12:00", style = MaterialTheme.typography.labelSmall)
                    Text("23:59", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = { date = date.minusDays(1) }) { Icon(Icons.Outlined.ChevronLeft, contentDescription = "前一天") }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(date.toString(), style = MaterialTheme.typography.titleMedium)
                Text("太阳赤纬随日期变化", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = { date = date.plusDays(1) }) { Icon(Icons.Outlined.ChevronRight, contentDescription = "后一天") }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LiquidGlassCard(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Metric("日出", sun.sunriseMinutes?.let(LocalTimeCalculator::formatMinutes) ?: "极区") } }
            LiquidGlassCard(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Metric("日落", sun.sunsetMinutes?.let(LocalTimeCalculator::formatMinutes) ?: "极区") } }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("直射点", style = MaterialTheme.typography.titleMedium)
                Text("纬度约 ${"%.2f".format(position.declinationDeg)}°")
                Text("太阳直射点随日期南北移动，随地球自转东西移动。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
