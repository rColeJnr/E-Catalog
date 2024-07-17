package com.rick.ui_components.movie_favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.rick.data.model_movie.FavoriteUiState
import com.rick.data.ui_components.common.EcsCatalogCard
import com.rick.data.ui_components.common.EcsEmptyState
import com.rick.data.ui_components.common.EcsScaffold
import com.rick.data.ui_components.common.EcsSnackbar
import com.rick.data.ui_components.movie_favorite.R

@Composable
fun MovieFavScreen(
    state: FavoriteUiState,
    onFavClick: (String) -> Unit,
    shouldDisplayUndoFavorite: Boolean,
    undoFavoriteRemoval: () -> Unit,
    clearUndoState: () -> Unit,
) {

    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    LaunchedEffect(
        key1 = shouldDisplayUndoFavorite
    ) {
        if (shouldDisplayUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = context.getString(R.string.data_ui_components_movie_favorite_favorite_removed),
                label = context.getString(R.string.data_ui_components_movie_favorite_undo),
                undoRemoval = undoFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }
    }

    EcsScaffold(
        snackbarHostState = snackbarHostState
    ) {
        when (state) {
            FavoriteUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator(modifier = Modifier.wrapContentSize()) }
            }

            is FavoriteUiState.ArticlesFavorites -> {
                if (state.favorites.isEmpty()) {
                    EcsEmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(state.favorites) { article ->
                            EcsCatalogCard(
                                itemTitle = article.headline,
                                itemSummary = article.leadParagraph,
                                itemImage = "https://www.nytimes.com/${article.multimedia}",
                                itemId = article.id,
                                onFavClick = onFavClick
                            )
                        }
                    }
                }
            }

            is FavoriteUiState.TrendingMoviesFavorites -> {
                if (state.favorites.isEmpty()) {
                    EcsEmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(state.favorites) { movie ->
                            EcsCatalogCard(
                                itemTitle = movie.title,
                                itemSummary = movie.overview,
                                itemImage = getTmdbImageUrl(movie.image),
                                itemId = movie.id.toString(),
                                onFavClick = onFavClick
                            )
                        }
                    }
                }
            }

            is FavoriteUiState.TrendingSeriesFavorites -> {
                if (state.favorites.isEmpty()) {
                    EcsEmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(state.favorites) { series ->
                            EcsCatalogCard(
                                itemTitle = series.name,
                                itemSummary = series.overview,
                                itemImage = getTmdbImageUrl(series.image),
                                itemId = series.id.toString(),
                                onFavClick = onFavClick
                            )
                        }
                    }
                }
            }
        }

    }
}

private fun getTmdbImageUrl(url: String): String = "https://image.tmdb.org/t/p/w200$url"

//
//@Preview
//@Composable
//fun PreviewFavoriteListItem() {
//    FavoriteListItem(favorite = Favorite(
//        1,
//        "Title Of movie",
//        "Summary of favorite",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "movie",
//        isFavorite = false
//    ), onFavClick = {})
//}
//
//@Preview
//@Composable
//fun PreviewFavScreen() {
//    FavScreen(
//        articles = dummynyMovies,
//        movies = dummymovies,
//        series = dummyseries,
//        showArticles = true,
//        shouldShowArticles = {},
//        showMovies = true,
//        shouldShowMovies = {},
//        showSeries = true,
//        shouldShowSeries = {},
//        onFavClick = {}
//    )
//
//}
//
//val dummynyMovies = listOf(
//    Favorite(
//        1,
//        "Movie title",
//        "Movie summary",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "movie",
//        isFavorite = false
//    )
//)
//val dummymovies = listOf(
//    Favorite(
//        3,
//        "Title Of movie",
//        "Summary Of movie, just to see how it looks when long like extra extra long",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "movie",
//        isFavorite = false
//    ),
//    Favorite(
//        4,
//        "Title Of movie, just to see how",
//        "Summary Of movie, just to see how it looks when long like extra extra long",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "movie",
//        isFavorite = false
//    )
//)
//val dummyseries = listOf(
//    Favorite(
//        5,
//        "Title Of movie, just to see how it looks when long like extra extra long",
//        "Summary Of movie, just to see how it looks when long like extra extra long",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "movie",
//        isFavorite = false
//    ),
//    Favorite(
//        5,
//        "Title Of movie, just to see",
//        "Summary Of movie, just to see how it",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "movie",
//        isFavorite = false
//    )
//)