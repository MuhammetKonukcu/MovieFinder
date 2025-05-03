package com.muhammetkonukcu.moviefinder.util

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleLanguageCode

actual fun currentLanguageTag(): String {
    val locale = NSLocale.currentLocale
    val lang = locale.objectForKey(NSLocaleLanguageCode) as? String ?: "en"
    val country = locale.objectForKey(NSLocaleCountryCode) as? String ?: "US"
    return "$lang-$country"
}

actual fun defaultRegion(): String {
    val locale = NSLocale.currentLocale
    val country = locale.objectForKey(NSLocaleCountryCode) as? String ?: "US"
    return country.uppercase()
}