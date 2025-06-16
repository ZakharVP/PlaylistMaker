package com.practicum.playlistmaker.playlist.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.playlist.main.domain.MainInteractor

class MainViewModel ( private val interactor: MainInteractor) : ViewModel() {
    private val _currentTheme = MutableLiveData<Boolean>()
    val currentTheme: LiveData<Boolean> = _currentTheme

    init {
        loadCurrentTheme()
    }

    private fun loadCurrentTheme() {
         _currentTheme.value = interactor.getCurrentTheme()
    }
}