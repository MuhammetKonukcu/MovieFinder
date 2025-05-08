package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import com.muhammetkonukcu.moviefinder.model.Filters
import com.muhammetkonukcu.moviefinder.remote.entity.Movie
import com.muhammetkonukcu.moviefinder.room.entity.HistoryEntity
import com.muhammetkonukcu.moviefinder.screen.sheet.SearchFilterSheet
import com.muhammetkonukcu.moviefinder.theme.AppPrimaryColor
import com.muhammetkonukcu.moviefinder.theme.Neutral550
import com.muhammetkonukcu.moviefinder.util.DonutProgress
import com.muhammetkonukcu.moviefinder.util.ErrorItem
import com.muhammetkonukcu.moviefinder.util.LoadAsyncImage
import com.muhammetkonukcu.moviefinder.util.LoadingItem
import com.muhammetkonukcu.moviefinder.util.getGenreMap
import com.muhammetkonukcu.moviefinder.util.toGenreNames
import com.muhammetkonukcu.moviefinder.viewmodel.SearchViewModel
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.delete
import moviefinder.composeapp.generated.resources.ph_cancel
import moviefinder.composeapp.generated.resources.ph_sliders
import moviefinder.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SearchScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel = koinViewModel<SearchViewModel>()
    val searchPagingItems = viewModel.searchFlow.collectAsLazyPagingItems()
    val historyPagingItems = viewModel.historyFlow.collectAsLazyPagingItems()
    val hasFilterChanges by viewModel.hasFilterChanges.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = innerPadding.calculateBottomPadding(),
                top = innerPadding.calculateTopPadding()
            )
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier) {
            SearchBar(
                viewModel = viewModel,
                userInput = query,
                navController = navController,
                onTextChange = { newValue ->
                    query = newValue
                }
            )

            if (!hasFilterChanges) {
                HistoryLazyColumn(
                    viewModel = viewModel,
                    lazyPagingItems = historyPagingItems,
                    navController = navController
                )
            } else {
                SearchLazyColumn(
                    viewModel = viewModel,
                    lazyPagingItems = searchPagingItems,
                    hasFilterChanges = hasFilterChanges,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    viewModel: SearchViewModel,
    userInput: String,
    navController: NavController,
    onTextChange: (String) -> Unit
) {
    var isFilterSheetOpen by remember { mutableStateOf(false) }

    var contentTypeStates by rememberSaveable {
        mutableStateOf(mapOf("Movie" to true, "Series" to false))
    }

    val genreIds = getGenreMap().keys.toList()
    var genreStates by rememberSaveable {
        mutableStateOf(genreIds.associateWith { false })
    }

    var selectedYear by rememberSaveable { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .width(IntrinsicSize.Max)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 12.dp
                        )
                    ),
                value = userInput,
                onValueChange = { newValue ->
                    onTextChange.invoke(newValue)
                    val filters = viewModel.filters.value.copy(query = newValue)
                    viewModel.updateFilters(filters)
                },
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                placeholder = {
                    val placeholderStr = stringResource(Res.string.search_hint)
                    Text(
                        text = placeholderStr,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onTertiary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
            )

            FilledIconButton(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .width(IntrinsicSize.Max)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = AppPrimaryColor,
                    contentColor = AppPrimaryColor,
                    disabledContentColor = AppPrimaryColor,
                    disabledContainerColor = AppPrimaryColor
                ),
                onClick = {
                    isFilterSheetOpen = true
                },
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ph_sliders),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            thickness = 1.dp
        )

        if (isFilterSheetOpen) {
            SearchFilterSheet(
                viewModel = viewModel,
                onApplyFilters = { contentType, genres, year, sortBy ->
                    contentTypeStates = contentType
                    genreStates = genres
                    selectedYear = year
                    val filters = Filters(
                        contentTypes = contentTypeStates,
                        genres = genreStates,
                        year = selectedYear,
                        query = userInput,
                        sortBy = sortBy
                    )
                    viewModel.updateFilters(filters)
                },
                onDismissSheet = {
                    isFilterSheetOpen = false
                }
            )
        }
    }
}

@Composable
private fun SearchLazyColumn(
    viewModel: SearchViewModel,
    lazyPagingItems: LazyPagingItems<Movie>,
    hasFilterChanges: Boolean,
    navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        when (lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                item {
                    if (hasFilterChanges)
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
                if (!media.title.isNullOrBlank())
                    MovieItem(movie = media, navController = navController, onClickedItem = {
                        viewModel.addHistory(movie = it)
                    })
                else
                    SeriesItem(series = media, navController = navController, onClickedItem = {
                        viewModel.addHistory(movie = it)
                    })
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
private fun HistoryLazyColumn(
    viewModel: SearchViewModel,
    lazyPagingItems: LazyPagingItems<HistoryEntity>,
    navController: NavController
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = { index ->
                val item = lazyPagingItems[index]
                "${item?.id}${item?.mediaType}"
            }
        ) { index ->
            val media = lazyPagingItems[index]
            media?.let {
                MovieItem(movie = media, navController = navController, onClickedItem = {
                    viewModel.removeFromHistory(id = media.id, mediaType = media.mediaType)
                })
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
private fun MovieItem(movie: Movie, navController: NavController, onClickedItem: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable {
        onClickedItem.invoke()
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
private fun SeriesItem(series: Movie, navController: NavController, onClickedItem: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable {
        onClickedItem.invoke()
        navController.navigate("SeriesDetail/${series.id}") {
            launchSingleTop = true
        }
    }, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Box {
            LoadAsyncImage(
                imagePath = series.posterPath,
                contentDescription = series.name ?: "",
                width = 100,
                height = 150,
                cornerRadius = RoundedCornerShape(8.dp)
            )
            val percent: Int = (series.voteAverage * 10).roundToInt()
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
                text = series.name ?: "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val genres =
                if (series.genreIds.isNotEmpty()) series.genreIds.toGenreNames(getGenreMap())
                    .joinToString(separator = " - ") else ""
            val year = series.firstAirDate?.take(4) ?: ""

            if (genres.length + year.length > 0) {
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
                text = series.overview ?: "",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MovieItem(
    movie: HistoryEntity,
    navController: NavController,
    onClickedItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (movie.mediaType == "movie") {
                    navController.navigate("MovieDetail/${movie.id}") {
                        launchSingleTop = true
                    }
                } else {
                    navController.navigate("SeriesDetail/${movie.id}") {
                        launchSingleTop = true
                    }
                }

            },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = movie.title ?: "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(modifier = Modifier.size(32.dp), onClick = { onClickedItem.invoke() }) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(Res.drawable.ph_cancel),
                        contentDescription = stringResource(Res.string.delete),
                        tint = Neutral550
                    )
                }
            }

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