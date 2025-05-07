package com.muhammetkonukcu.moviefinder.util

import platform.Foundation.NSDate
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.NSLocaleCountryCode
import platform.Foundation.NSLocaleLanguageCode
import platform.Foundation.timeIntervalSince1970

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

actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}