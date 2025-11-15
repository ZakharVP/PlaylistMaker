package com.practicum.playlistmaker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.playlistmaker.ThemeManager
import com.practicum.playlistmaker.playlist.settings.ui.SettingsViewModel

@Composable
fun ObserveAppTheme(content: @Composable (Boolean) -> Unit) {
    val isDarkTheme = produceState(initialValue = ThemeManager.themeState.value) {
        ThemeManager.themeState.collect { value = it }
    }.value

    content(isDarkTheme)
}