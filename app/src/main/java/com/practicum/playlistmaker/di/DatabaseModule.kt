package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.db.data.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database.db"
        ).build()
    }

    single { get<AppDatabase>().trackDao() }
    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistTrackDao() }
}