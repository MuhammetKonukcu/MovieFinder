package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.theme.AppPrimaryColor
import com.muhammetkonukcu.moviefinder.util.DetailText
import com.muhammetkonukcu.moviefinder.util.DonutProgress
import com.muhammetkonukcu.moviefinder.util.LoadAsyncImage
import com.muhammetkonukcu.moviefinder.util.MoviePlaceholderItem
import com.muhammetkonukcu.moviefinder.util.TopMoviePlaceholderItem
import com.muhammetkonukcu.moviefinder.util.getGenreMap
import com.muhammetkonukcu.moviefinder.util.toGenreNames
import com.muhammetkonukcu.moviefinder.viewmodel.MoviesViewModel
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.now_playing
import moviefinder.composeapp.generated.resources.popular
import moviefinder.composeapp.generated.resources.see_all
import moviefinder.composeapp.generated.resources.top_10_this_week
import moviefinder.composeapp.generated.resources.top_10_today
import moviefinder.composeapp.generated.resources.top_rated_home
import moviefinder.composeapp.generated.resources.upcoming
import moviefinder.composeapp.generated.resources.watch_free
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MoviesScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel = koinViewModel<MoviesViewModel>()

    ScreenWithLazyColumn(
        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
private fun ScreenWithLazyColumn(
    modifier: Modifier,
    viewModel: MoviesViewModel,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HomeTopMovieItem(
                viewModel = viewModel,
                navController = navController
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.popular),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/1") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies =
                viewModel.popularMovies.collectAsLazyPagingItems().FetchAvailableMovieItems()
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.top_10_this_week),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/2") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies = viewModel.weekTrendingMovies.collectAsLazyPagingItems()
                .FetchAvailableMovieItems(desired = 10)
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.top_10_today),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/3") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies = viewModel.dayTrendingMovies.collectAsLazyPagingItems()
                .FetchAvailableMovieItems(desired = 10)
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.watch_free),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/4") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies =
                viewModel.freeToWatchMovies.collectAsLazyPagingItems().FetchAvailableMovieItems()
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.top_rated_home),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/5") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies =
                viewModel.topRatedMovies.collectAsLazyPagingItems().FetchAvailableMovieItems()
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.now_playing),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/6") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies =
                viewModel.nowPlayingMovies.collectAsLazyPagingItems().FetchAvailableMovieItems()
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.upcoming),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )

                TextButton(onClick = {
                    navController.navigate("SeeAllMovies/7") {
                        launchSingleTop = true
                    }
                }) {
                    Text(
                        text = stringResource(Res.string.see_all),
                        color = AppPrimaryColor,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
        }
        item {
            val movies =
                viewModel.upcomingMovies.collectAsLazyPagingItems().FetchAvailableMovieItems()
            if (movies.isNotEmpty()) {
                MoviesLazyRow(movies = movies, navController = navController)
            } else {
                PlaceholderLazyRow()
            }
        }
    }
}

@Composable
private fun MoviesLazyRow(movies: List<Movie>, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(items = movies) { item ->
            RowMovieItem(movie = item, navController = navController)
        }
    }
}

@Composable
private fun PlaceholderLazyRow() {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(10) {
            MoviePlaceholderItem()
        }
    }
}

@Composable
private fun HomeTopMovieItem(
    viewModel: MoviesViewModel,
    navController: NavController
) {
    val movies =
        viewModel.dayTrendingMovies.collectAsLazyPagingItems().FetchAvailableMovieItems(desired = 1)
    var isClicked by remember { mutableStateOf(false) }
    movies.firstOrNull()?.let { movie ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (!isClicked) isClicked = true
                }
        ) {
            LoadAsyncImage(
                imagePath = movie.posterPath,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.5f)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0x03171A1F),
                                Color(0xFF171A1F)
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = movie.title ?: "",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))
                val scrollState = rememberScrollState()

                Row(
                    modifier = Modifier.horizontalScroll(state = scrollState)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val percent: Int = (movie.voteAverage * 10).roundToInt()
                    DonutProgress(progress = percent)

                    val movieGenres = movie.genreIds.toGenreNames(getGenreMap())
                    movieGenres.forEach { name -> DetailText(text = name) }

                    if (!movie.releaseDate.isNullOrBlank()) {
                        DetailText(text = movie.releaseDate.take(4))
                    }
                }
            }
        }

        if (isClicked) {
            navController.navigate("MovieDetail/${movie.id}") {
                launchSingleTop = true
            }
        }

    } ?: run { TopMoviePlaceholderItem() }
}

@Composable
private fun RowMovieItem(movie: Movie, navController: NavController) {
    Box(modifier = Modifier.clickable {
        navController.navigate("MovieDetail/${movie.id}") {
            launchSingleTop = true
        }
    }) {
        LoadAsyncImage(
            imagePath = movie.posterPath,
            contentDescription = movie.title ?: "",
            width = 100,
            height = 150,
            cornerRadius = RoundedCornerShape(8.dp)
        )
        val percent: Int = (movie.voteAverage * 10).roundToInt()
        DonutProgress(
            modifier = Modifier.align(Alignment.TopStart)
                .offset(x = 3.dp, y = 3.dp),
            progress = percent,
            size = 30.dp,
            textSize = 12.sp,
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun LazyPagingItems<Movie>.FetchAvailableMovieItems(desired: Int = 20): List<Movie> {
    val available = this.itemCount
    val count = minOf(desired, available)

    return (0 until count).mapNotNull { index -> this[index] }
}