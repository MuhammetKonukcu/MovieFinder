package com.muhammetkonukcu.moviefinder.di

import com.muhammetkonukcu.moviefinder.remote.TmdbClient
import com.muhammetkonukcu.moviefinder.repository.MovieRepository
import com.muhammetkonukcu.moviefinder.repository.MovieRepositoryImpl
import com.muhammetkonukcu.moviefinder.util.currentLanguageTag
import com.muhammetkonukcu.moviefinder.util.defaultRegion
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun networkModule(apiKey: String): Module = module {
    single<HttpClient> {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
    single {
        TmdbClient(
            apiKey = apiKey,
            httpClient = get(),
            defaultRegion = defaultRegion(),
            defaultLanguage = currentLanguageTag(),
        )
    }
}

fun repositoryModule(): Module = module {
    single<MovieRepository> { MovieRepositoryImpl(get<TmdbClient>()) }
}