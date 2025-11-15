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

    fun getCurrentTheme(): SettingsTheme {
        return interactor.getSettingsTheme()
    }

    fun toggleTheme() {
        val currentTheme = getCurrentTheme()
        val newTheme = currentTheme.copy(darkThemeEnabled = !currentTheme.darkThemeEnabled)
        interactor.saveSettingsTheme(newTheme)
    }
}