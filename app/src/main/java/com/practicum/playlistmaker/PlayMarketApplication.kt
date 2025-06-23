package com.practicum.playlistmaker

import android.app.Application
import org.koin.core.context.startKoin
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.di.interactorModule
import com.practicum.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext

class PlayMarketApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PlayMarketApplication)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

    }
}