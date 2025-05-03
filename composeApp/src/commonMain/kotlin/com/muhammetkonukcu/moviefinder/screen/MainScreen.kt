package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.muhammetkonukcu.moviefinder.lang.AppLang
import com.muhammetkonukcu.moviefinder.lang.rememberAppLocale
import com.muhammetkonukcu.moviefinder.model.BottomNavModel
import com.muhammetkonukcu.moviefinder.theme.AppPrimaryColor
import com.muhammetkonukcu.moviefinder.theme.AppTheme
import io.ktor.http.decodeURLQueryComponent
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.bookmark
import moviefinder.composeapp.generated.resources.magnifying_glass_fill
import moviefinder.composeapp.generated.resources.movie
import moviefinder.composeapp.generated.resources.ph_bookmark
import moviefinder.composeapp.generated.resources.ph_bookmark_fill
import moviefinder.composeapp.generated.resources.ph_film_slate
import moviefinder.composeapp.generated.resources.ph_film_slate_fill
import moviefinder.composeapp.generated.resources.ph_magnifying_glass
import moviefinder.composeapp.generated.resources.ph_television_simple
import moviefinder.composeapp.generated.resources.ph_television_simple_fill
import moviefinder.composeapp.generated.resources.search
import moviefinder.composeapp.generated.resources.series
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

val LocalAppLocalization = compositionLocalOf {
    AppLang.English
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@Preview
fun MainScreen() {
    val currentLanguage = rememberAppLocale()
    CompositionLocalProvider(LocalAppLocalization provides currentLanguage) {
        AppTheme {
            Surface(
                modifier = Modifier.fillMaxSize().navigationBarsPadding(),
                color = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    bottomBar = {
                        if (currentRoute in listOf("Movie", "Series", "Search", "Bookmark")) {
                            BottomNavigation(navController)
                        }
                    }
                ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = "Movie",
                            ) {
                                composable("Movie") {
                                    MoviesScreen(
                                        navController = navController,
                                        innerPadding = innerPadding
                                    )
                                }
                                composable("Series") {
                                    SeriesScreen(
                                        navController = navController,
                                        innerPadding = innerPadding
                                    )
                                }
                                composable("Search") {
                                    SearchScreen(
                                        navController = navController,
                                        innerPadding = innerPadding
                                    )
                                }
                                composable("Bookmark") {
                                    BookmarkScreen(
                                        navController = navController,
                                        innerPadding= innerPadding
                                    )
                                }

                                composable(
                                    route = "SeeAllMovies/{listId}",
                                    arguments = listOf(
                                        navArgument("listId") { type = NavType.IntType },
                                    )
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("listId")!!
                                    SeeAllMoviesScreen(navController = navController, listId = id)
                                }

                                composable(
                                    route = "SeeAllSeries/{listId}",
                                    arguments = listOf(
                                        navArgument("listId") { type = NavType.IntType },
                                    )
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("listId")!!
                                    SeeAllSeriesScreen(navController = navController, listId = id)
                                }

                                composable(
                                    route = "MovieDetail/{itemId}",
                                    arguments = listOf(
                                        navArgument("itemId") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("itemId")!!
                                    MovieDetailScreen(
                                        navController = navController,
                                        itemId = id,
                                    )
                                }

                                composable(
                                    route = "SeriesDetail/{itemId}",
                                    arguments = listOf(
                                        navArgument("itemId") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("itemId")!!
                                    SeriesDetailScreen(
                                        navController = navController,
                                        itemId = id,
                                    )
                                }

                                composable(
                                    route = "Profile/{itemId}",
                                    arguments = listOf(
                                        navArgument("itemId") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val id = backStackEntry.arguments?.getInt("itemId")!!
                                    ProfileScreen(
                                        navController = navController,
                                        itemId = id
                                    )
                                }

                                composable(
                                    route = "ImageDetail/{title}/{imageList}/{index}",
                                    arguments = listOf(
                                        navArgument("title") { type = NavType.StringType },
                                        navArgument("imageList") { type = NavType.StringType },
                                        navArgument("index") { type = NavType.IntType }
                                    )
                                ) { backStackEntry ->
                                    val title = backStackEntry.arguments?.getString("title") ?: ""
                                    val index = backStackEntry.arguments?.getInt("index", 0) ?: 0
                                    val raw = backStackEntry.arguments
                                        ?.getString("imageList")
                                        .orEmpty()
                                    val decoded = raw.decodeURLQueryComponent()
                                    val imageList = decoded.split(",")
                                    ImageDetailScreen(
                                        navController = navController,
                                        imageList = imageList,
                                        title = title,
                                        index = index
                                    )
                                }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetNavItems(): List<BottomNavModel> {
    val navigationItems = listOf(
        BottomNavModel(
            route = "Movie",
            title = stringResource(Res.string.movie),
            selectedIcon = painterResource(Res.drawable.ph_film_slate_fill),
            unselectedIcon = painterResource(Res.drawable.ph_film_slate),
            hasNews = false
        ),
        BottomNavModel(
            route = "Series",
            title = stringResource(Res.string.series),
            selectedIcon = painterResource(Res.drawable.ph_television_simple_fill),
            unselectedIcon = painterResource(Res.drawable.ph_television_simple),
            hasNews = false
        ),
        BottomNavModel(
            route = "Search",
            title = stringResource(Res.string.search),
            selectedIcon = painterResource(Res.drawable.magnifying_glass_fill),
            unselectedIcon = painterResource(Res.drawable.ph_magnifying_glass),
            hasNews = false
        ),
        BottomNavModel(
            route = "Bookmark",
            title = stringResource(Res.string.bookmark),
            selectedIcon = painterResource(Res.drawable.ph_bookmark_fill),
            unselectedIcon = painterResource(Res.drawable.ph_bookmark),
            hasNews = false
        ),
    )
    return navigationItems
}

@Composable
fun BottomNavigation(navController: NavController) {
    val navigationItems = SetNavItems()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry
        ?.destination
        ?.route

    NavigationBar(
        modifier = Modifier.height(50.dp),
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        navigationItems.forEachIndexed { index, item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                alwaysShowLabel = true,
                colors = NavigationBarItemColors(
                    selectedIndicatorColor = Color.Unspecified,
                    selectedIconColor = AppPrimaryColor,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.tertiary,
                    unselectedTextColor = MaterialTheme.colorScheme.tertiary,
                    disabledIconColor = MaterialTheme.colorScheme.tertiary,
                    disabledTextColor = MaterialTheme.colorScheme.tertiary
                ),
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                icon = {
                    BadgedBox(badge = { if (item.hasNews) Badge() }) {
                        Icon(
                            painter = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    }
                },
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().route ?: "") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
