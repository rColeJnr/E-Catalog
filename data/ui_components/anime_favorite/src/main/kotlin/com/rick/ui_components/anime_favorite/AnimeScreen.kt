package com.rick.ui_components.anime_favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.rick.data.model_anime.FavoriteUiState
import com.rick.data.ui_components.anime_favorite.R
import com.rick.data.ui_components.common.EcsAnimatedVisibilityBox
import com.rick.data.ui_components.common.EcsCatalogCard
import com.rick.data.ui_components.common.EcsEmptyState
import com.rick.data.ui_components.common.EcsSectionRow

@Composable
fun AnimeFavScreen(
    animeState: FavoriteUiState,
    mangaState: FavoriteUiState,
    onAnimeFavClick: (Int) -> Unit,
    onMangaFavClick: (Int) -> Unit,
    shouldShowAnime: (Boolean) -> Unit,
    showAnime: Boolean,
    shouldShowManga: (Boolean) -> Unit,
    showManga: Boolean,
    shouldDisplayAnimeUndoFavorite: Boolean,
    shouldDisplayMangaUndoFavorite: Boolean,
    undoAnimeFavoriteRemoval: () -> Unit,
    undoMangaFavoriteRemoval: () -> Unit,
    clearUndoState: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = shouldDisplayAnimeUndoFavorite, key2 = shouldDisplayMangaUndoFavorite) {
        if (shouldDisplayAnimeUndoFavorite) {
            val snackBarResult = snackbarHostState.showSnackbar(
                message = "Anime Favorite removed",
                actionLabel = "Undo"
            ) == SnackbarResult.ActionPerformed
            if (snackBarResult) {
                undoAnimeFavoriteRemoval()
            } else {
                clearUndoState()
            }
        }

        if (shouldDisplayMangaUndoFavorite) {
            val snackBarResult = snackbarHostState.showSnackbar(
                message = "Manga Favorite removed",
                actionLabel = "Undo"
            ) == SnackbarResult.ActionPerformed
            if (snackBarResult) {
                undoMangaFavoriteRemoval()
            } else {
                clearUndoState()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
                .fillMaxSize()
                .background(colorResource(id = R.color.background).copy(alpha = 0.8f))
        ) {
            EcsSectionRow("Anime", showAnime, shouldShowAnime)
            EcsAnimatedVisibilityBox(
                screenState = showAnime,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (animeState) {
                    is FavoriteUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is FavoriteUiState.AnimeFavorites -> {
                        if (animeState.favorites.isEmpty()) {
                            EcsEmptyState()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(it)
                                    .wrapContentHeight()
                            ) {
                                items(animeState.favorites) { jikan ->
                                    EcsCatalogCard(
                                        itemTitle = jikan.title,
                                        itemId = jikan.id.toString(),
                                        itemImage = jikan.background,
                                        itemSummary = jikan.synopsis,
                                        onFavClick = { Int }
                                    )
                                }
                            }
                        }
                    }

                    is FavoriteUiState.MangaFavorites -> {/* Do nothing */
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            EcsSectionRow(text = "Manga", isVisible = showManga, shouldShowManga)
            EcsAnimatedVisibilityBox(
                screenState = showManga,
                density = density,
                modifier = Modifier.fillMaxWidth()
            ) {
                when (mangaState) {
                    is FavoriteUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is FavoriteUiState.MangaFavorites -> {
                        if (mangaState.favorites.isEmpty()) {
                            EcsEmptyState()
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .padding(it)
                                    .wrapContentHeight()
                            ) {
                                items(mangaState.favorites) { jikan ->
                                    EcsCatalogCard(
                                        itemTitle = jikan.title,
                                        itemId = jikan.id.toString(),
                                        itemImage = jikan.background,
                                        itemSummary = jikan.synopsis,
                                        onFavClick = { Int }
                                    )
                                }
                            }
                        }
                    }

                    is FavoriteUiState.AnimeFavorites -> {/* Do nothing */
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun PreviewAnimeItem() {
//    AnimeItem(
//        jikan = UserAnime(
//            0,
//            "Long a bit tilte here",
//            "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//            "Synopis, a little bit of bla bla bla balblabla  text ext and more text a bit",
//            "PG-13",
//            isFavorite = true
//        ), onFavClick = {})
//}

//@Preview
//@Composable
//fun JikanItemPrev() {
//    FavScreen(
//        anime = dummyData,
////        listOf(
////            JikanFavorite(
////                3,
////                "Dorathans sword",
////                "",
////                "THIs is a made up title by meself",
////                "Rated 18+",
////                "Typing types"
////            )
////        ),
//        manga = dummyData, {},
//        shouldShowManga = {},
//        showManga = true,
//        shouldShowAnime = {},
//        showAnime = true
//    )
//}

//private val dummyData = listOf(
//    JikanFavorite(
//        0,
//        "One Piece",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "Synopis, a little bit of bla bla bla balblabla  text ext and more text",
//        "PG-13",
//        false
//    ),
//    JikanFavorite(
//        1,
//        "One punch man",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "Synopis, a little bit of bla bla bla balblabla  text ext and more text",
//        "PG-13",
//        true
//    ),
//    JikanFavorite(
//        2,
//        "Hakuna Matata",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
//        "Synopis, a little bit of bla bla bla balblabla  text ext and more text",
//        "PG-13",
//        false
//    ),
//)