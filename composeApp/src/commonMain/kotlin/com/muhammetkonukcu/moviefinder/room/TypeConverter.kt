package com.muhammetkonukcu.moviefinder.room

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromIntList(value: List<Int>?): String? =
        value?.joinToString(separator = ",")

    @TypeConverter
    fun toIntList(value: String?): List<Int>? =
        value
            ?.takeIf { it.isNotEmpty() }
            ?.split(",")
            ?.mapNotNull { it.toIntOrNull() }
}
