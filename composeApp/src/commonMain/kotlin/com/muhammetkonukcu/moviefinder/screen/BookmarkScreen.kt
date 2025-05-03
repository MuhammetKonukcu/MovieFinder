package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.muhammetkonukcu.moviefinder.room.entity.MovieEntity
import com.muhammetkonukcu.moviefinder.util.DonutProgress
import com.muhammetkonukcu.moviefinder.util.ErrorItem
import com.muhammetkonukcu.moviefinder.util.LoadAsyncImage
import com.muhammetkonukcu.moviefinder.util.LoadingItem
import com.muhammetkonukcu.moviefinder.util.getGenreMap
import com.muhammetkonukcu.moviefinder.util.toGenreNames
import com.muhammetkonukcu.moviefinder.viewmodel.BookmarkViewModel
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.bookmark
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BookmarkScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel = koinViewModel<BookmarkViewModel>()

    Scaffold(
        modifier = Modifier.statusBarsPadding()
            .padding(bottom = innerPadding.calculateBottomPadding())
            .background(color = MaterialTheme.colorScheme.background),
        topBar = { TopAppBar() }
    ) { innerPadding ->
        val lazyPagingItems = viewModel.favoriteMoviesPaging.collectAsLazyPagingItems()

        BookmarkLazyColumn(
            modifier = Modifier.padding(innerPadding),
            lazyPagingItems = lazyPagingItems,
            navController = navController
        )
    }
}

@Composable
private fun TopAppBar() {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.bookmark),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BookmarkLazyColumn(
    modifier: Modifier,
    lazyPagingItems: LazyPagingItems<MovieEntity>,
    navController: NavController
) {
    LazyColumn(
        modifier = modifier,
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
private fun MovieItem(movie: MovieEntity, navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().clickable {
        if (movie.mediaType == "movie") {
            navController.navigate("MovieDetail/${movie.id}") {
                launchSingleTop = true
            }
        } else {
            navController.navigate("SeriesDetail/${movie.id}") {
                launchSingleTop = true
            }
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