package com.rick.ecatalogs.book.data_book.model

sealed class FavoriteUiEvents {
    data class RemoveGutenbergFavorite(val bookId: Int) : FavoriteUiEvents()
    data class RemoveBestsellerFavorite(val bookId: String) : FavoriteUiEvents()
    data class ShouldShowBestsellers(val show: Boolean) : FavoriteUiEvents()
    data class ShouldShowGutenberg(val show: Boolean) : FavoriteUiEvents()
    data object UndoGutenbergFavoriteRemoval : FavoriteUiEvents()
    data object UndoBestsellerFavoriteRemoval : FavoriteUiEvents()
    data object ClearUndoState : FavoriteUiEvents()
}
