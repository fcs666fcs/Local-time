package com.localtime.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.localtime.ui.components.LiquidGlassCard
import com.localtime.ui.components.SectionHeader
import com.localtime.ui.theme.ThemeMode

@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    reduceMotion: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onReduceMotionChange: (Boolean) -> Unit,
    onLocationClick: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SectionHeader("SETTINGS", "设置", "把显示、定位和动画全部交给你控制。")

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("主题", style = MaterialTheme.typography.titleMedium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { item ->
                        FilterChip(selected = item == themeMode, onClick = { onThemeModeChange(item) }, label = { Text(themeLabel(item)) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                    Column {
                        Text("减少动态效果", style = MaterialTheme.typography.titleMedium)
                        Text("减少位移动画与持续光效，但不隐藏任何数据。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Switch(checked = reduceMotion, onCheckedChange = onReduceMotionChange)
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("观察地点", style = MaterialTheme.typography.titleMedium)
                        Text("GPS 与手动经纬度并列可用，不启用后台持续定位。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                androidx.compose.material3.TextButton(onClick = onLocationClick) { Text("管理") }
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("离线能力与隐私", style = MaterialTheme.typography.titleMedium)
                }
                Text("地方时、平均太阳时、太阳位置、日出日落和地理示意均在本机计算。网络数据只负责增强信息，不参与核心数学模型。", style = MaterialTheme.typography.bodyMedium)
            }
        }

        LiquidGlassCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("关于地方时时钟", style = MaterialTheme.typography.titleMedium)
                Text("Local Time · 高中地理学习型 Android App", style = MaterialTheme.typography.bodyMedium)
                Text("版本 1.1 · 视觉系统：Liquid Glass 风格 · Material 3", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun themeLabel(mode: ThemeMode): String = when (mode) {
    ThemeMode.SYSTEM -> "跟随系统"
    ThemeMode.LIGHT -> "浅色"
    ThemeMode.DARK -> "深色"
}
