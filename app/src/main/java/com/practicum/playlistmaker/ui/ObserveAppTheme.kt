package com.practicum.playlistmaker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.playlistmaker.PlayMarketApplication

@Composable
fun ObserveAppTheme(content: @Composable (Boolean) -> Unit) {
    val app = LocalContext.current.applicationContext as PlayMarketApplication
    val themeState by app.themeState.collectAsStateWithLifecycle()

    content(themeState)
}