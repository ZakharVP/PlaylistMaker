package com.practicum.playlistmaker

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.practicum.playlistmaker.ConstantsApp.Config
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.databaseModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.viewModelModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class PlayMarketApplication: Application() {

    private val _themeState = MutableStateFlow(false)
    val themeState: StateFlow<Boolean> = _themeState.asStateFlow()

    override fun onCreate() {
        super.onCreate()

        initTheme()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PlayMarketApplication)
            modules(
                databaseModule,
                dataModule,
                repositoryModule,
                interactorModule,
                viewModelModule
                )
        }

    }

    private fun initTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val darkThemeEnabled = sharedPreferences.getBoolean(
            Config.DARK_THEME_ENABLED,
            false
        )

        _themeState.value = darkThemeEnabled
        applyTheme(darkThemeEnabled)
    }

    fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        _themeState.value = isDarkTheme
    }
}