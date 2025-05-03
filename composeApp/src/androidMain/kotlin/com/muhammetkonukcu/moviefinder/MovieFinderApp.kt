package com.muhammetkonukcu.moviefinder

import android.app.Application
import com.muhammetkonukcu.moviefinder.di.initKoin
import org.koin.android.ext.koin.androidContext

class MovieFinderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MovieFinderApp)
        }
    }
}