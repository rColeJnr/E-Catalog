package com.rick.ui_components.book_favorite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.rick.data.model_book.FavoriteUiState
import com.rick.data.model_book.gutenberg.Author
import com.rick.data.ui_components.common.EcsBookCatalogCard
import com.rick.data.ui_components.common.EcsEmptyState
import com.rick.data.ui_components.common.EcsScaffold
import com.rick.data.ui_components.common.EcsSnackbar

@Composable
fun BookFavScreen(
    bestsellerState: FavoriteUiState,
    onBestsellerFavClick: (String) -> Unit,
    shouldDisplayBestsellerUndoFavorite: Boolean,
    undoBestsellerFavoriteRemoval: () -> Unit,
    clearUndoState: () -> Unit,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(
        key1 = shouldDisplayBestsellerUndoFavorite,
    ) {
        if (shouldDisplayBestsellerUndoFavorite) {
            EcsSnackbar(
                snackbarHostState = snackbarHostState,
                message = "Bestseller Favorite removed",
                undoRemoval = undoBestsellerFavoriteRemoval,
                clearUndoState = clearUndoState
            )
        }
    }

    EcsScaffold(
        snackbarHostState = snackbarHostState
    ) {
        when (bestsellerState) {
            FavoriteUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { CircularProgressIndicator(modifier = Modifier.wrapContentSize()) }
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

            is FavoriteUiState.GutenbergFavorites -> {
                if (bestsellerState.favorites.isEmpty()) {
                    EcsEmptyState()
                } else {
                    LazyColumn {
                        items(bestsellerState.favorites) { book ->
                            EcsBookCatalogCard(
                                image = book.formats.imageJpeg ?: "",
                                title = book.title,
                                author = getAuthorsAsString(book.authors),
                                id = book.id.toString(),
                                onFavClick = onBestsellerFavClick
                            )
                        }
                    }
                }
            }
        }

    }
}

fun getAuthorsAsString(authors: List<Author>): String {
    val author = StringBuilder()
    authors.forEach {
        author.append("${it.name}\nbirth year: ${it.birthYear}, death year: ${it.deathYear}")
    }

    return authors.toString().trim()
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