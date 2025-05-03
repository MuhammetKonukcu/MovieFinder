package com.muhammetkonukcu.moviefinder.screen.sheet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.muhammetkonukcu.moviefinder.theme.AppPrimaryColor
import com.muhammetkonukcu.moviefinder.util.getGenreMap
import com.muhammetkonukcu.moviefinder.viewmodel.SearchViewModel
import kotlinx.coroutines.launch
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.all_times
import moviefinder.composeapp.generated.resources.apply
import moviefinder.composeapp.generated.resources.back
import moviefinder.composeapp.generated.resources.content_type
import moviefinder.composeapp.generated.resources.fewest_votes
import moviefinder.composeapp.generated.resources.filter_and_sort
import moviefinder.composeapp.generated.resources.genres
import moviefinder.composeapp.generated.resources.highest_revenue
import moviefinder.composeapp.generated.resources.least_popular
import moviefinder.composeapp.generated.resources.lowest_rated
import moviefinder.composeapp.generated.resources.lowest_revenue
import moviefinder.composeapp.generated.resources.most_popular
import moviefinder.composeapp.generated.resources.most_votes
import moviefinder.composeapp.generated.resources.movie
import moviefinder.composeapp.generated.resources.name_a_z
import moviefinder.composeapp.generated.resources.name_z_a
import moviefinder.composeapp.generated.resources.newest
import moviefinder.composeapp.generated.resources.newest_release
import moviefinder.composeapp.generated.resources.oldest
import moviefinder.composeapp.generated.resources.oldest_release
import moviefinder.composeapp.generated.resources.ph_caret_left
import moviefinder.composeapp.generated.resources.reset
import moviefinder.composeapp.generated.resources.series
import moviefinder.composeapp.generated.resources.sort_by
import moviefinder.composeapp.generated.resources.time_periods
import moviefinder.composeapp.generated.resources.top_rated
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterSheet(
    viewModel: SearchViewModel,
    onApplyFilters: (
        contentTypes: Map<String, Boolean>,
        genres: Map<Int, Boolean>,
        year: Int?,
        sortBy: String?,
    ) -> Unit,
    onDismissSheet: () -> Unit
) {
    val filters by viewModel.filters.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = { CustomDragHandle() },
        onDismissRequest = {
            onDismissSheet.invoke()
        }
    ) {
        var contentTypeStates by rememberSaveable {
            mutableStateOf(filters.contentTypes)
        }
        var genreStates by rememberSaveable {
            mutableStateOf(filters.genres)
        }
        var selectedYear by rememberSaveable {
            mutableStateOf(filters.year)
        }
        var sortByStates by rememberSaveable {
            mutableStateOf(filters.sortBy)
        }

        Column(modifier = Modifier) {
            SheetTopBar(
                onDismissSheet = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismissSheet.invoke()
                    }
                }
            )

            ContentTypeLazyRow(
                initialStates = contentTypeStates,
                onSelectionChanged = { label, sel ->
                    contentTypeStates = contentTypeStates.keys
                        .associateWith { key -> key == label }
                }
            )

            GenresLazyRow(
                initialStates = genreStates,
                onSelectionChanged = { id, sel ->
                    genreStates = genreStates.toMutableMap().also { it[id] = sel }
                }
            )

            val startYear = 1874
            val currentYear = 2025

            val tmdbYears: List<Int> = (startYear..currentYear)
                .toList()
                .asReversed()

            TimePeriodsLazyRow(
                years = tmdbYears,
                selectedYear = selectedYear,
                onYearSelected = { yr ->
                    selectedYear = yr
                }
            )

            SortByLazyRow(
                currentContentType = contentTypeStates.entries.find { it.value == true }?.key
                    ?: "Movie",
                currentSortByValue = sortByStates,
                onSelectionChanged = { sort ->
                    sortByStates = sort
                }
            )

            SheetBottomBar(
                onDismissSheet = {
                    val filters = viewModel.filters.value.copy(
                        contentTypes = viewModel.defaultFilters.contentTypes,
                        genres = viewModel.defaultFilters.genres,
                        year = viewModel.defaultFilters.year,
                        sortBy = viewModel.defaultFilters.sortBy,
                    )
                    viewModel.updateFilters(filters)
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismissSheet.invoke()
                    }
                },
                applyFilters = {
                    onApplyFilters.invoke(
                        contentTypeStates,
                        genreStates,
                        selectedYear,
                        sortByStates
                    )
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismissSheet.invoke()
                    }
                }
            )
        }
    }
}

@Composable
fun CustomDragHandle(
    modifier: Modifier = Modifier,
    width: Dp = 32.dp,
    height: Dp = 4.dp,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    color: Color = MaterialTheme.colorScheme.tertiaryContainer,
) {
    Surface(
        modifier =
            modifier
                .padding(vertical = 12.dp)
                .semantics {
                    contentDescription = ""
                },
        color = color,
        shape = shape
    ) {
        Box(Modifier.size(width = width, height = height))
    }
}

@Composable
private fun SheetTopBar(onDismissSheet: () -> Unit) {
    Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            modifier = Modifier,
            onClick = {
                onDismissSheet.invoke()
            },
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ph_caret_left),
                contentDescription = stringResource(Res.string.back),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = stringResource(Res.string.filter_and_sort),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SheetBottomBar(onDismissSheet: () -> Unit, applyFilters: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            thickness = 1.dp
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onDismissSheet.invoke()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.reset),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            TextButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    applyFilters.invoke()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = AppPrimaryColor,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(Res.string.apply),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun ContentTypeLazyRow(
    initialStates: Map<String, Boolean>,
    onSelectionChanged: (String, Boolean) -> Unit = { _, _ -> }
) {
    var buttonStates by remember { mutableStateOf(initialStates) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(Res.string.content_type),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(buttonStates.keys.toList()) { label ->
                val selected = buttonStates[label] == true

                val background = if (selected) AppPrimaryColor else Color.Transparent
                val contentColor =
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                val border = if (selected) null else BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.tertiary
                )

                TextButton(
                    onClick = {
                        buttonStates = buttonStates.keys.associateWith { it == label }
                        onSelectionChanged(label, true)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = background,
                        contentColor = contentColor
                    ),
                    border = border,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    val btnStr = if (label == "Movie") stringResource(Res.string.movie)
                    else stringResource(Res.string.series)
                    Text(text = btnStr, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun GenresLazyRow(
    initialStates: Map<Int, Boolean>,
    onSelectionChanged: (Int, Boolean) -> Unit
) {
    var itemsState by rememberSaveable {
        mutableStateOf(initialStates)
    }

    val listState = rememberLazyListState()
    val selectedIndex =
        itemsState.entries.toList().indexOfFirst { it.value == true }.coerceAtLeast(0)
    val initialIndex = remember { selectedIndex }

    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(Res.string.genres),
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            itemsState.entries.toList().forEach { (id, isSelected) ->
                item {
                    TextButton(
                        onClick = {
                            val new = itemsState.toMutableMap()
                            new[id] = !isSelected
                            itemsState = new
                            onSelectionChanged(id, !isSelected)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (isSelected) AppPrimaryColor else Color.Transparent,
                            contentColor = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.tertiary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) null else BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            text = getGenreMap()[id] ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimePeriodsLazyRow(
    years: List<Int>,
    selectedYear: Int?,
    onYearSelected: (Int?) -> Unit
) {
    val items = listOf(stringResource(Res.string.all_times)) + years.map { it.toString() }
    val listState = rememberLazyListState()

    val selectedIndex = items.indexOf(
        selectedYear?.toString() ?: stringResource(Res.string.all_times)
    ).coerceAtLeast(0)

    val initialIndex = remember { selectedIndex }

    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(Res.string.time_periods),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(items) { label ->
                val isAllTimes = label == stringResource(Res.string.all_times)
                val isSelected = if (isAllTimes) {
                    selectedYear == null
                } else {
                    selectedYear?.toString() == label
                }

                val background = if (isSelected) AppPrimaryColor else Color.Transparent
                val contentColor =
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                val border = if (isSelected) null else BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.tertiary
                )

                TextButton(
                    onClick = {
                        val newSelection = if (isAllTimes) null else label.toInt()
                        onYearSelected(newSelection)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = background,
                        contentColor = contentColor
                    ),
                    border = border,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
fun SortByLazyRow(
    currentContentType: String,
    currentSortByValue: String?,
    onSelectionChanged: (String?) -> Unit
) {
    val commonSortOptions = mapOf(
        "popularity.asc" to stringResource(Res.string.least_popular),
        "popularity.desc" to stringResource(Res.string.most_popular),
        "vote_average.asc" to stringResource(Res.string.lowest_rated),
        "vote_average.desc" to stringResource(Res.string.top_rated),
        "vote_count.asc" to stringResource(Res.string.fewest_votes),
        "vote_count.desc" to stringResource(Res.string.most_votes),
    )

    val movieSortOptions = mapOf(
        "release_date.asc" to stringResource(Res.string.oldest),
        "release_date.desc" to stringResource(Res.string.newest),
        "revenue.asc" to stringResource(Res.string.lowest_revenue),
        "revenue.desc" to stringResource(Res.string.highest_revenue),
        "primary_release_date.asc" to stringResource(Res.string.oldest_release),
        "primary_release_date.desc" to stringResource(Res.string.newest_release),
        "original_title.asc" to stringResource(Res.string.name_a_z),
        "original_title.desc" to stringResource(Res.string.name_z_a),
    )

    val seriesSortOptions = mapOf(
        "first_air_date.asc" to stringResource(Res.string.oldest),
        "first_air_date.desc" to stringResource(Res.string.newest),
        "original_name.asc" to stringResource(Res.string.name_a_z),
        "original_name.desc" to stringResource(Res.string.name_z_a)
    )

    val itemList =
        if (currentContentType == "Movie") commonSortOptions + movieSortOptions
        else commonSortOptions + seriesSortOptions

    val listState = rememberLazyListState()
    val selectedIndex = itemList.keys.toList().indexOf(currentSortByValue).coerceAtLeast(0)
    val initialIndex = remember { selectedIndex }

    LaunchedEffect(Unit) {
        listState.scrollToItem(initialIndex)
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = stringResource(Res.string.sort_by),
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(itemList.entries.toList()) { (key, label) ->
                val selected = currentSortByValue == key

                val background = if (selected) AppPrimaryColor else Color.Transparent
                val contentColor =
                    if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.tertiary
                val border =
                    if (selected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)

                TextButton(
                    onClick = {
                        val newSort = if (selected) null else key
                        onSelectionChanged(newSort)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = background,
                        contentColor = contentColor
                    ),
                    border = border,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(text = label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
