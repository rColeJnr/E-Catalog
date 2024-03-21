package com.rick.ui_components.favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FavScreen(favorites: List<Favorite>) {


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.background).copy(alpha = 0.8f))
        ) {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(favorites) { favorite ->
                    FavoriteListItem(favorite, {}, {})
                }
            }
        }
    }
}

@Composable
fun FavoriteListItem(
    favorite: Favorite,
    onItemClick: (Favorite) -> Unit,
    onFavClick: (Favorite) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .requiredHeight(82.dp)
            .clickable {
                onItemClick(favorite)
            }
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.background).copy(
                alpha = 0.5f
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(favorite.image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.favorite_fragment),
                placeholder = painterResource(R.drawable.filled_icon),
                modifier = Modifier.height(250.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(
                modifier = Modifier
                    .wrapContentWidth(align = Alignment.Start)
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = favorite.title,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
                Text(
                    text = favorite.author,
                    style = MaterialTheme.typography.bodyMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = { onFavClick(favorite) },
                modifier = Modifier
                    .requiredSize(65.dp)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.fav_filled_icon),
                    contentDescription = stringResource(
                        id = R.string.favorite_button
                    ),
                    tint = colorResource(id = R.color.icons)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBookItem() {
    FavoriteListItem(favorite = Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ), onItemClick = {}, onFavClick = {})
}

@Preview
@Composable
fun FavScreenPrev() {
    MaterialTheme {
        FavScreen(
            favorites = dummyBooks
        )
    }
}

val dummyBooks = listOf(
    Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title",
        "Book author and author book and assistance",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title, why no long title example are you afradid of long titles",
        "Book author",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
    Favorite(
        1,
        "Book title mid long titlle, gotta check erthing",
        "Book author and author book",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png"
    ),
)