package com.rick.data.ui_components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest


@Composable
fun EcsCatalogCard(
    itemTitle: String,
    itemImage: String,
    itemId: String,
    itemSummary: String,
    onFavClick: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.data_ui_components_common_background)
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp)
        ) {
            val (title, card, favorite, synopsis) = createRefs()
            EcsText(
                text = itemTitle,
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )
            Card(
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.data_ui_components_common_buttons)),
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(title.bottom)
                    bottom.linkTo(synopsis.top)
                }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(itemImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.data_ui_components_common_favorite),
                    placeholder = painterResource(id = R.drawable.data_ui_components_common_fav_filled_icon),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp, max = 350.dp),
                    contentScale = ContentScale.FillHeight,
                )
            }
            IconButton(
                onClick = { onFavClick(itemId) },
                modifier = Modifier.constrainAs(favorite) {
                    bottom.linkTo(card.bottom)
                    end.linkTo(card.end)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = stringResource(
                        R.string.data_ui_components_common_favorite
                    ),
                    modifier = Modifier.size(width = 36.dp, height = 31.dp)
                )
            }
            EcsText(
                text = itemSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(synopsis) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
            )
        }
    }
}


@Composable
fun EcsBookCatalogCard(
    image: String,
    title: String,
    author: String,
    id: String,
    onFavClick: (String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .requiredHeight(82.dp)
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.data_ui_components_common_background).copy(
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
                    .data(image)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.data_ui_components_common_favorite),
                placeholder = painterResource(R.drawable.data_ui_components_common_fav_filled_icon),
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
                EcsText(
                    text = title,
                    modifier = Modifier,
                    maxLines = 2
                )
                EcsText(
                    text = author,
                    modifier = Modifier,
                    maxLines = 2
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = { onFavClick(id) },
                modifier = Modifier
                    .requiredSize(65.dp)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = stringResource(
                        R.string.data_ui_components_common_favorite
                    )
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewBookItem() {
//    EcsBookCatalogCard(favorite = Favorite(
//        1,
//        "Book title",
//        "Book author",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
////        type = "book",
//        isFavorite = true
//    ), onItemClick = {}, onFavClick = {})
//}

