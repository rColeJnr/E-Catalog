package com.rick.ui_components.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun FavScreen(
    anime: List<Any>,
    manga: List<Any>,
    onFavClick: (Any) -> Unit,
    shouldShowAnime: (Boolean) -> Unit,
    showAnime: Boolean,
    shouldShowManga: (Boolean) -> Unit,
    showManga: Boolean,
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
                .fillMaxSize()
                .background(colorResource(id = R.color.background).copy(alpha = 0.8f))
        ) {
            JikanSectionRow("Anime", showAnime, shouldShowAnime)
            AnimatedVisibilityBox(
                screenState = showAnime,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .wrapContentHeight()
                ) {
                    items(anime) { jikan ->
                        AnimeItem(jikan = jikan, onFavClick = onFavClick)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            JikanSectionRow(text = "Manga", isVisible = showManga, shouldShowManga)
            AnimatedVisibilityBox(
                screenState = showManga,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .wrapContentHeight()
                ) {
                    items(manga) { jikan ->
                        AnimeItem(jikan = jikan, onFavClick = onFavClick)
                    }
                }
            }
        }
    }
}

@Composable
fun JikanSectionRow(text: String, isVisible: Boolean, onClick: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(isVisible) }
            .padding(start = 6.dp),
    ) {
        JikanText(
            text = text,
            fontSize = 28.sp,
            modifier = Modifier
        )
        Icon(
            imageVector = if (isVisible) Icons.Filled.ArrowDropDown else Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
fun AnimeItem(jikan: JikanFavorite, onFavClick: (JikanFavorite) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onFavClick(jikan)
            }
            .padding(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.background)
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(6.dp)
        ) {
            val (title, card, favorite, synopsis, rating) = createRefs()
            JikanText(
                text = jikan.title ?: "no title found",
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
            )
//            Spacer(modifier = Modifier.height(2.dp))
            Card(
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.buttons)),
                modifier = Modifier.constrainAs(card) {
                    top.linkTo(title.bottom)
                    bottom.linkTo(synopsis.top)
                }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(jikan.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.fav_jikan),
                    placeholder = painterResource(id = R.drawable.fav_filled_icon),
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.image_height))
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillHeight,
                )
            }
            IconButton(
                onClick = { onFavClick(jikan) },
                modifier = Modifier.constrainAs(favorite) {
                    bottom.linkTo(card.bottom)
                    end.linkTo(card.end)
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = stringResource(
                        R.string.favorite
                    )
                )
            }
            JikanText(
                text = jikan.synopsis
                    ?: ("no synopsis found, maybe i should give u a bit more text, " +
                            "also, i should wrap you and give u eclipses dots"),
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

@Preview
@Composable
fun PreviewAnimeItem() {
    AnimeItem(
        jikan = JikanFavorite(
            0,
            "Long a bit tilte here",
            "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
            "Synopis, a little bit of bla bla bla balblabla  text ext and more text a bit",
            "PG-13",
            "Manga"
        ), onFavClick = {})
}

@Composable
fun JikanText(modifier: Modifier, text: String, fontSize: TextUnit = 22.sp) {
    Text(
        text = text,
        fontSize = fontSize,
        fontFamily = FontFamily(Font(R.font.high_tower_text, FontWeight.Bold)),
        textAlign = TextAlign.Start,
        modifier = modifier
            .padding(bottom = 2.dp)
    )
}


@Composable
fun AnimatedVisibilityBox(
    screenState: Boolean,
    density: Density,
    fromTop: Dp = 0.dp,
    modifier: Modifier,
    composable: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = screenState,
        enter = slideInVertically {
            // Slide in from 40 dp from the top.
            with(density) { fromTop.roundToPx() }
        } + expandVertically(
            // Expand from the top.
            expandFrom = Alignment.Top
        ) + fadeIn(
            // Fade in with the initial alpha of 0.3f.
            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        composable()
    }
}

@Preview
@Composable
fun JikanItemPrev() {
    FavScreen(
        anime = dummyData,
//        listOf(
//            JikanFavorite(
//                3,
//                "Dorathans sword",
//                "",
//                "THIs is a made up title by meself",
//                "Rated 18+",
//                "Typing types"
//            )
//        ),
        manga = dummyData, {},
        shouldShowManga = {},
        showManga = true,
        shouldShowAnime = {},
        showAnime = true
    )
}

private val dummyData = listOf(
    JikanFavorite(
        0,
        "One Piece",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "Synopis, a little bit of bla bla bla balblabla  text ext and more text",
        "PG-13",
        "Manga"
    ),
    JikanFavorite(
        1,
        "One punch man",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "Synopis, a little bit of bla bla bla balblabla  text ext and more text",
        "PG-13",
        "TV"
    ),
    JikanFavorite(
        2,
        "Hakuna Matata",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "Synopis, a little bit of bla bla bla balblabla  text ext and more text",
        "PG-13",
        "TV"
    ),
)