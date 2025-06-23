package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.playlist.search.data.network.ItunesApplicationApi
import com.practicum.playlistmaker.playlist.search.data.network.NetworkClient
import com.practicum.playlistmaker.playlist.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.playlist.search.data.sharedprefs.SearchHistoryStorage
import com.practicum.playlistmaker.playlist.settings.data.datasource.ThemePreferencesDataSource
import com.practicum.playlistmaker.util.AndroidNetworkChecker
import com.practicum.playlistmaker.util.NetworkChecker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val dataModule = module {
    single<NetworkClient> { RetrofitNetworkClient() }
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single<ItunesApplicationApi> { get<Retrofit>().create(ItunesApplicationApi::class.java) }
    single { SearchHistoryStorage(androidContext()) }
    single<NetworkChecker> { AndroidNetworkChecker(androidContext()) }
    single {
        ThemePreferencesDataSource(
            androidContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        )
    }
}