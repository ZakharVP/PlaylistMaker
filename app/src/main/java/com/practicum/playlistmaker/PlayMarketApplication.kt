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

    override fun onCreate() {
        super.onCreate()

        ThemeManager.initTheme(this)

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
}