package com.rick.data.ui_components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFD4D4D4), Color(0xFFFFFFFF)),
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(bottom = 16.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(itemImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    placeholder = painterResource(id = com.rick.data.ui_design.R.drawable.data_ui_design_favorite_filled),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            EcsText(
                text = itemTitle,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                maxLines = 1
            )

            EcsTextSmaller(
                text = itemSummary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                maxLines = 5
            )

            EcsTextButton(
                text = "Remove from favorites",
                color = colorResource(android.R.color.holo_blue_dark),
                onClick = { onFavClick(itemId) },
                modifier = Modifier
                    .padding(top = 8.dp)
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
    val backgroundGradient = linearGradient(
        colors = listOf(
            colorResource(com.rick.data.ui_design.R.color.data_ui_design_color_surface), // end color
            colorResource(com.rick.data.ui_design.R.color.data_ui_design_color_on_surface), // start color
        ),
        start = Offset.Infinite,
        end = Offset.Zero
    )

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = backgroundGradient)
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    placeholder = painterResource(com.rick.data.ui_design.R.drawable.data_ui_design_favorite_filled),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            EcsText(
                text = title,
                fontSize = 17.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                maxLines = 1,
            )

            EcsTextSmaller(
                text = author,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, start = 1.dp, end = 1.dp),
                maxLines = 1,
            )

            EcsTextButton(
                text = "Remove from favorites",
                color = colorResource(android.R.color.holo_blue_dark),
                onClick = { onFavClick(id) },
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewBookCard() {
    EcsBookCatalogCard(image = "", title = "Book title", author = "Book authro", id = "") {

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

