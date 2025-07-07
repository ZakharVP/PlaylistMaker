package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import org.koin.core.context.startKoin
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext

class PlayMarketApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initTheme()

        startKoin {
            androidContext(this@PlayMarketApplication)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

    }

    private fun initTheme() {
        // Получаем SharedPreferences
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        // Проверяем сохраненную тему (по умолчанию светлая)
        val darkThemeEnabled = sharedPreferences.getBoolean("dark_theme_enabled", false)

        // Устанавливаем тему приложения
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}