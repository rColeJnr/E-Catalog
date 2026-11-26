package com.rick.ecatalogs.game.screen_game.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rick.ecatalogs.data.ui_components.common.EcsPosterItem
import com.rick.ecatalogs.data.ui_components.common.EcsTheme
import com.rick.ecatalogs.game.data_game.model.Game
import com.rick.ecatalogs.game.data_game.model.UserGame
import com.rick.ecatalogs.game.data_game.model.coverUrl

/** A game in the catalog, search results and favorites: cover, title, rating · year · platforms, genres. */
@Composable
fun GameListItem(
    item: UserGame,
    onClick: (Game) -> Unit,
    onFavoriteClick: (Game, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val game = item.game
    Surface(
        onClick = { onClick(game) },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Row(Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(game.coverUrl())
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(84.dp)
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(10.dp)),
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = game.subtitle(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
                if (game.genres.isNotEmpty()) {
                    Text(
                        text = game.genres.joinToString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                if (game.summary.isNotBlank()) {
                    Text(
                        text = game.summary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
            Box(contentAlignment = Alignment.TopEnd) {
                FavoriteButton(item.isFavorite) { onFavoriteClick(game, !item.isFavorite) }
            }
        }
    }
}

@Composable
fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = stringResource(
                if (isFavorite) R.string.game_screen_game_common_remove_favorite
                else R.string.game_screen_game_common_add_favorite
            ),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

fun Game.asPosterItem() = EcsPosterItem(
    id = id,
    title = name,
    imageUrl = coverUrl(),
    subtitle = listOfNotNull(releaseDateText(), ratingText()).joinToString(" · ").ifEmpty { null },
)

@Preview
@Composable
private fun PreviewGameListItem() {
    EcsTheme {
        GameListItem(
            item = UserGame(
                Game(1, "Hollow Knight", null, "Explore a vast ruined kingdom.", 89.0, 1500, 1488499200,
                    listOf("Platform", "Indie"), listOf("PC", "Switch")),
                isFavorite = true,
            ),
            onClick = {},
            onFavoriteClick = { _, _ -> },
        )
    }
}
