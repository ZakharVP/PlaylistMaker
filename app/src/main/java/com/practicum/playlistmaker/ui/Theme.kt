package com.practicum.playlistmaker.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF3772E7),
    onPrimary = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1B22),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1A1B22),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9FBBF3),
    onPrimary = Color(0xFF1A1B22),
    surface = Color(0xFF1A1B22),
    onSurface = Color(0xFFFFFFFF),
    background = Color(0xFF1A1B22),
    onBackground = Color(0xFFFFFFFF),
)

@Composable
fun PlaylistMakerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}