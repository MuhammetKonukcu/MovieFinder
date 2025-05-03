package com.muhammetkonukcu.moviefinder.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.muhammetkonukcu.moviefinder.theme.Black
import com.muhammetkonukcu.moviefinder.theme.Neutral200
import com.muhammetkonukcu.moviefinder.zoom.Settings
import com.muhammetkonukcu.moviefinder.zoom.rememberZoomState
import com.muhammetkonukcu.moviefinder.zoom.pinchToZoomSnapBack
import kotlinx.coroutines.launch
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.back
import moviefinder.composeapp.generated.resources.ph_caret_left
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue

@Composable
fun ImageDetailScreen(
    navController: NavController,
    imageList: List<String>?,
    title: String,
    index: Int
) {
    Scaffold(topBar = {
        TopAppBar(name = title, navController = navController)
    }) { innerPadding ->
        var settings by remember { mutableStateOf(Settings()) }
        val pagerState = rememberPagerState(pageCount = { imageList?.size ?: 0 })

        val coroutineScope = rememberCoroutineScope()
        coroutineScope.launch { pagerState.scrollToPage(index) }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            imageList?.getOrNull(page)?.let {
                val painter =
                    rememberAsyncImagePainter(model = "https://image.tmdb.org/t/p/original$it")
                val state = painter.state

                val contentSize = (state as? AsyncImagePainter.State.Success)
                    ?.painter
                    ?.intrinsicSize
                    ?: Size.Zero

                val zoomState = rememberZoomState(
                    contentSize = contentSize,
                    initialScale = settings.initialScale
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Black),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painter,
                        contentDescription = title,
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                val pageOffset = (
                                        (pagerState.currentPage - page) +
                                                pagerState.currentPageOffsetFraction
                                        ).absoluteValue
                                alpha = lerp(0.2f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                            }
                            .pinchToZoomSnapBack(
                                zoomState = zoomState,
                                zoomEnabled = settings.zoomEnabled,
                            )
                    )

                    if (state is AsyncImagePainter.State.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Neutral200,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(name: String, navController: NavController) {
    Row(modifier = Modifier.statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
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