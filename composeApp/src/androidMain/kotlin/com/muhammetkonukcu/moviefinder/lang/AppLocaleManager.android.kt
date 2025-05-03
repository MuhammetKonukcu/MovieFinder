package com.muhammetkonukcu.moviefinder.lang

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

class AndroidAppLocaleManager(
    private val context: Context,
) : AppLocaleManager {

    private val localManager: Any? by lazy {
        val cls = try {
            Class.forName("android.app.LocaleManager")
        } catch (_: ClassNotFoundException) {
            null
        }
        cls?.let {
            context.getSystemService(Context.LOCATION_SERVICE)
            context.getSystemService("locale")
        }
    }

    override fun getLocale(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && localManager != null) {
            val appLocales = localManager!!.javaClass
                .getMethod("getApplicationLocales")
                .invoke(localManager) as android.os.LocaleList
            appLocales.get(0)?.toLanguageTag()?.substringBefore("-") ?: "en"
        } else {
            val tags = AppCompatDelegate.getApplicationLocales()
                .toLanguageTags()
                .split("-")
            tags.firstOrNull().orEmpty().ifEmpty { "en" }
        }
    }
}

@Composable
actual fun rememberAppLocale(): AppLang {
    val context = LocalContext.current
    val locale = AndroidAppLocaleManager(context).getLocale()
    return remember(locale) {
        locale.toApLang()
    }
}

private fun String?.toApLang(
): AppLang = when (this) {
    "tr" -> AppLang.Turkish
    else -> AppLang.English
}