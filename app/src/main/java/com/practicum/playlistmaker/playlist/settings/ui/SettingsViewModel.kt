package com.practicum.playlistmaker.playlist.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.playlist.settings.domain.model.SettingsTheme

class SettingsViewModel(private val interactor: SettingsInteractor) : ViewModel() {
    private val _themeState = MutableLiveData<SettingsTheme>()
    val themeState: LiveData<SettingsTheme> = _themeState

    init {
        loadInitialTheme()
    }

    private fun loadInitialTheme() {
        _themeState.value = interactor.getSettingsTheme()
    }

    fun toggleTheme() {
        _themeState.value?.let { current ->
            val newTheme = current.copy(darkThemeEnabled = !current.darkThemeEnabled)
            _themeState.value = newTheme
            interactor.saveSettingsTheme(newTheme)
        }
    }
}