package com.muhammetkonukcu.moviefinder.di

import com.muhammetkonukcu.moviefinder.database.getDatabase
import com.muhammetkonukcu.moviefinder.room.database.AppDatabase
import org.koin.dsl.module


actual fun platformModule() = module {
    single<AppDatabase> { getDatabase(get()) }
}