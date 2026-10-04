package com.localtime.feature.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.localtime.core.astronomy.SolarCalculator
import com.localtime.core.time.LocalTimeCalculator
import com.localtime.ui.components.LiquidGlassCard
import com.localtime.ui.components.SectionHeader
import java.time.LocalDate
import kotlin.math.roundToInt

private enum class CalcMode(val title: String) { LOCAL("地方时"), DELTA("经度差"), DAYLIGHT("昼长"), ZONE("时区") }

@Composable
fun CalculatorScreen(reduceMotion: Boolean) {
    var mode by remember { mutableStateOf(CalcMode.LOCAL) }
    var longitude by remember { mutableStateOf("116") }
    var referenceLongitude by remember { mutableStateOf("120") }
    var civil by remember { mutableStateOf("15:00") }
    var offset by remember { mutableStateOf("8") }
    var latitude by remember { mutableStateOf("35") }
    var dateText by remember { mutableStateOf(LocalDate.now().toString()) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SectionHeader("LEARN MODE", "地理题计算器", "不是只给答案，而是把每一步推理显示出来。")

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalcMode.entries.forEach { item ->
                FilterChip(selected = mode == item, onClick = { mode = item }, label = { Text(item.title) }, modifier = Modifier.weight(1f))
            }
        }

        when (mode) {
            CalcMode.LOCAL -> LocalCalculation(longitude, civil, offset, { longitude = it }, { civil = it }, { offset = it })
            CalcMode.DELTA -> DeltaCalculation(longitude, referenceLongitude, { longitude = it }, { referenceLongitude = it })
            CalcMode.DAYLIGHT -> DaylightCalculation(latitude, dateText, { latitude = it }, { dateText = it })
            CalcMode.ZONE -> ZoneCalculation(longitude, offset, { longitude = it }, { offset = it })
        }
    }
}

@Composable
private fun LocalCalculation(lon: String, civil: String, offset: String, setLon: (String) -> Unit, setCivil: (String) -> Unit, setOffset: (String) -> Unit) {
    Field("经度（东经为正）", lon, setLon)
    Field("区时，例如 15:00", civil, setCivil)
    Field("UTC 偏移", offset, setOffset)

    val civilMin = civilToMinutes(civil)
    val lonValue = lon.toDoubleOrNull()
    val zone = offset.toDoubleOrNull()
    if (civilMin != null && lonValue != null && zone != null) {
        val central = LocalTimeCalculator.centralLongitude(zone)
        val delta = lonValue - central
        val deltaMin = delta * 4
        val result = LocalTimeCalculator.highSchoolLocalMinutes(civilMin, lonValue, zone)
        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("计算过程", style = MaterialTheme.typography.titleMedium)
                Step("①", "中央经线", "${central.stripTrailingZero()}°")
                Step("②", "经度差", "${delta.stripTrailingZero()}°")
                Step("③", "时间差", "${delta.stripTrailingZero()}° × 4 min/° = ${deltaMin.stripTrailingZero()} min")
                Step("④", "地方时", LocalTimeCalculator.formatMinutes(result))
                Text("结论：同一时区内，向东每 1°，地方时约提前 4 分钟。", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun DeltaCalculation(lon: String, reference: String, setLon: (String) -> Unit, setReference: (String) -> Unit) {
    Field("观察地经度", lon, setLon)
    Field("参考经度", reference, setReference)
    val a = lon.toDoubleOrNull()
    val b = reference.toDoubleOrNull()
    if (a != null && b != null) {
        val delta = a - b
        val minutes = delta * 4
        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("经度实验", style = MaterialTheme.typography.titleMedium)
                Step("①", "经度差", "${delta.stripTrailingZero()}°")
                Step("②", "对应时间差", "${minutes.stripTrailingZero()} 分钟")
                Step("③", "东西早晚", if (delta >= 0) "观察地位于参考经线以东，地方时更早/更快。" else "观察地位于参考经线以西，地方时更晚/更慢。")
            }
        }
    }
}

@Composable
private fun DaylightCalculation(latitude: String, dateText: String, setLatitude: (String) -> Unit, setDate: (String) -> Unit) {
    Field("纬度", latitude, setLatitude)
    Field("日期", dateText, setDate)
    val lat = latitude.toDoubleOrNull()
    val date = runCatching { LocalDate.parse(dateText) }.getOrNull()
    if (lat != null && date != null && lat in -90.0..90.0) {
        val s = SolarCalculator.sunriseSunset(date, lat)
        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("昼夜计算", style = MaterialTheme.typography.titleMedium)
                Text("太阳赤纬 ≈ ${"%.2f".format(SolarCalculator.declinationDeg(date))}°")
                Text(when (s.state.name) {
                    "POLAR_DAY" -> "极昼：24小时白昼"
                    "POLAR_NIGHT" -> "极夜：24小时黑夜"
                    else -> "昼长：${LocalTimeCalculator.formatMinutes(s.daylightMinutes ?: 0.0)}"
                }, style = MaterialTheme.typography.headlineSmall)
                if (s.sunriseMinutes != null && s.sunsetMinutes != null) {
                    Text("日出 ${LocalTimeCalculator.formatMinutes(s.sunriseMinutes)} · 日落 ${LocalTimeCalculator.formatMinutes(s.sunsetMinutes)}")
                }
            }
        }
    }
}

@Composable
private fun ZoneCalculation(lon: String, offset: String, setLon: (String) -> Unit, setOffset: (String) -> Unit) {
    Field("经度", lon, setLon)
    Field("UTC 偏移", offset, setOffset)
    val a = lon.toDoubleOrNull()
    val b = offset.toDoubleOrNull()
    if (a != null && b != null) {
        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("时区换算", style = MaterialTheme.typography.titleMedium)
                Text("中央经线 = UTC × 15° = ${LocalTimeCalculator.centralLongitude(b).stripTrailingZero()}°")
                Text("当前经度与中央经线相差 ${(a - LocalTimeCalculator.centralLongitude(b)).stripTrailingZero()}°")
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(value, onValueChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
}

@Composable
private fun Step(index: String, title: String, result: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(index, color = MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(result, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private fun civilToMinutes(value: String): Double? = runCatching {
    val p = value.split(":")
    require(p.size == 2)
    val h = p[0].toInt()
    val m = p[1].toInt()
    require(h in 0..23 && m in 0..59)
    h * 60.0 + m
}.getOrNull()

private fun Double.stripTrailingZero(): String = if (this.roundToInt().toDouble() == this) roundToInt().toString() else "%.2f".format(java.util.Locale.US, this)
