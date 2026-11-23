package com.rick.ecatalogs.data.ui_components.anime_favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeOut
import androidx.compose.animation.fadeIn
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import com.rick.ecatalogs.anime.data_anime.model.FavoriteUiState
import com.rick.ecatalogs.data.ui_components.anime_favorite.R
import com.rick.ecatalogs.data.ui_components.common.EcsCatalogCard
import com.rick.ecatalogs.data.ui_components.common.EcsEmptyState
import com.rick.ecatalogs.data.ui_components.common.EcsScaffold

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

    val backgroundGradient = linearGradient(
        colors = listOf(
            colorResource(com.rick.ecatalogs.data.ui_design.R.color.data_ui_design_color_surface), // end color
            colorResource(com.rick.ecatalogs.data.ui_design.R.color.data_ui_design_color_on_surface), // start color
        ),
        start = Offset.Infinite,
        end = Offset.Zero
    )

    EcsScaffold (
        modifier = Modifier
            .fillMaxSize(),
        snackbarHostState = snackbarHostState,
    ) {
        val density = LocalDensity.current
        Column(
            modifier = Modifier
                .scrollable(state = rememberScrollState(), orientation = Orientation.Vertical)
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            AnimatedContent(
                targetState = state,
                contentKey = { it::class },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "favoritesState",
            ) { state ->
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
                                    .fillMaxSize()
                            ) {
                                items(state.favorites, key = { it.id }) { anime ->
                                    // Animates position changes and removals (e.g. un-favoriting)
                                    Box(Modifier.animateItem()) {
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
                    }

                    is FavoriteUiState.MangaFavorites -> {
                        if (state.favorites.isEmpty()) {
                            EcsEmptyState()
                        } else {
                            LazyColumn() {
                                items(state.favorites, key = { it.id }) { manga ->
                                    // Animates position changes and removals (e.g. un-favoriting)
                                    Box(Modifier.animateItem()) {
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