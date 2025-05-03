package com.muhammetkonukcu.moviefinder.lang

import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.en
import moviefinder.composeapp.generated.resources.tr
import org.jetbrains.compose.resources.StringResource

enum class AppLang(
    val code: String,
    val stringRes: StringResource
) {
    English("en", Res.string.en),
    Turkish("tr", Res.string.tr)
}