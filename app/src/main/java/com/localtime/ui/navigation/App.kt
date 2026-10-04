package com.localtime.ui.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import com.localtime.core.model.GeoCoordinate
import com.localtime.core.model.LocationSource
import com.localtime.feature.calculator.CalculatorScreen
import com.localtime.feature.geography.GeographyScreen
import com.localtime.feature.home.HomeScreen
import com.localtime.feature.settings.SettingsScreen
import com.localtime.feature.sky.SkyScreen
import com.localtime.ui.components.GlassPill
import com.localtime.ui.theme.LocalTimeTheme
import com.localtime.ui.theme.ThemeMode
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

private enum class Destination(val title: String) {
    HOME("主页"), CALCULATOR("计算"), SKY("天空"), GEOGRAPHY("地理"), SETTINGS("设置"),
}

@Composable
fun LocalTimeApp() {
    var current by rememberSaveable { mutableStateOf(Destination.HOME.name) }
    var latitude by rememberSaveable { mutableStateOf(25.03) }
    var longitude by rememberSaveable { mutableStateOf(121.57) }
    var source by rememberSaveable { mutableStateOf(LocationSource.MANUAL.name) }
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.SYSTEM.name) }
    var reduceMotion by rememberSaveable { mutableStateOf(false) }
    var showLocation by rememberSaveable { mutableStateOf(false) }
    var showWelcome by rememberSaveable { mutableStateOf(true) }

    LocalTimeTheme(
        themeMode = ThemeMode.valueOf(themeMode),
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                LiquidNavigationBar(
                    selected = Destination.valueOf(current),
                    onSelect = { current = it.name },
                )
            },
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
                when (Destination.valueOf(current)) {
                    Destination.HOME -> HomeScreen(
                        coordinate = GeoCoordinate(latitude, longitude),
                        locationSource = LocationSource.valueOf(source),
                        reduceMotion = reduceMotion,
                        onLocationClick = { showLocation = true },
                    )
                    Destination.CALCULATOR -> CalculatorScreen(reduceMotion = reduceMotion)
                    Destination.SKY -> SkyScreen(coordinate = GeoCoordinate(latitude, longitude), reduceMotion = reduceMotion)
                    Destination.GEOGRAPHY -> GeographyScreen(coordinate = GeoCoordinate(latitude, longitude), reduceMotion = reduceMotion)
                    Destination.SETTINGS -> SettingsScreen(
                        themeMode = ThemeMode.valueOf(themeMode),
                        reduceMotion = reduceMotion,
                        onThemeModeChange = { themeMode = it.name },
                        onReduceMotionChange = { reduceMotion = it },
                        onLocationClick = { showLocation = true },
                    )
                }
            }
        }

        if (showLocation) {
            LocationSheet(
                coordinate = GeoCoordinate(latitude, longitude),
                onDismiss = { showLocation = false },
                onCoordinateChange = {
                    latitude = it.latitudeDeg
                    longitude = it.longitudeDeg
                    source = LocationSource.MANUAL.name
                },
                onGpsSuccess = {
                    latitude = it.latitudeDeg
                    longitude = it.longitudeDeg
                    source = LocationSource.GPS.name
                    showLocation = false
                },
            )
        }

        if (showWelcome) {
            WelcomeOverlay(onDismiss = { showWelcome = false }, reduceMotion = reduceMotion)
        }
    }
}

@Composable
private fun LiquidNavigationBar(
    selected: Destination,
    onSelect: (Destination) -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .shadow(18.dp, RoundedCornerShape(28.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.68f),
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(28.dp),
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Destination.entries.forEach { destination ->
                val chosen = destination == selected
                GlassPill(
                    modifier = Modifier.weight(1f).padding(horizontal = 3.dp).clickable { onSelect(destination) },
                    selected = chosen,
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Icon(navIcon(destination), contentDescription = destination.title, tint = if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(destination.title, style = MaterialTheme.typography.labelSmall, color = if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

private fun navIcon(destination: Destination) = when (destination) {
    Destination.HOME -> Icons.Outlined.AccessTime
    Destination.CALCULATOR -> Icons.Outlined.Calculate
    Destination.SKY -> Icons.Outlined.WbSunny
    Destination.GEOGRAPHY -> Icons.Outlined.Public
    Destination.SETTINGS -> Icons.Outlined.Settings
}

@Composable
private fun LocationSheet(
    coordinate: GeoCoordinate,
    onDismiss: () -> Unit,
    onCoordinateChange: (GeoCoordinate) -> Unit,
    onGpsSuccess: (GeoCoordinate) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var lat by remember(coordinate) { mutableStateOf("%.4f".format(Locale.US, coordinate.latitudeDeg)) }
    var lon by remember(coordinate) { mutableStateOf("%.4f".format(Locale.US, coordinate.longitudeDeg)) }
    var error by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true || grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) fetchCurrentLocation(context, onGpsSuccess, { error = it })
        else error = "未获得定位权限，可以继续使用手动经纬度。"
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text("观察地点", style = MaterialTheme.typography.headlineSmall)
            Text("定位和手动输入必须同样可用。核心地方时计算完全可以离线运行。", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
                onClick = {
                    val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (fine || coarse) fetchCurrentLocation(context, onGpsSuccess, { error = it })
                    else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("使用当前位置") }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(lat, { lat = it }, label = { Text("纬度") }, modifier = Modifier.weight(1f))
                OutlinedTextField(lon, { lon = it }, label = { Text("经度") }, modifier = Modifier.weight(1f))
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = {
                    val a = lat.toDoubleOrNull()
                    val b = lon.toDoubleOrNull()
                    if (a == null || b == null || a !in -90.0..90.0 || b !in -180.0..180.0) {
                        error = "请输入合法范围：纬度 -90°～90°，经度 -180°～180°。"
                    } else {
                        onCoordinateChange(GeoCoordinate(a, b))
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("应用手动位置") }
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("取消") }
        }
    }
}

private fun fetchCurrentLocation(
    context: Context,
    onSuccess: (GeoCoordinate) -> Unit,
    onError: (String) -> Unit,
) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    try {
        client.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(GeoCoordinate(location.latitude, location.longitude, location.accuracy.toDouble()))
            } else {
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                    .addOnSuccessListener { fresh ->
                        if (fresh != null) onSuccess(GeoCoordinate(fresh.latitude, fresh.longitude, fresh.accuracy.toDouble()))
                        else onError("暂时无法获取位置，请检查系统定位开关。")
                    }
                    .addOnFailureListener { onError("定位失败：${it.message ?: "未知错误"}") }
            }
        }.addOnFailureListener { onError("定位失败：${it.message ?: "未知错误"}") }
    } catch (_: SecurityException) {
        onError("尚未授予定位权限。")
    }
}

@Composable
private fun WelcomeOverlay(onDismiss: () -> Unit, reduceMotion: Boolean) {
    LaunchedEffect(Unit) {
        if (!reduceMotion) {
            kotlinx.coroutines.delay(2200)
            onDismiss()
        }
    }
    Box(
        Modifier.fillMaxSize().background(
            androidx.compose.ui.graphics.Brush.radialGradient(
                listOf(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.32f),
                    MaterialTheme.colorScheme.background.copy(alpha = 0.98f),
                )
            )
        ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(Icons.Outlined.WbSunny, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
            Text("给每一个选择地理的同学", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Text("你看到的是时间，\n但地理告诉你，时间从来不只是钟表上的数字。", style = MaterialTheme.typography.headlineSmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("愿你看懂经纬线，也看懂这个世界。", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onDismiss) { Text("跳过") }
        }
    }
}
