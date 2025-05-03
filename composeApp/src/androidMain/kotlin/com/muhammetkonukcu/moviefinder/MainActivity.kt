package com.muhammetkonukcu.moviefinder

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.muhammetkonukcu.moviefinder.screen.MainScreen
import com.muhammetkonukcu.moviefinder.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(getColor(R.color.neutral_900))
        )
        setContent {
            MainScreen()
        }
    }
}

@Preview
@Composable
private fun AppAndroidPreview() {
    AppTheme {
    }
}