package com.practicum.playlistmaker

import android.content.Context
import android.content.Context.MODE_PRIVATE

import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS
import com.practicum.playlistmaker.ConstantsApp.PLAYLIST_SETTINGS_THEME_NIGHT_VALUE


object ThemeManager {

    fun saveThemeToPreferences(context: Context, isNightMode: Boolean){
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, MODE_PRIVATE)

        sharedPreferences.edit()
            .putBoolean(PLAYLIST_SETTINGS_THEME_NIGHT_VALUE, isNightMode)
            .apply()
    }

    fun getThemeFromPreferences(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_SETTINGS, MODE_PRIVATE)
        return sharedPreferences.getBoolean(PLAYLIST_SETTINGS_THEME_NIGHT_VALUE, false)
    }

}