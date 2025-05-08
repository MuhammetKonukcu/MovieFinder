package com.muhammetkonukcu.moviefinder.di

import com.muhammetkonukcu.moviefinder.BuildKonfig
import com.muhammetkonukcu.moviefinder.room.dao.HistoryDao
import com.muhammetkonukcu.moviefinder.room.dao.MovieDao
import com.muhammetkonukcu.moviefinder.room.database.AppDatabase
import com.muhammetkonukcu.moviefinder.room.repository.MovieLocalRepository
import com.muhammetkonukcu.moviefinder.room.repository.MovieLocalRepositoryImpl
import com.muhammetkonukcu.moviefinder.room.repository.SearchHistoryRepository
import com.muhammetkonukcu.moviefinder.room.repository.SearchHistoryRepositoryImpl
import com.muhammetkonukcu.moviefinder.viewmodel.BookmarkViewModel
import com.muhammetkonukcu.moviefinder.viewmodel.MoviesDetailViewModel
import com.muhammetkonukcu.moviefinder.viewmodel.MoviesViewModel
import com.muhammetkonukcu.moviefinder.viewmodel.ProfileViewModel
import com.muhammetkonukcu.moviefinder.viewmodel.SearchViewModel
import com.muhammetkonukcu.moviefinder.viewmodel.SeriesDetailViewModel
import com.muhammetkonukcu.moviefinder.viewmodel.SeriesViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun appModule(): Module = module {
    single { MoviesViewModel(get()) }
    single { SeriesViewModel(get()) }
    single { BookmarkViewModel(get()) }
    single { SearchViewModel(get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { MoviesDetailViewModel(get(), get()) }
    viewModel { SeriesDetailViewModel(get(), get()) }
}

fun localRepositoryModule(): Module = module {
    single<MovieDao>     { get<AppDatabase>().getMovieDao() }
    single<HistoryDao> { get<AppDatabase>().getHistoryDao() }

    single<MovieLocalRepository>     { MovieLocalRepositoryImpl(get()) }
    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get()) }
}

expect fun platformModule(): Module

fun initKoin(config: KoinAppDeclaration? = null) =
    startKoin {
        config?.invoke(this)

        modules(
            appModule(),
            platformModule(),
            repositoryModule(),
            localRepositoryModule(),
            networkModule(apiKey = BuildKonfig.tmdb_auth_token)
        )
    }