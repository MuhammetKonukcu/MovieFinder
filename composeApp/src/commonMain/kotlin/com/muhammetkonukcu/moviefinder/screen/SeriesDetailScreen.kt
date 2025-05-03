package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.muhammetkonukcu.moviefinder.remote.entity.Actor
import com.muhammetkonukcu.moviefinder.remote.entity.Creator
import com.muhammetkonukcu.moviefinder.remote.entity.Image
import com.muhammetkonukcu.moviefinder.remote.entity.Season
import com.muhammetkonukcu.moviefinder.remote.entity.SeriesDetail
import com.muhammetkonukcu.moviefinder.remote.entity.Video
import com.muhammetkonukcu.moviefinder.theme.Neutral700
import com.muhammetkonukcu.moviefinder.theme.Neutral800
import com.muhammetkonukcu.moviefinder.theme.White
import com.muhammetkonukcu.moviefinder.util.ActorPlaceholderItem
import com.muhammetkonukcu.moviefinder.util.ContentType
import com.muhammetkonukcu.moviefinder.util.DetailText
import com.muhammetkonukcu.moviefinder.util.DetailTitleDescPlaceholder
import com.muhammetkonukcu.moviefinder.util.DonutProgress
import com.muhammetkonukcu.moviefinder.util.ImagePlaceholderItem
import com.muhammetkonukcu.moviefinder.util.LoadAsyncImage
import com.muhammetkonukcu.moviefinder.util.TextPlaceholder
import com.muhammetkonukcu.moviefinder.util.TopMoviePlaceholderItem
import com.muhammetkonukcu.moviefinder.viewmodel.SeriesDetailViewModel
import io.ktor.http.encodeURLParameter
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.back
import moviefinder.composeapp.generated.resources.bookmark
import moviefinder.composeapp.generated.resources.cast
import moviefinder.composeapp.generated.resources.creators
import moviefinder.composeapp.generated.resources.episode
import moviefinder.composeapp.generated.resources.episodes
import moviefinder.composeapp.generated.resources.female_placeholder_590
import moviefinder.composeapp.generated.resources.images
import moviefinder.composeapp.generated.resources.male_placeholder_590
import moviefinder.composeapp.generated.resources.ph_bookmark
import moviefinder.composeapp.generated.resources.ph_bookmark_fill
import moviefinder.composeapp.generated.resources.ph_caret_left
import moviefinder.composeapp.generated.resources.ph_play_circle_fill
import moviefinder.composeapp.generated.resources.placeholder_dark
import moviefinder.composeapp.generated.resources.season
import moviefinder.composeapp.generated.resources.seasons
import moviefinder.composeapp.generated.resources.videos
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SeriesDetailScreen(
    navController: NavController,
    itemId: Int,
) {
    val seriesDetailVM = koinViewModel<SeriesDetailViewModel>()

    LaunchedEffect(itemId) {
        val contentType = ContentType.Series
        seriesDetailVM.loadAll(contentType, itemId)
        seriesDetailVM.isMovieFavorite(itemId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenLazyColumn(
            viewModel = seriesDetailVM,
            navController = navController
        )
    }
}

@Composable
private fun ScreenLazyColumn(
    viewModel: SeriesDetailViewModel,
    navController: NavController
) {
    val detail = viewModel.seriesDetail.collectAsState()
    val videos = viewModel.videos.collectAsState()
    val images = viewModel.images.collectAsState()
    val cast = viewModel.actors.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            detail.value?.let {
                TopView(
                    viewModel = viewModel,
                    series = it,
                    navController = navController
                )
            } ?: run { TopMoviePlaceholderItem(navController = navController) }
        }

        item {
            detail.value?.let {
                SeriesDetails(
                    name = it.name,
                    tagline = it.tagline,
                    overview = it.overview
                )
            } ?: run { DetailTitleDescPlaceholder() }
        }

        detail.value?.let { value ->
            if (value.createdBy.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.creators),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    CreatorsLazyRow(actors = value.createdBy, navController = navController)
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.creators),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item { ActorsPlaceholderRow() }
        }

        cast.value?.let { value ->
            if (value.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.cast),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    ActorsLazyRow(actors = value, navController = navController)
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.cast),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item { ActorsPlaceholderRow() }
        }

        detail.value?.seasons?.let { value ->
            if (value.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.seasons),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    SeasonsLazyRow(seasons = value)
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.seasons),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item {
                SeasonsLazyRow(seasons = null)
            }
        }

        videos.value?.let { value ->
            if (value.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.videos),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    VideosLazyRow(videos = value)
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.videos),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item {
                VideosLazyRow(null)
            }
        }

        images.value?.let { value ->
            if (value.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.images),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    ImagesLazyRow(
                        images = value,
                        name = detail.value?.name ?: "",
                        navController = navController
                    )
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.images),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item {
                ImagesLazyRow(
                    images = null,
                    name = "",
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun TopView(
    viewModel: SeriesDetailViewModel,
    series: SeriesDetail,
    navController: NavController
) {
    val isMovieFavorite = viewModel.isMovieFavorite.collectAsState()
    val favoriteIcon = if (isMovieFavorite.value) painterResource(Res.drawable.ph_bookmark_fill)
    else painterResource(Res.drawable.ph_bookmark)

    Box(modifier = Modifier.fillMaxWidth()) {
        LoadAsyncImage(
            imagePath = series.posterPath,
            contentDescription = series.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1.5f)
        )

        Row(
            modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                modifier = Modifier.background(
                    color = Color(0xFF171A1F).copy(alpha = 0.4f),
                    shape = RoundedCornerShape(24.dp)
                ), onClick = { navController.navigateUp() }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ph_caret_left),
                    contentDescription = stringResource(Res.string.back),
                    tint = White
                )
            }

            IconButton(
                modifier = Modifier.background(
                    color = Color(0xFF171A1F).copy(alpha = 0.4f),
                    shape = RoundedCornerShape(24.dp)
                ), onClick = {
                    if (isMovieFavorite.value)
                        viewModel.removeFavoriteMovies(id = series.id)
                    else
                        viewModel.addFavoriteMovie(series = series)
                }
            ) {
                Icon(
                    painter = favoriteIcon,
                    contentDescription = stringResource(Res.string.bookmark),
                    tint = White
                )
            }
        }

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .horizontalScroll(state = scrollState)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .align(alignment = Alignment.BottomStart),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val percent: Int = (series.voteAverage * 10).roundToInt()
            DonutProgress(progress = percent)

            series.genres.forEach { genre ->
                DetailText(text = genre.name)
            }

            if (!series.firstAirDate.isNullOrBlank()) {
                DetailText(text = series.firstAirDate.take(4))
            }

            val seasonStr = if (series.numberOfSeasons < 2) stringResource(Res.string.season)
            else stringResource(Res.string.seasons)
            DetailText(text = "${series.numberOfSeasons} $seasonStr")

            val episodeStr = if (series.numberOfEpisodes < 2) stringResource(Res.string.episode)
            else stringResource(Res.string.episodes)
            DetailText(text = "${series.numberOfEpisodes} $episodeStr")

            series.networks?.forEach { network ->
                DetailText(text = network.name)
            }
        }
    }
}

@Composable
private fun SeriesDetails(
    name: String?,
    tagline: String?,
    overview: String?,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!name.isNullOrBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (!tagline.isNullOrBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                text = tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (!overview.isNullOrBlank()) {
            Text(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                text = overview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ActorsLazyRow(actors: List<Actor>, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = actors) { actor ->
            ActorItem(actor = actor, navController = navController)
        }
    }
}

@Composable
private fun ActorsPlaceholderRow() {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(count = 10) {
            ActorPlaceholderItem()
        }
    }
}

@Composable
private fun CreatorsLazyRow(actors: List<Creator>, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = actors) { creator ->
            CreatorItem(creator = creator, navController = navController)
        }
    }
}

@Composable
private fun SeasonsLazyRow(seasons: List<Season>?) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        val parentWidth = maxWidth
        val itemWidth = parentWidth * 0.8f

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            seasons?.let {
                items(items = seasons) { season ->
                    SeasonItem(season = season, width = itemWidth)
                }
            } ?: run {
                items(count = 5) {
                    SeasonPlaceHolderItem(width = itemWidth)
                }
            }
        }
    }
}

@Composable
private fun SeasonItem(season: Season, width: Dp) {
    Row(
        modifier = Modifier
            .width(width)
            .background(color = Neutral800, shape = RoundedCornerShape(size = 12.dp))
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LoadAsyncImage(
            imagePath = season.posterPath,
            contentDescription = season.name,
            cornerRadius = RoundedCornerShape(8.dp),
            width = 100,
            height = 150
        )

        Column(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = season.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            val year = season.airDate?.take(4) ?: ""
            val episodeStr = season.episodeCount ?: ""
            if (year.length + episodeStr.length > 0) {
                val separator = " - "

                val infoStr = when {
                    year.isNotEmpty() && episodeStr.isNotEmpty() -> {
                        year + separator + stringResource(Res.string.episode)
                    }

                    year.isNotEmpty() -> year
                    episodeStr.isNotEmpty() -> episodeStr
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

            season.overview?.let {
                Text(
                    text = season.overview,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.primary,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SeasonPlaceHolderItem(width: Dp) {
    Row(
        modifier = Modifier
            .width(width)
            .background(color = Neutral800, shape = RoundedCornerShape(size = 12.dp))
            .padding(all = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(width = 100.dp, height = 150.dp)
                .background(
                    color = Neutral700.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(size = 8.dp)
                ),
        )

        Column(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextPlaceholder(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp))
            TextPlaceholder(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
            TextPlaceholder(modifier = Modifier.fillMaxWidth().weight(1f))
        }
    }
}

@Composable
private fun VideosLazyRow(videos: List<Video>?) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        val parentWidth = maxWidth
        val itemWidth = parentWidth * 0.8f

        videos?.let {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(videos) { video ->
                    VideoItem(
                        video = video,
                        width = itemWidth,
                    )
                }
            }
        } ?: run {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(count = 5) {
                    ImagePlaceholderItem(width = itemWidth)
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun ImagesLazyRow(
    images: List<Image>?,
    name: String,
    navController: NavController
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        val parentWidth = maxWidth
        val itemWidth = parentWidth * 0.8f

        images?.let {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(items = images) { index, image ->
                    ImageItem(
                        image = image,
                        width = itemWidth,
                        onClicked = {
                            val itemsParam =
                                images.joinToString(",") { it.filePath.encodeURLParameter() }
                            navController.navigate("ImageDetail/$name/$itemsParam/$index") {
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        } ?: run {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(count = 5) {
                    ImagePlaceholderItem(width = itemWidth)
                }
            }
        }
    }
}

@Composable
private fun CreatorItem(creator: Creator, navController: NavController) {
    Column(
        modifier = Modifier.width(118.dp).clickable {
            navController.navigate("Profile/${creator.id}") {
                launchSingleTop = true
            }
        },
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholderRes = when (creator.gender) {
            1 -> painterResource(Res.drawable.female_placeholder_590)
            2 -> painterResource(Res.drawable.male_placeholder_590)
            else -> painterResource(Res.drawable.placeholder_dark)
        }

        LoadAsyncImage(
            imagePath = creator.profilePath,
            contentDescription = creator.name,
            width = 118,
            height = 150,
            placeholderRes = placeholderRes,
            errorRes = placeholderRes,
            cornerRadius = RoundedCornerShape(8.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = creator.name,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun ActorItem(actor: Actor, navController: NavController) {
    Column(
        modifier = Modifier.width(118.dp).clickable {
            navController.navigate("Profile/${actor.id}") {
                launchSingleTop = true
            }
        },
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholderRes = when (actor.gender) {
            1 -> painterResource(Res.drawable.female_placeholder_590)
            2 -> painterResource(Res.drawable.male_placeholder_590)
            else -> painterResource(Res.drawable.placeholder_dark)
        }

        LoadAsyncImage(
            imagePath = actor.profilePath,
            contentDescription = actor.name ?: "",
            width = 118,
            height = 150,
            placeholderRes = placeholderRes,
            errorRes = placeholderRes,
            cornerRadius = RoundedCornerShape(8.dp)
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = actor.name ?: "",
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = actor.characterName ?: "",
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun VideoItem(
    video: Video,
    width: Dp
) {
    val uriHandler = LocalUriHandler.current
    Box(
        modifier = Modifier
            .width(width)
            .aspectRatio(16f / 9f)
            .clickable {
                val url = when (video.site.lowercase()) {
                    "youtube" -> "https://www.youtube.com/watch?v=${video.key}"
                    "vimeo" -> "https://vimeo.com/${video.key}"
                    else -> null
                }
                url?.let { uriHandler.openUri(it) }
            }
    ) {
        val thumbnailUrl = when (video.site.lowercase()) {
            "youtube" -> "https://img.youtube.com/vi/${video.key}/0.jpg"
            else -> ""
        }

        LoadAsyncImage(
            modifier = Modifier.fillMaxSize().clip(shape = RoundedCornerShape(12.dp)),
            model = thumbnailUrl,
            contentDescription = video.name
        )

        Icon(
            painter = painterResource(Res.drawable.ph_play_circle_fill),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
private fun ImageItem(
    image: Image,
    width: Dp,
    onClicked: () -> Unit
) {
    LoadAsyncImage(
        modifier = Modifier.width(width).aspectRatio(1.77f)
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { onClicked.invoke() },
        imagePath = image.filePath,
        contentDescription = image.filePath
    )
}
