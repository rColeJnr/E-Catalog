package com.rick.ui_components.movie_favorite

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.rick.data.model_movie.FavoriteUiState
import com.rick.ui_components.common.EcsAnimatedVisibilityBox
import com.rick.ui_components.common.EcsCatalogCard
import com.rick.ui_components.common.EcsEmptyState
import com.rick.ui_components.common.EcsScaffold
import com.rick.ui_components.common.EcsSectionRow
import com.rick.ui_components.common.EcsSnackbar

@Composable
fun MovieFavScreen(
    articlesState: FavoriteUiState,
    moviesState: FavoriteUiState,
    seriesState: FavoriteUiState,
    showArticles: Boolean,
    shouldShowArticles: (Boolean) -> Unit,
    showMovies: Boolean,
    shouldShowMovies: (Boolean) -> Unit,
    showSeries: Boolean,
    shouldShowSeries: (Boolean) -> Unit,
    onArticleFavClick: (String) -> Unit,
    onMovieFavClick: (String) -> Unit,
    onSeriesFavClick: (String) -> Unit,
    shouldDisplaySeriesUndoFavorite: Boolean,
    shouldDisplayMovieUndoFavorite: Boolean,
    shouldDisplayArticleUndoFavorite: Boolean,
    undoSeriesFavoriteRemoval: () -> Unit,
    undoMovieFavoriteRemoval: () -> Unit,
    undoArticleFavoriteRemoval: () -> Unit,
    clearUndoState: () -> Unit,
) {

    val density = LocalDensity.current
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(
        key1 = shouldDisplayMovieUndoFavorite,
        key2 = shouldDisplaySeriesUndoFavorite,
        key3 = shouldDisplayArticleUndoFavorite
    ) {
        if (shouldDisplayArticleUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = "Article Favorite removed",
                undoRemoval = undoArticleFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }

        if (shouldDisplayMovieUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = "Movie Favorite removed",
                undoRemoval = undoMovieFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }

        if (shouldDisplaySeriesUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = "Series Favorite removed",
                undoRemoval = undoSeriesFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }
    }

    EcsScaffold(
        snackbarHostState = snackbarHostState
    ) {
        EcsSectionRow("Ny articles", showArticles, shouldShowArticles)
        EcsAnimatedVisibilityBox(
            screenState = showArticles,
            density = density,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (articlesState) {
                FavoriteUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }

                is FavoriteUiState.ArticlesFavorites -> {
                    if (articlesState.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {
                            items(articlesState.favorites) { article ->
                                EcsCatalogCard(
                                    itemTitle = article.headline.main ?: article.abstract,
                                    itemSummary = article.leadParagraph,
                                    itemImage = article.multimedia.first().url,
                                    itemId = article.id.toString(),
                                    onFavClick = onArticleFavClick
                                )
                            }
                        }
                    }
                }

                else -> {}
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        EcsSectionRow(text = "Movies", isVisible = showMovies, shouldShowMovies)
        EcsAnimatedVisibilityBox(
            screenState = showMovies,
            density = density,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (moviesState) {
                FavoriteUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }

                is FavoriteUiState.TrendingMoviesFavorites -> {
                    if (moviesState.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {
                            items(moviesState.favorites) { movie ->
                                EcsCatalogCard(
                                    itemTitle = movie.title,
                                    itemSummary = movie.overview,
                                    itemImage = movie.image,
                                    itemId = movie.id.toString(),
                                    onFavClick = onMovieFavClick
                                )
                            }
                        }
                    }
                }

                is FavoriteUiState.ArticlesFavorites -> {
                    /* do nothing */
                }

                is FavoriteUiState.TrendingSeriesFavorites -> {
                    /* do nothing */
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        EcsSectionRow(text = "Trending Series", isVisible = showSeries, shouldShowSeries)
        EcsAnimatedVisibilityBox(
            screenState = showSeries,
            density = density,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (seriesState) {
                FavoriteUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }

                is FavoriteUiState.TrendingSeriesFavorites -> {
                    if (seriesState.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .wrapContentHeight()
                        ) {
                            items(seriesState.favorites) { series ->
                                EcsCatalogCard(
                                    itemTitle = series.name,
                                    itemSummary = series.overview,
                                    itemImage = series.image,
                                    itemId = series.id.toString(),
                                    onFavClick = onSeriesFavClick
                                )
                            }
                        }
                    }
                }

                is FavoriteUiState.ArticlesFavorites -> {
                    /* do nothing */
                }

                is FavoriteUiState.TrendingMoviesFavorites -> {
                    /* do nothing */
                }
            }
        }
    }

}
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