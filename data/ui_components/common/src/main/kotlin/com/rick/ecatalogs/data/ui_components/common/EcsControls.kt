package com.rick.ecatalogs.data.ui_components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rick.ecatalogs.data.ui_design.R

/**
 * The app's back arrow, as the XML toolbars show it (`data_ui_design_back_icon`), for Compose top
 * bars. Tinted with the content colour like the toolbars' navigation icon.
 */
@Composable
fun EcsBackButton(onClick: () -> Unit, contentDescription: String, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(painterResource(R.drawable.data_ui_design_back_icon), contentDescription = contentDescription)
    }
}

/**
 * The favorite button of the XML cards (`DataUiDesignFavoriteButton`): the app's star, white, on a
 * 40dp frosted-glass circle. Filled when [isFavorite]. [small] is the book covers' size
 * (`DataUiDesignFavoriteButton.Small`: 32dp circle, 18dp star), for small images.
 */
@Composable
fun EcsGlassFavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    small: Boolean = false,
) {
    IconButton(onClick = onClick, modifier = modifier.size(if (small) 32.dp else 40.dp).glass(CircleShape)) {
        Icon(
            painter = painterResource(
                if (isFavorite) R.drawable.data_ui_design_favorite_filled
                else R.drawable.data_ui_design_favorite_outlined
            ),
            contentDescription = contentDescription,
            tint = colorResource(R.color.data_ui_design_on_scrim),
            // The star drawables are wider than tall: fitted in, as the XML button's inset does
            modifier = Modifier.size(if (small) 18.dp else 24.dp),
        )
    }
}

/**
 * A search result like the XML search screens' cards (e.g. `movie_screen_movie_trending_series_search_search_entry`):
 * a wide image with the glass favorite on it, the title, a two-line summary, and a bottom line with
 * the [facts] (year, rating…) on the left and "Show translation" on the right (when a
 * [LocalTextTranslator] is provided).
 */
@Composable
fun EcsSearchResultCard(
    title: String,
    imageUrl: String?,
    summary: String,
    facts: List<String>,
    isFavorite: Boolean,
    favoriteDescription: String,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(Modifier.padding(8.dp)) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer),
                )
                EcsGlassFavoriteButton(
                    isFavorite = isFavorite,
                    onClick = onFavoriteClick,
                    contentDescription = favoriteDescription,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
            val translation = rememberTranslationToggle(summary)
            if (summary.isNotBlank()) {
                Text(
                    translation.shownText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 2.dp),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            ) {
                Text(
                    facts.joinToString("  ·  "),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.data_ui_design_text_secondary),
                    modifier = Modifier.weight(1f),
                )
                val label = translation.label
                if (summary.isNotBlank() && label != null) {
                    Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.data_ui_design_color_buttons),
                        modifier = Modifier
                            .clickable(enabled = translation.enabled, onClick = translation.onToggle)
                            .padding(2.dp),
                    )
                }
            }
        }
    }
}
