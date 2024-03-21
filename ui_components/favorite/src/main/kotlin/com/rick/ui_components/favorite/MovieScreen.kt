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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
    articles: List<Favorite>,
    movies: List<Favorite>,
    series: List<Favorite>,
    showArticles: Boolean,
    shouldShowArticles: (Boolean) -> Unit,
    showMovies: Boolean,
    shouldShowMovies: (Boolean) -> Unit,
    showSeries: Boolean,
    shouldShowSeries: (Boolean) -> Unit,
    onFavClick: (Favorite) -> Unit,
) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        containerColor = colorResource(id = R.color.background)
    ) {
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
                .fillMaxSize()
                .background(colorResource(id = R.color.background).copy(alpha = 0.8f))
        ) {
            SectionRow("Ny articles", showArticles, shouldShowArticles)
            AnimatedVisibilityBox(
                screenState = showArticles,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .wrapContentHeight()
                ) {
                    items(articles) { favorite ->
                        FavoriteListItem(favorite = favorite, onFavClick = onFavClick)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            SectionRow(text = "Movies", isVisible = showMovies, shouldShowMovies)
            AnimatedVisibilityBox(
                screenState = showMovies,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .wrapContentHeight()
                ) {
                    items(movies) { favorite ->
                        FavoriteListItem(favorite = favorite, onFavClick = onFavClick)
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            SectionRow(text = "Series", isVisible = showSeries, shouldShowSeries)
            AnimatedVisibilityBox(
                screenState = showMovies,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(it)
                        .wrapContentHeight()
                ) {
                    items(series) { favorite ->
                        FavoriteListItem(favorite = favorite, onFavClick = onFavClick)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionRow(text: String, isVisible: Boolean, onClick: (Boolean) -> Unit) {
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
fun FavoriteListItem(favorite: Favorite, onFavClick: (Favorite) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onFavClick(favorite)
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
            val (title, card, favButton, synopsis) = createRefs()
            JikanText(
                text = favorite.title ?: "no title found",
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
                        .data(favorite.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.favorite),
                    placeholder = painterResource(id = R.drawable.fav_filled_icon),
                    modifier = Modifier
                        .height(dimensionResource(id = R.dimen.image_height))
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillHeight,
                )
            }
            IconButton(
                onClick = { onFavClick(favorite) },
                modifier = Modifier.constrainAs(favButton) {
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
                text = favorite.summary
                    ?: ("no synopsis found, maybe i should give u a bit more text, " +
                            "also, i should wrap you and give u eclipses dots"),
                modifier = Modifier
                    .width(346.dp)
                    .constrainAs(synopsis) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
            )
        }
    }
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
fun PreviewFavoriteListItem() {
    FavoriteListItem(favorite = Favorite(
        1,
        "Title Of movie",
        "Summary of favorite",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie",
        isFavorite = false
    ), onFavClick = {})
}

@Preview
@Composable
fun PreviewFavScreen() {
    FavScreen(
        articles = dummynyMovies,
        movies = dummymovies,
        series = dummyseries,
        showArticles = true,
        shouldShowArticles = {},
        showMovies = true,
        shouldShowMovies = {},
        showSeries = true,
        shouldShowSeries = {},
        onFavClick = {}
    )

}

val dummynyMovies = listOf(
    Favorite(
        1,
        "Movie title",
        "Movie summary",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie",
        isFavorite = false
    )
)
val dummymovies = listOf(
    Favorite(
        3,
        "Title Of movie",
        "Summary Of movie, just to see how it looks when long like extra extra long",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie",
        isFavorite = false
    ),
    Favorite(
        4,
        "Title Of movie, just to see how",
        "Summary Of movie, just to see how it looks when long like extra extra long",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie",
        isFavorite = false
    )
)
val dummyseries = listOf(
    Favorite(
        5,
        "Title Of movie, just to see how it looks when long like extra extra long",
        "Summary Of movie, just to see how it looks when long like extra extra long",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie",
        isFavorite = false
    ),
    Favorite(
        5,
        "Title Of movie, just to see",
        "Summary Of movie, just to see how it",
        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
        "movie",
        isFavorite = false
    )
)