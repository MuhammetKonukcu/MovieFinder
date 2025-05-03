package com.muhammetkonukcu.moviefinder.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.muhammetkonukcu.moviefinder.theme.AppPrimaryColor
import com.muhammetkonukcu.moviefinder.theme.Neutral700
import com.muhammetkonukcu.moviefinder.theme.White
import moviefinder.composeapp.generated.resources.Res
import moviefinder.composeapp.generated.resources.back
import moviefinder.composeapp.generated.resources.check_internet_connection
import moviefinder.composeapp.generated.resources.mi_refresh
import moviefinder.composeapp.generated.resources.ph_caret_left
import moviefinder.composeapp.generated.resources.placeholder_dark
import moviefinder.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoadAsyncImage(
    imagePath: String?,
    posterSize: Int = 500,
    contentDescription: String,
    width: Int,
    height: Int,
    contentScale: ContentScale = ContentScale.FillBounds,
    cornerRadius: RoundedCornerShape = RoundedCornerShape(0.dp),
    placeholderRes: Painter = painterResource(Res.drawable.placeholder_dark),
    errorRes: Painter = painterResource(Res.drawable.placeholder_dark),
) {
    val size = if (posterSize == 0) "original" else "$posterSize"
    val baseUrl = "https://image.tmdb.org/t/p/"
    AsyncImage(
        model = "${baseUrl}w$size$imagePath",
        placeholder = placeholderRes,
        error = errorRes,
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = Modifier
            .size(width.dp, height.dp)
            .clip(cornerRadius)
    )
}

@Composable
fun LoadAsyncImage(
    modifier: Modifier,
    imagePath: String?,
    posterSize: Int = 500,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Painter = painterResource(Res.drawable.placeholder_dark),
    errorRes: Painter = painterResource(Res.drawable.placeholder_dark),
) {
    val size = if (posterSize == 0) "original" else "$posterSize"
    val baseUrl = "https://image.tmdb.org/t/p/"
    AsyncImage(
        modifier = modifier,
        model = "${baseUrl}w$size$imagePath",
        contentDescription = contentDescription,
        contentScale = contentScale,
        placeholder = placeholderRes,
        error = errorRes,
    )
}

@Composable
fun LoadAsyncImage(
    modifier: Modifier,
    model: Any?,
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Painter = painterResource(Res.drawable.placeholder_dark),
    errorRes: Painter = painterResource(Res.drawable.placeholder_dark),
) {
    AsyncImage(
        modifier = modifier,
        model = model,
        contentDescription = contentDescription,
        contentScale = contentScale,
        placeholder = placeholderRes,
        error = errorRes,
    )
}

@Composable
fun DonutProgress(
    progress: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    textSize: TextUnit = 14.sp,
    strokeWidth: Dp = 3.dp,
    indicatorBackgroundColor: Color = Color(0x25FFFFFF),
    progressColor: Color = AppPrimaryColor,
    donutBackgroundColor: Color = Color(0x337F55E0),
    strokeCap: StrokeCap = StrokeCap.Round,
    gapSize: Dp = 0.dp
) {
    val progressFraction = (progress.coerceIn(0, 100) / 100f)

    Box(
        modifier = modifier.size(size).background(
            color = donutBackgroundColor,
            shape = RoundedCornerShape(size / 2)
        ),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier.fillMaxSize(),
            color = progressColor,
            strokeWidth = strokeWidth,
            trackColor = indicatorBackgroundColor,
            strokeCap = strokeCap,
            gapSize = gapSize
        )

        Text(
            text = "$progress%",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = textSize),
        )
    }
}

@Composable
fun DetailText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x337F55E0), shape = RoundedCornerShape(8.dp))
            .border(1.dp, AppPrimaryColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}

@Composable
fun MoviePlaceholderItem() {
    Box(
        modifier = Modifier
            .size(100.dp, 150.dp)
            .background(color = Neutral700.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun TopMoviePlaceholderItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.5f)
            .background(color = Neutral700.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
    )
}

@Composable
fun TopMoviePlaceholderItem(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f / 1.5f)
            .background(color = Neutral700.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
    ) {
        Row(modifier = Modifier.statusBarsPadding().fillMaxWidth().padding(horizontal = 12.dp)) {
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
        }
    }
}

@Composable
fun ActorPlaceholderItem() {
    Column(
        modifier = Modifier.width(118.dp)
    ) {
        Box(
            modifier = Modifier.width(118.dp).height(150.dp)
                .background(color = Neutral700.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.height(2.dp))
        TextPlaceholder(
            modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.CenterHorizontally)
                .height(10.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        TextPlaceholder(
            modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally).height(8.dp)
        )
    }
}

@Composable
fun ImagePlaceholderItem(width: Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .aspectRatio(16f / 9f)
            .background(color = Neutral700.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp))
    )
}

@Composable
fun DetailTitleDescPlaceholder() {
    Column(modifier = Modifier.fillMaxWidth()) {
        TextPlaceholder(
            modifier = Modifier.fillMaxWidth(0.5f)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(24.dp)
        )
        TextPlaceholder(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .height(24.dp)
        )
    }
}

@Composable
fun TextPlaceholder(modifier: Modifier = Modifier) {
    Text(
        modifier = modifier
            .background(
                color = Neutral700.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        text = ""
    )
}

@Composable
fun LoadingItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.onTertiary,
            strokeWidth = 2.dp,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun ErrorItem(
    onRetryClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(Res.string.check_internet_connection),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onTertiary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        IconButton(
            onClick = onRetryClick
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                painter = painterResource(Res.drawable.mi_refresh),
                contentDescription = stringResource(Res.string.refresh),
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}