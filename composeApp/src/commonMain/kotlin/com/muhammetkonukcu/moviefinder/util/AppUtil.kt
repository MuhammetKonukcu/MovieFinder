package com.muhammetkonukcu.moviefinder.util

import androidx.compose.runtime.Composable
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.action
import moviefinder.composeapp.generated.resources.adventure
import moviefinder.composeapp.generated.resources.animation
import moviefinder.composeapp.generated.resources.comedy
import moviefinder.composeapp.generated.resources.crime
import moviefinder.composeapp.generated.resources.department_acting
import moviefinder.composeapp.generated.resources.department_art
import moviefinder.composeapp.generated.resources.department_camera
import moviefinder.composeapp.generated.resources.department_costume_make_up
import moviefinder.composeapp.generated.resources.department_crew
import moviefinder.composeapp.generated.resources.department_directing
import moviefinder.composeapp.generated.resources.department_editing
import moviefinder.composeapp.generated.resources.department_lighting
import moviefinder.composeapp.generated.resources.department_production
import moviefinder.composeapp.generated.resources.department_sound
import moviefinder.composeapp.generated.resources.department_visual_effects
import moviefinder.composeapp.generated.resources.department_writing
import moviefinder.composeapp.generated.resources.documentary
import moviefinder.composeapp.generated.resources.drama
import moviefinder.composeapp.generated.resources.family
import moviefinder.composeapp.generated.resources.fantasy
import moviefinder.composeapp.generated.resources.history
import moviefinder.composeapp.generated.resources.horror
import moviefinder.composeapp.generated.resources.music
import moviefinder.composeapp.generated.resources.mystery
import moviefinder.composeapp.generated.resources.romance
import moviefinder.composeapp.generated.resources.sci_fiction
import moviefinder.composeapp.generated.resources.thriller
import moviefinder.composeapp.generated.resources.tv_movie
import moviefinder.composeapp.generated.resources.war
import moviefinder.composeapp.generated.resources.western
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

val genreIds: List<Int> = listOf(
    12,
    14,
    16,
    18,
    27,
    28,
    35,
    36,
    37,
    53,
    80,
    99,
    878,
    9648,
    10402,
    10749,
    10751,
    10752,
    10770,
)

@Composable
fun getGenreMap(): Map<Int, String> {
    return mapOf(
        12 to stringResource(Res.string.adventure),
        14 to stringResource(Res.string.fantasy),
        16 to stringResource(Res.string.animation),
        18 to stringResource(Res.string.drama),
        27 to stringResource(Res.string.horror),
        28 to stringResource(Res.string.action),
        35 to stringResource(Res.string.comedy),
        36 to stringResource(Res.string.history),
        37 to stringResource(Res.string.western),
        53 to stringResource(Res.string.thriller),
        80 to stringResource(Res.string.crime),
        99 to stringResource(Res.string.documentary),
        878 to stringResource(Res.string.sci_fiction),
        9648 to stringResource(Res.string.mystery),
        10402 to stringResource(Res.string.music),
        10749 to stringResource(Res.string.romance),
        10751 to stringResource(Res.string.family),
        10752 to stringResource(Res.string.war),
        10770 to stringResource(Res.string.tv_movie),
    )
}

fun getDepartment(department: String): StringResource {
    val deptKey = department.lowercase()
    val deptRes = when (deptKey) {
        "acting"           -> Res.string.department_acting
        "directing"        -> Res.string.department_directing
        "writing"          -> Res.string.department_writing
        "production"       -> Res.string.department_production
        "editing"          -> Res.string.department_editing
        "camera"           -> Res.string.department_camera
        "sound"            -> Res.string.department_sound
        "visual effects"   -> Res.string.department_visual_effects
        "art"              -> Res.string.department_art
        "costume & make-up"-> Res.string.department_costume_make_up
        "lighting"         -> Res.string.department_lighting
        "crew"             -> Res.string.department_crew
        else               -> Res.string.department_crew
    }
    return deptRes
}

fun List<Int>.toGenreNames(genreMap: Map<Int, String>): List<String> =
    this.mapNotNull { genreMap[it] }