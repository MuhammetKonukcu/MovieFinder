package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.muhammetkonukcu.moviefinder.remote.entity.CombinedCast
import com.muhammetkonukcu.moviefinder.remote.entity.CombinedCrew
import com.muhammetkonukcu.moviefinder.remote.entity.Image
import com.muhammetkonukcu.moviefinder.remote.entity.Person
import com.muhammetkonukcu.moviefinder.theme.Neutral300
import com.muhammetkonukcu.moviefinder.theme.Neutral700
import com.muhammetkonukcu.moviefinder.util.DetailText
import com.muhammetkonukcu.moviefinder.util.DonutProgress
import com.muhammetkonukcu.moviefinder.util.ImagePlaceholderItem
import com.muhammetkonukcu.moviefinder.util.LoadAsyncImage
import com.muhammetkonukcu.moviefinder.util.MoviePlaceholderItem
import com.muhammetkonukcu.moviefinder.util.getDepartment
import com.muhammetkonukcu.moviefinder.viewmodel.ProfileViewModel
import io.ktor.http.encodeURLParameter
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.actress
import moviefinder.composeapp.generated.resources.back
import moviefinder.composeapp.generated.resources.detail_birth_age
import moviefinder.composeapp.generated.resources.detail_birth_death
import moviefinder.composeapp.generated.resources.detail_death
import moviefinder.composeapp.generated.resources.detail_department
import moviefinder.composeapp.generated.resources.detail_place
import moviefinder.composeapp.generated.resources.female
import moviefinder.composeapp.generated.resources.female_placeholder_590
import moviefinder.composeapp.generated.resources.images
import moviefinder.composeapp.generated.resources.male
import moviefinder.composeapp.generated.resources.male_placeholder_590
import moviefinder.composeapp.generated.resources.ph_caret_left
import moviefinder.composeapp.generated.resources.placeholder_dark
import moviefinder.composeapp.generated.resources.production_team
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.roundToInt

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    itemId: Int
) {
    val profileViewModel = koinViewModel<ProfileViewModel>()
    val personDetails = profileViewModel.personDetail.collectAsState()

    LaunchedEffect(itemId) {
        profileViewModel.loadProfile(itemId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background),
        topBar = {
            TopAppBar(
                name = personDetails.value?.name ?: "",
                navController = navController
            )
        }
    ) { innerPadding ->
        ScreenLazyColumn(
            innerPadding = innerPadding,
            navController = navController,
            viewModel = profileViewModel
        )
    }
}

@Composable
private fun TopAppBar(name: String, navController: NavController) {
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
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ScreenLazyColumn(
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val personDetails = viewModel.personDetail.collectAsState()
    val personCast = viewModel.personCast.collectAsState()
    val personCrew = viewModel.personCrew.collectAsState()
    val personImages = viewModel.personImages.collectAsState()
    LazyColumn(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
        item {
            personDetails.value?.let { person ->
                PersonInfo(person = person)
            } ?: run { PersonImagePlaceholder() }
        }

        personCast.value?.let { value ->
            if (value.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.actress),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    PersonCastsLazyRow(casts = value, navController = navController)
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.actress),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item {
                PersonCastsLazyRow(casts = null, navController = navController)
            }
        }

        personCrew.value?.let { value ->
            if (value.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.production_team),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                item {
                    PersonCrewsLazyRow(crews = value, navController = navController)
                }
            }
        } ?: run {
            item {
                Text(
                    text = stringResource(Res.string.production_team),
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
            item {
                PersonCrewsLazyRow(crews = null, navController = navController)
            }
        }

        personImages.value?.let { value ->
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
                        name = personDetails.value?.name ?: "",
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
                    name = personDetails.value?.name ?: "",
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun PersonInfo(person: Person) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val placeholderRes = when (person.gender) {
            1 -> painterResource(Res.drawable.female_placeholder_590)
            2 -> painterResource(Res.drawable.male_placeholder_590)
            else -> painterResource(Res.drawable.placeholder_dark)
        }
        LoadAsyncImage(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .clip(CircleShape)
                .border(width = 2.dp, color = Neutral300, CircleShape),
            imagePath = person.profilePath,
            contentDescription = person.name,
            placeholderRes = placeholderRes,
            errorRes = placeholderRes,
        )

        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .horizontalScroll(state = scrollState)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            person.department?.let { dep ->
                DetailText(
                    text = stringResource(
                        Res.string.detail_department,
                        stringResource(getDepartment(dep))
                    )
                )
            }

            if (person.gender == 1 || person.gender == 2) {
                val genderStr = if (person.gender == 1) stringResource(Res.string.female)
                else stringResource(Res.string.male)

                DetailText(text = genderStr)
            }

            PersonBirthDeathDetail(birthday = person.birthday, deathday = person.deathday)

            person.placeOfBirth?.let { pob ->
                DetailText(text = stringResource(Res.string.detail_place, pob))
            }
        }
    }
}

@Composable
private fun PersonImagePlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
                .background(color = Neutral700.copy(alpha = 0.3f), shape = CircleShape)
                .border(width = 2.dp, color = Neutral300, CircleShape),
        )
    }
}

@Composable
private fun PersonBirthDeathDetail(birthday: String?, deathday: String?) {
    val birthYear = birthday?.take(4)
    val deathYear = deathday?.take(4)

    when {
        birthYear != null && deathYear != null -> {
            DetailText(text = stringResource(Res.string.detail_birth_death, birthYear, deathYear))
        }

        birthYear != null && deathYear == null -> {
            val age = 2025 - birthYear.toInt()
            DetailText(text = stringResource(Res.string.detail_birth_age, birthYear, age))

        }

        birthYear == null && deathYear != null -> {
            DetailText(text = stringResource(Res.string.detail_death, deathYear))

        }

        else -> {}
    }
}

@Composable
private fun PersonCastsLazyRow(casts: List<CombinedCast>?, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        casts?.let {
            items(items = casts) { cast ->
                CastItem(movie = cast, navController = navController)
            }
        } ?: run { items(count = 10) { MoviePlaceholderItem() } }
    }
}

@Composable
private fun PersonCrewsLazyRow(crews: List<CombinedCrew>?, navController: NavController) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        crews?.let {
            items(items = crews) { crew ->
                CrewItem(movie = crew, navController = navController)
            }
        } ?: run {
            items(count = 10) { MoviePlaceholderItem() }
        }
    }
}

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
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            images?.let {
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
            } ?: run {
                items(count = 10) { ImagePlaceholderItem(width = itemWidth) }
            }

        }
    }
}

@Composable
private fun CastItem(movie: CombinedCast, navController: NavController) {
    Box(modifier = Modifier.clickable {
        if (movie.mediaType == "movie") {
            navController.navigate("MovieDetail/${movie.id}") {
                launchSingleTop = true
            }
        } else {
            navController.navigate("SeriesDetail/${movie.id}") {
                launchSingleTop = true
            }
        }
    }) {
        LoadAsyncImage(
            imagePath = movie.posterPath,
            contentDescription = movie.displayTitle,
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
private fun CrewItem(movie: CombinedCrew, navController: NavController) {
    Box(modifier = Modifier.clickable {
        if (movie.mediaType == "movie") {
            navController.navigate("MovieDetail/${movie.id}") {
                launchSingleTop = true
            }
        } else {
            navController.navigate("SeriesDetail/${movie.id}") {
                launchSingleTop = true
            }
        }
    }) {
        LoadAsyncImage(
            imagePath = movie.posterPath,
            contentDescription = movie.displayTitle,
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
private fun ImageItem(
    image: Image, width: Dp,
    onClicked: () -> Unit
) {
    LoadAsyncImage(
        modifier = Modifier
            .width(width)
            .height(width * 1.5f)
            .clip(shape = RoundedCornerShape(12.dp))
            .clickable { onClicked.invoke() },
        imagePath = image.filePath,
        contentDescription = image.filePath,
    )
}

