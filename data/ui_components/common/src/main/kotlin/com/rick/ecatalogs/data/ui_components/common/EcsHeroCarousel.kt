package com.rick.ecatalogs.data.ui_components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rick.ecatalogs.data.ui_design.R

/**
 * A titled centered carousel: the current card sits in the middle with at most one smaller card
 * peeking on each side (none at the first and last card); swipes snap card by card, and cards
 * grow as they move to the center. Titles sit on the image and fade out on the side cards.
 * Nothing is shown when [items] is empty. Matches the View carousels (useHeroCarousel).
 */
@Composable
fun EcsHeroCarousel(
    title: String,
    items: List<EcsPosterItem>,
    onClick: (EcsPosterItem) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 240.dp,
) {
    if (items.isEmpty()) return
    Column(modifier) {
        EcsSectionTitle(title)
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val cardWidth = maxWidth * CARD_WIDTH
            val sidePadding = (maxWidth - cardWidth) / 2
            val state = rememberPagerState { items.size }
            HorizontalPager(
                state = state,
                contentPadding = PaddingValues(horizontal = sidePadding),
                pageSpacing = 8.dp,
                modifier = Modifier.fillMaxWidth().height(height),
                key = { items[it].id },
            ) { page ->
                val item = items[page]
                // 0 for the centered card, 1 for a neighbour; negative offset = left of center
                val offset = state.getOffsetDistanceInPages(page)
                val distance = abs(offset).coerceIn(0f, 1f)
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            // Side cards shrink towards the centered card, hugging it
                            transformOrigin = TransformOrigin(if (offset < 0f) 1f else 0f, 0.5f)
                            val scale = lerp(1f, SIDE_SCALE, distance)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(1f, SIDE_ALPHA, distance)
                        }
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(colorResource(R.color.data_ui_design_color_on_surface))
                        .clickable { onClick(item) }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        colorResource(R.color.data_ui_design_scrim_mid),
                                        colorResource(R.color.data_ui_design_scrim),
                                    )
                                )
                            )
                    )
                    Column(
                        Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                            .alpha((1f - distance * TEXT_FADE_SPEED).coerceIn(0f, 1f))
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        item.subtitle?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float) = start + (stop - start) * fraction

/** Same proportions as the View carousels (CarouselMotion.kt). */
private const val CARD_WIDTH = 0.72f
private const val SIDE_SCALE = 0.84f
private const val SIDE_ALPHA = 0.75f
private const val TEXT_FADE_SPEED = 2.5f

@Preview
@Composable
private fun PreviewHeroCarousel() {
    EcsTheme {
        EcsHeroCarousel(
            title = "This season",
            items = (1..5).map { EcsPosterItem(it, "Title $it", null, "★ 8.$it") },
            onClick = {},
        )
    }
}
