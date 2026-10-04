package com.localtime.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.North
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.localtime.core.astronomy.SolarCalculator
import com.localtime.core.model.GeoCoordinate
import com.localtime.core.model.LocationSource
import com.localtime.core.time.LocalTimeCalculator
import com.localtime.ui.components.AnimatedNumber
import com.localtime.ui.components.LiquidGlassCard
import com.localtime.ui.components.Metric
import com.localtime.ui.components.SectionHeader
import com.localtime.ui.components.SolarArc
import java.time.Instant
import java.time.ZoneId
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    coordinate: GeoCoordinate,
    locationSource: LocationSource,
    reduceMotion: Boolean,
    onLocationClick: () -> Unit,
) {
    var now by remember { mutableStateOf(Instant.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Instant.now()
            kotlinx.coroutines.delay(1000)
        }
    }

    val zone = ZoneId.systemDefault()
    val snap = SolarCalculator.coordinateTimeSnapshot(now, zone, coordinate)
    val civilMinutes = LocalTimeCalculator.minutesOfDay(snap.civilTime.toLocalTime())
    val highSchoolMinutes = LocalTimeCalculator.highSchoolLocalMinutes(civilMinutes, coordinate.longitudeDeg, LocalTimeCalculator.zoneOffsetHours(snap.civilTime))
    val date = snap.civilTime.toLocalDate()
    val position = SolarCalculator.solarPosition(date, snap.localMeanMinutes, coordinate.latitudeDeg)
    val trajectory = remember(date, coordinate.latitudeDeg) { SolarCalculator.trajectory(date, coordinate.latitudeDeg) }
    val sunCycle = SolarCalculator.sunriseSunset(date, coordinate.latitudeDeg)
    val meanSolar = LocalTimeCalculator.formatMinutes(snap.localMeanMinutes)
    val apparent = LocalTimeCalculator.formatMinutes(snap.localApparentMinutes ?: snap.localMeanMinutes)
    val schoolLocal = LocalTimeCalculator.formatMinutes(highSchoolMinutes)
    val zoneDelta = LocalTimeCalculator.longitudeDeltaMinutes(coordinate.longitudeDeg, LocalTimeCalculator.zoneOffsetHours(snap.civilTime))

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text("LOCAL TIME", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, letterSpacing = 1.7.sp)
                Text("地方时间实验室", style = MaterialTheme.typography.headlineSmall)
                Text("把经度变成看得见的时间差。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onLocationClick) {
                Icon(Icons.Outlined.GpsFixed, contentDescription = "选择观察地点")
                Text(if (locationSource == LocationSource.GPS) "定位" else "手动")
            }
        }

        LiquidGlassCard(
            modifier = Modifier.fillMaxWidth(),
            tint = if (position.altitudeDeg >= 0) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("观察地点", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(formatCoordinate(coordinate), style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    }
                    Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                }
                Text("地方时", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                AnimatedNumber(schoolLocal, reduceMotion = reduceMotion)
                Text("高中地理计算意义上的地方时", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Metric("区时", snap.civilTime.toLocalTime().withNano(0).toString(), modifier = Modifier.weight(1f))
                    Metric("经度时差", formatSignedMinutes(zoneDelta), "中央经线 ${LocalTimeCalculator.centralLongitude(LocalTimeCalculator.zoneOffsetHours(snap.civilTime)).roundToInt()}°", modifier = Modifier.weight(1f))
                }
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(top = 18.dp, bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("太阳实时轨迹", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                        Text(if (position.altitudeDeg >= 0) "太阳在地平线以上" else "太阳已落地平线", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(if (position.altitudeDeg >= 0) Icons.Outlined.LightMode else Icons.Outlined.North, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                }
                SolarArc(position, trajectory, Modifier.fillMaxWidth(), showLabels = true)
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LiquidGlassCard(Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Metric("太阳高度", "${position.altitudeDeg.roundToInt()}°", "地平线以上为正")
                }
            }
            LiquidGlassCard(Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Metric("太阳方位", "${position.azimuthDeg.roundToInt()}°", "北=0° 顺时针")
                }
            }
        }

        SectionHeader("时间尺度", "同一时刻，三种时间")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LiquidGlassCard(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Metric("平均太阳时", meanSolar) } }
            LiquidGlassCard(Modifier.weight(1f)) { Column(Modifier.padding(16.dp)) { Metric("太阳视时", apparent, "含均时差") } }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("太阳日程", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    DayTime("日出", sunCycle.sunriseMinutes?.let(LocalTimeCalculator::formatMinutes) ?: "极区")
                    DayTime("正午", "12:00")
                    DayTime("日落", sunCycle.sunsetMinutes?.let(LocalTimeCalculator::formatMinutes) ?: "极区")
                }
                Text(
                    when (sunCycle.state.name) {
                        "POLAR_DAY" -> "当前纬度与日期形成极昼：全天太阳不落。"
                        "POLAR_NIGHT" -> "当前纬度与日期形成极夜：全天太阳不升。"
                        else -> "昼长约 ${LocalTimeCalculator.formatMinutes(sunCycle.daylightMinutes ?: 0.0)}。"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("地理解释", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                Text("太阳赤纬 ${"%.2f".format(position.declinationDeg)}° · 直射点纬度近似为太阳赤纬。", style = MaterialTheme.typography.bodyMedium)
                Text("太阳视时 = 平均太阳时 + 均时差；地方时则按高中地理经度差 × 4 分钟计算。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun DayTime(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
    }
}

private fun formatCoordinate(c: GeoCoordinate): String =
    "%.2f°%s  %.2f°%s".format(java.util.Locale.US, abs(c.latitudeDeg), if (c.latitudeDeg >= 0) "N" else "S", abs(c.longitudeDeg), if (c.longitudeDeg >= 0) "E" else "W")

private fun formatSignedMinutes(minutes: Double): String =
    "%s%d分".format(java.util.Locale.CHINA, if (minutes >= 0) "+" else "−", abs(minutes).roundToInt())
