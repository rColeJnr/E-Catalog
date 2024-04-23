package com.rick.ui_components.book_favorite

import androidx.compose.foundation.layout.fillMaxWidth
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
import com.rick.data.model_book.FavoriteUiState
import com.rick.ui_components.common.EcsAnimatedVisibilityBox
import com.rick.ui_components.common.EcsBookCatalogCard
import com.rick.ui_components.common.EcsEmptyState
import com.rick.ui_components.common.EcsScaffold
import com.rick.ui_components.common.EcsSectionRow
import com.rick.ui_components.common.EcsSnackbar

@Composable
fun BookFavScreen(
    gutenbergState: FavoriteUiState,
    bestsellerState: FavoriteUiState,
    onGutenbergFavClick: (String) -> Unit,
    onBestsellerFavClick: (String) -> Unit,
    showBestsellers: Boolean,
    shouldShowBestsellers: (Boolean) -> Unit,
    showGutenberg: Boolean,
    shouldShowGutenberg: (Boolean) -> Unit,
    shouldDisplayGutenbergUndoFavorite: Boolean,
    shouldDisplayBestsellerUndoFavorite: Boolean,
    undoGutenbergFavoriteRemoval: () -> Unit,
    undoBestsellerFavoriteRemoval: () -> Unit,
    clearUndoState: () -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(
        key1 = shouldDisplayBestsellerUndoFavorite,
        key2 = shouldDisplayGutenbergUndoFavorite
    ) {
        if (shouldDisplayBestsellerUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = "Bestseller Favorite removed",
                undoRemoval = undoBestsellerFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }

        if (shouldDisplayGutenbergUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = "Manga Favorite removed",
                undoRemoval = undoGutenbergFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }
    }

    val density = LocalDensity.current
    EcsScaffold(
        snackbarHostState = snackbarHostState
    ) {
        EcsSectionRow(
            text = "Project Gutenberg",
            isVisible = showGutenberg,
            onClick = shouldShowGutenberg
        )
        EcsAnimatedVisibilityBox(
            screenState = showGutenberg,
            density = density,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (gutenbergState) {
                FavoriteUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }

                is FavoriteUiState.GutenbergFavorites -> {
                    if (gutenbergState.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn {
                            items(gutenbergState.favorites) { book ->
                                EcsBookCatalogCard(
                                    image = book.formats.imageJpeg!!,
                                    title = book.title,
                                    author = book.authors.first().name,
                                    id = book.id.toString(),
                                    onFavClick = onGutenbergFavClick
                                )
                            }
                        }
                    }
                }

                is FavoriteUiState.BestsellerFavorites -> {/* Do nothing */
                }
            }
        }
        EcsSectionRow(
            text = "NY Times Bestsellers",
            isVisible = showBestsellers,
            onClick = shouldShowBestsellers
        )
        EcsAnimatedVisibilityBox(
            screenState = showBestsellers,
            density = density,
            modifier = Modifier.fillMaxWidth()
        ) {
            when (bestsellerState) {
                FavoriteUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.wrapContentSize())
                }

                is FavoriteUiState.BestsellerFavorites -> {
                    if (bestsellerState.favorites.isEmpty()) {
                        EcsEmptyState()
                    } else {
                        LazyColumn {
                            items(bestsellerState.favorites) { book ->
                                EcsBookCatalogCard(
                                    image = book.image,
                                    title = book.title,
                                    author = book.author,
                                    id = book.id,
                                    onFavClick = onBestsellerFavClick
                                )
                            }
                        }
                    }
                }

                is FavoriteUiState.GutenbergFavorites -> {/* Do nothing */
                }
            }
        }

    }
}

private const val TAG = "BookScreen"

//@Preview
//@Composable
//fun FavScreenPrev() {
//    MaterialTheme {
//        BookFavScreen(
//            favorites = dummyBooks
//        )
//    }
//}
//
//val dummyBooks = listOf(
//    Favorite(
//        1,
//        "Book title",
//        "Book author",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
////        type = "book",
//        isFavorite = true
//    ),
//    Favorite(
//        1,
//        "Book title",
//        "Book author",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
////        type = "book",
//        isFavorite = true
//    ),
//    Favorite(
//        1,
//        "Book title",
//        "Book author and author book and assistance",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
////        type = "book",
//        isFavorite = true
//    ),
//    Favorite(
//        1,
//        "Book title, why no long title example are you afradid of long titles",
//        "Book author",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
////        type = "book",
//        isFavorite = true
//    ),
//    Favorite(
//        1,
//        "Book title mid long titlle, gotta check erthing",
//        "Book author and author book",
//        "https://www.nixsolutions.com/uploads/2020/07/Golang-700x395.png",
////        type = "book",
//        isFavorite = true
//    ),
//)