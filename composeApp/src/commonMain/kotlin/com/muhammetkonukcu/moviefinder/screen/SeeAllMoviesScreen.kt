package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.util.DonutProgress
import com.muhammetkonukcu.moviefinder.util.ErrorItem
import com.muhammetkonukcu.moviefinder.util.LoadAsyncImage
import com.muhammetkonukcu.moviefinder.util.LoadingItem
import com.muhammetkonukcu.moviefinder.util.getGenreMap
import com.muhammetkonukcu.moviefinder.util.toGenreNames
import com.muhammetkonukcu.moviefinder.viewmodel.MoviesViewModel
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.back
import moviefinder.composeapp.generated.resources.now_playing
import moviefinder.composeapp.generated.resources.ph_caret_left
import moviefinder.composeapp.generated.resources.popular
import moviefinder.composeapp.generated.resources.top_10_this_week
import moviefinder.composeapp.generated.resources.top_10_today
import moviefinder.composeapp.generated.resources.top_rated_home
import moviefinder.composeapp.generated.resources.upcoming
import moviefinder.composeapp.generated.resources.watch_free
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SeeAllMoviesScreen(navController: NavController, listId: Int) {
    val viewModel = koinViewModel<MoviesViewModel>()

    Scaffold(modifier = Modifier.statusBarsPadding(), topBar = {
        TopAppBar(categoryName = getCategoryName(listId), navController)
    }) { innerPadding ->
        MoviesLazyColumn(
            paddingValues = innerPadding,
            lazyPagingItems = GetMovies(viewModel, listId),
            navController = navController
        )
    }
}

@Composable
private fun TopAppBar(categoryName: String, navController: NavController) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            modifier = Modifier,
            onClick = { navController.navigateUp() },
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ph_caret_left),
                contentDescription = stringResource(Res.string.back),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = categoryName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun MoviesLazyColumn(
    paddingValues: PaddingValues,
    lazyPagingItems: LazyPagingItems<Movie>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    LoadingItem()
                }
            }

            is LoadState.Error -> {
                item { ErrorItem(onRetryClick = { lazyPagingItems.retry() }) }
            }

            else -> {}
        }

        items(count = lazyPagingItems.itemCount) { index ->
            val media = lazyPagingItems[index]
            media?.let {
                MovieItem(movie = media, navController = navController)
            }
        }

        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item { LoadingItem() }
            }

            is LoadState.Error -> {
                item { ErrorItem(onRetryClick = { lazyPagingItems.retry() }) }
            }

            else -> {}
        }
    }
}

@Composable
private fun MovieItem(movie: Movie, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().clickable {
        navController.navigate("MovieDetail/${movie.id}") {
            launchSingleTop = true
        }
    }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box {
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

        Column(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = movie.title ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val genres = if (movie.genreIds.isNotEmpty()) movie.genreIds.toGenreNames(getGenreMap())
                .joinToString(separator = " - ") else ""
            val year = movie.releaseDate?.take(4) ?: ""

            if (year.length + genres.length > 0) {
                val separator = " - "

                val infoStr = when {
                    year.isNotEmpty() && genres.isNotEmpty() -> year + separator + genres
                    year.isNotEmpty() -> year
                    genres.isNotEmpty() -> genres
                    else -> ""
                }

                Text(
                    text = infoStr,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = movie.overview ?: "",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
        }
    }
}

@Composable
private fun getCategoryName(listId: Int): String {
    return when (listId) {
        1 -> stringResource(Res.string.popular)
        2 -> stringResource(Res.string.top_10_this_week)
        3 -> stringResource(Res.string.top_10_today)
        4 -> stringResource(Res.string.watch_free)
        5 -> stringResource(Res.string.top_rated_home)
        6 -> stringResource(Res.string.now_playing)
        else -> stringResource(Res.string.upcoming)
    }
}

@Composable
private fun GetMovies(viewModel: MoviesViewModel, listId: Int): LazyPagingItems<Movie> {
    return when (listId) {
        1 -> viewModel.popularMovies.collectAsLazyPagingItems()
        2 -> viewModel.weekTrendingMovies.collectAsLazyPagingItems()
        3 -> viewModel.dayTrendingMovies.collectAsLazyPagingItems()
        4 -> viewModel.freeToWatchMovies.collectAsLazyPagingItems()
        5 -> viewModel.topRatedMovies.collectAsLazyPagingItems()
        6 -> viewModel.nowPlayingMovies.collectAsLazyPagingItems()
        else -> viewModel.upcomingMovies.collectAsLazyPagingItems()
    }
}

