package com.rick.ecatalogs.data.ui_components.common

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rick.ecatalogs.data.translation.TextTranslator
import com.rick.ecatalogs.data.ui_design.R
import kotlinx.coroutines.launch
import com.rick.ecatalogs.data.ui_components.common.R as UiR

/**
 * Favorites card in the same style as the XML poster cards: the poster fills the card, the title
 * sits on a dark gradient over it, and the summary follows below. Tapping the card opens the
 * item ([onClick]); the heart removes it from favorites.
 */
@Composable
fun EcsCatalogCard(
    itemTitle: String,
    itemImage: String,
    itemId: String,
    itemSummary: String,
    onFavClick: (String) -> Unit,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        PosterCard(
            image = itemImage,
            height = 360.dp,
            onRemove = { onFavClick(itemId) },
            onClick = onClick,
        ) {
            Text(
                text = itemTitle,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (itemSummary.isNotBlank()) {
            TranslatableSummary(itemSummary)
        }
    }
}

/**
 * Translator for the summaries of [EcsCatalogCard]s: a screen provides one to offer
 * "Show translation" under each summary.
 */
val LocalTextTranslator = staticCompositionLocalOf<TextTranslator?> { null }

@Composable
private fun TranslatableSummary(summary: String) {
    val translator = LocalTextTranslator.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var translation by remember(summary) { mutableStateOf<String?>(null) }
    var showingTranslation by remember(summary) { mutableStateOf(false) }
    var translating by remember(summary) { mutableStateOf(false) }

    Crossfade(
        targetState = if (showingTranslation) translation ?: summary else summary,
        label = "summary",
    ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 10.dp),
        )
    }
    if (translator != null && translator.isAvailable) {
        TextButton(
            enabled = !translating,
            onClick = {
                when {
                    showingTranslation -> showingTranslation = false
                    translation != null -> showingTranslation = true
                    else -> scope.launch {
                        translating = true
                        translation = translator.translate(summary)
                        translating = false
                        if (translation != null) {
                            showingTranslation = true
                        } else {
                            Toast.makeText(
                                context,
                                UiR.string.data_ui_components_common_translation_failed,
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                }
            },
        ) {
            Text(
                stringResource(
                    when {
                        translating -> UiR.string.data_ui_components_common_translating
                        showingTranslation -> UiR.string.data_ui_components_common_show_original
                        else -> UiR.string.data_ui_components_common_show_translation
                    },
                ),
            )
        }
    }
}

/** Book favorites card (grid / list): cover with title and author over a gradient. */
@Composable
fun EcsBookCatalogCard(
    image: String,
    title: String,
    author: String,
    id: String,
    onFavClick: (String) -> Unit,
    onClick: () -> Unit = {},
) {
    PosterCard(
        image = image,
        height = 260.dp,
        onRemove = { onFavClick(id) },
        onClick = onClick,
        modifier = Modifier.padding(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (author.isNotBlank()) {
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PosterCard(
    image: String,
    height: Dp,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overlay: @Composable () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box(Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            // Gradient that keeps the text readable on any poster
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
                    .padding(16.dp)
            ) { overlay() }
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(40.dp)
                    .glass(CircleShape),
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = colorResource(R.color.data_ui_design_on_scrim),
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewCatalogCard() {
    EcsTheme {
        EcsCatalogCard(
            itemTitle = "Dune: Part Two",
            itemImage = "",
            itemId = "1",
            itemSummary = "Paul Atreides unites with the Fremen to wage war against the conspirators.",
            onFavClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewBookCard() {
    EcsTheme {
        EcsBookCatalogCard(
            image = "", title = "Book title", author = "Book author", id = "", onFavClick = {},
        )
    }
}

/** Frosted glass: translucent body, white sheen from the top, thin light rim (matches the XML). */
@Composable
private fun Modifier.glass(shape: Shape): Modifier {
    val fill = colorResource(R.color.data_ui_design_glass_fill)
    val sheen = colorResource(R.color.data_ui_design_glass_sheen)
    val rim = colorResource(R.color.data_ui_design_glass_rim)
    return this
        .clip(shape)
        .background(fill, shape)
        .background(Brush.verticalGradient(0f to sheen, 0.5f to Color.Transparent), shape)
        .border(1.dp, rim, shape)
}
