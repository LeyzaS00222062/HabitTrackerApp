package com.example.habittracker.ui.theme

import android.R
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.White,
    secondary = Color(0xFFFF80AB),
    onSecondary = Color.White,
    tertiary = Color(0xFFCF9BFF),
    background = Color(0xFF1A1A1A),
    onBackground = Color.White,
    surface = Color(0xFF2D2D2D),
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFFFF80AB),
    onSecondary = Color.White,
    tertiary = Color(0xFFCF9BFF),
    background = Color(0xFFF3E8FF),
    onBackground = Color(0xFF3700B3),
    surface = Color.White,
    onSurface = Color(0xFF3700B3)
)



@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}