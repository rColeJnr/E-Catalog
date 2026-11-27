package com.rick.ecatalogs.data.trailer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

/** One page of a details screen's header. */
sealed interface GalleryItem {
    val key: String

    data class Trailer(val videoId: String) : GalleryItem {
        override val key get() = "trailer:$videoId"
    }

    /** [wide] images (16:9 backdrops) are shown whole, over a blurred copy; others fill the page. */
    data class Image(val url: String, val wide: Boolean = false) : GalleryItem {
        override val key get() = "image:$url"
    }
}

/**
 * The swipeable header of a details screen: the trailer first (tap to play here), then the images.
 * Images fade into the page at the bottom; the trailer page doesn't, so the player's controls stay
 * visible. When the trailer arrives after the images (details load later than the poster), the
 * gallery jumps to it unless the user has already swiped.
 *
 * [onFirstImageLoaded] is called once, when the first page's image has loaded or failed (screens
 * hold their enter transition until then).
 */
@Composable
fun MediaGallery(
    items: List<GalleryItem>,
    title: String,
    modifier: Modifier = Modifier,
    onFirstImageLoaded: () -> Unit = {},
) {
    if (items.isEmpty()) {
        Box(modifier)
        return
    }
    val pagerState = rememberPagerState { items.size }
    var userSwiped by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(pagerState) {
        pagerState.interactionSource.interactions.collect {
            if (it is DragInteraction.Start) userSwiped = true
        }
    }
    val firstKey = items.first().key
    val currentOnFirstImageLoaded by rememberUpdatedState(onFirstImageLoaded)
    var reported by remember { mutableStateOf(false) }
    val onImageLoaded: (page: Int) -> Unit = { page ->
        if (page == 0 && !reported) {
            reported = true
            currentOnFirstImageLoaded()
        }
    }
    LaunchedEffect(firstKey) {
        if (items.first() is GalleryItem.Trailer && !userSwiped) pagerState.scrollToPage(0)
    }

    Box(modifier) {
        HorizontalPager(
            state = pagerState,
            key = { items[it].key },
            modifier = Modifier.fillMaxSize().testTag(TAG_GALLERY),
        ) { page ->
            when (val item = items[page]) {
                is GalleryItem.Trailer -> InlineTrailer(
                    videoId = item.videoId,
                    title = title,
                    isVisible = pagerState.currentPage == page,
                    modifier = Modifier.fillMaxSize(),
                    onThumbnailLoaded = { onImageLoaded(page) },
                )

                is GalleryItem.Image -> GalleryImage(item, title, onLoaded = { onImageLoaded(page) })
            }
        }
        if (items.size > 1) {
            PageDots(
                count = items.size,
                current = pagerState.currentPage,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            )
        }
    }
}

/**
 * A trailer that plays where it is: YouTube's thumbnail with a play button, then the player.
 * Its fullscreen button opens the trailer screen at the same second. Pauses while not [isVisible];
 * keeps its position across rotation. If YouTube refuses to play it here (embedding disabled by
 * the owner), offers to open it in YouTube.
 */
@Composable
fun InlineTrailer(
    videoId: String,
    title: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    onThumbnailLoaded: () -> Unit = {},
) {
    val context = LocalContext.current
    var playing by rememberSaveable(videoId) { mutableStateOf(false) }
    var position by rememberSaveable(videoId) { mutableFloatStateOf(0f) }
    var failed by rememberSaveable(videoId) { mutableStateOf(false) }

    Box(modifier.background(Color.Black), contentAlignment = Alignment.Center) {
        when {
            failed -> TrailerUnavailable(onOpenInYouTube = { openInYouTube(context, videoId) })
            playing -> YouTubePlayerHost(
                videoId = videoId,
                startSeconds = position,
                paused = !isVisible,
                onSecond = { position = it },
                onError = { failed = true },
                onFullscreenClick = { TrailerActivity.start(context, videoId, title, position) },
                modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
            )

            else -> TrailerThumbnail(videoId, onPlay = { playing = true }, onLoaded = onThumbnailLoaded)
        }
    }
}

@Composable
private fun TrailerThumbnail(videoId: String, onPlay: () -> Unit, onLoaded: () -> Unit) {
    val playLabel = stringResource(R.string.data_trailer_play_trailer)
    Box(
        Modifier
            .fillMaxSize()
            .clickable(role = Role.Button, onClick = onPlay)
            .semantics { contentDescription = playLabel }
            .testTag(TAG_PLAY_TRAILER),
        contentAlignment = Alignment.Center,
    ) {
        // hqdefault is letterboxed; cropping to 16:9 removes the black bars
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(youtubeThumbnailUrl(videoId))
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            onSuccess = { onLoaded() },
            onError = { onLoaded() },
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        )
        Box(
            Modifier
                .size(64.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }
        Text(
            stringResource(R.string.data_trailer_trailer),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                .padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
            color = Color.White,
        )
    }
}

@Composable
internal fun TrailerUnavailable(onOpenInYouTube: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            stringResource(R.string.data_trailer_unavailable),
            color = Color.White,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
        TextButton(onClick = onOpenInYouTube) {
            Text(stringResource(R.string.data_trailer_open_in_youtube))
        }
    }
}

@Composable
private fun GalleryImage(image: GalleryItem.Image, title: String, onLoaded: () -> Unit) {
    val request = ImageRequest.Builder(LocalContext.current).data(image.url).crossfade(true).build()
    Box(Modifier.fillMaxSize()) {
        if (image.wide) {
            // A 16:9 image cropped to this page would lose its sides: shown whole, with a blurred,
            // dimmed copy filling the rest (not blurred before Android 12, so dimmed more)
            AsyncImage(
                model = request,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(24.dp),
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = WIDE_BACKGROUND_DIM)))
        }
        AsyncImage(
            model = request,
            contentDescription = title,
            contentScale = if (image.wide) ContentScale.Fit else ContentScale.Crop,
            onSuccess = { onLoaded() },
            onError = { onLoaded() },
            modifier = Modifier.fillMaxSize(),
        )
        // Fades the image into the page, like the single header image did
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.background))
                )
        )
    }
}

@Composable
private fun PageDots(count: Int, current: Int, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.data_trailer_page_of, current + 1, count)
    Row(
        modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(count) { index ->
            Box(
                Modifier
                    .size(if (index == current) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = if (index == current) 0.95f else 0.5f))
            )
        }
    }
}

private val WIDE_BACKGROUND_DIM =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) 0.35f else 0.7f

/** Test tags: the gallery pager and the trailer's play button. */
const val TAG_GALLERY = "data_trailer_gallery"
const val TAG_PLAY_TRAILER = "data_trailer_play"
