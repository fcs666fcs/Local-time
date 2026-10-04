package com.localtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.localtime.ui.navigation.LocalTimeApp
import com.localtime.ui.theme.LocalTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            LocalTimeTheme {
                LocalTimeApp()
            }
        }
    }
}
