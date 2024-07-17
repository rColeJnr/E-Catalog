package com.rick.ui_components.anime_favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import com.rick.data.model_anime.FavoriteUiState
import com.rick.data.ui_components.anime_favorite.R
import com.rick.data.ui_components.common.EcsCatalogCard
import com.rick.data.ui_components.common.EcsEmptyState

@Composable
fun AnimeFavScreen(
    state: FavoriteUiState,
    onFavClick: (Int) -> Unit,
    shouldDisplayUndoFavorite: Boolean,
    undoFavoriteRemoval: () -> Unit,
    clearUndoState: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    LaunchedEffect(key1 = shouldDisplayUndoFavorite) {
        if (shouldDisplayUndoFavorite) {
            val snackBarResult = snackbarHostState.showSnackbar(
                message = context.getString(R.string.data_ui_components_anime_favorite_anime_favorite_removed),
                actionLabel = context.getString(R.string.data_ui_components_anime_favorite_undo)
            ) == SnackbarResult.ActionPerformed
            if (snackBarResult) {
                undoFavoriteRemoval()
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
                .background(
                    colorResource(id = R.color.data_ui_components_anime_favorite_background).copy(
                        alpha = 0.8f
                    )
                )
        ) {
            when (state) {
                is FavoriteUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) { CircularProgressIndicator(modifier = Modifier.wrapContentSize()) }
                }

                is FavoriteUiState.AnimeFavorites -> {
                    if (state.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(it)
                                .wrapContentHeight()
                        ) {
                            items(state.favorites) { anime ->
                                EcsCatalogCard(
                                    itemTitle = anime.title,
                                    itemId = anime.id.toString(),
                                    itemImage = anime.images,
                                    itemSummary = anime.synopsis,
                                    onFavClick = { onFavClick(it.toInt()) }
                                )
                            }
                        }
                    }
                }

                is FavoriteUiState.MangaFavorites -> {
                    if (state.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .padding(it)
                                .wrapContentHeight()
                        ) {
                            items(state.favorites) { manga ->
                                EcsCatalogCard(
                                    itemTitle = manga.title,
                                    itemId = manga.id.toString(),
                                    itemImage = manga.images,
                                    itemSummary = manga.synopsis,
                                    onFavClick = { onFavClick(it.toInt()) }
                                )
                            }
                        }
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