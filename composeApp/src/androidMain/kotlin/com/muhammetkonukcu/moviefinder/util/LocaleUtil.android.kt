package com.muhammetkonukcu.moviefinder.util

import java.util.Locale

actual fun currentLanguageTag(): String = Locale.getDefault().toLanguageTag()

actual fun defaultRegion(): String {
    return Locale.getDefault().country.uppercase().ifEmpty { "US" }
}