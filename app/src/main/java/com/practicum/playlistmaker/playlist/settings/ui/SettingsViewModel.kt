package com.practicum.playlistmaker.playlist.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.playlist.settings.domain.model.SettingsTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(private val interactor: SettingsInteractor) : ViewModel() {
    private val _themeState = MutableStateFlow(SettingsTheme(false))
    val themeState: StateFlow<SettingsTheme> = _themeState.asStateFlow()

    init {
        loadInitialTheme()
    }

    private fun loadInitialTheme() {
        val currentTheme = interactor.getSettingsTheme()
        _themeState.value = currentTheme
    }

    fun toggleTheme() {
        val current = _themeState.value
        val newTheme = current.copy(darkThemeEnabled = !current.darkThemeEnabled)
        _themeState.value = newTheme
        interactor.saveSettingsTheme(newTheme)
    }
}