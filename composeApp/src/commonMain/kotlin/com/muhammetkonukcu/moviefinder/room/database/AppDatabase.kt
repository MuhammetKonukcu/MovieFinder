package com.muhammetkonukcu.moviefinder.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.muhammetkonukcu.moviefinder.room.Converters
import com.muhammetkonukcu.moviefinder.room.dao.HistoryDao
import com.muhammetkonukcu.moviefinder.room.dao.MovieDao
import com.muhammetkonukcu.moviefinder.room.entity.HistoryEntity
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity

@Database(entities = [MovieEntity::class, HistoryEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMovieDao(): MovieDao
    abstract fun getHistoryDao(): HistoryDao
}