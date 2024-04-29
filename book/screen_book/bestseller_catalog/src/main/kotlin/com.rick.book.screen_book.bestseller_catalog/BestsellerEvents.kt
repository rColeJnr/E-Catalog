package com.rick.book.screen_book.bestseller_catalog

sealed class BestsellerEvents {

    data class SelectedGenre(val bookGenre: Int) : BestsellerEvents()

    data class UpdateBestsellerFavorite(val id: String, val isFavorite: Boolean) :
        BestsellerEvents()
}
