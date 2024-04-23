package com.rick.screen_book.bestseller_screen

sealed class BestsellerEvents {

    data class SelectedGenre(val bookGenre: Int) : BestsellerEvents()

    data class UpdateBestsellerFavorite(val id: String, val isFavorite: Boolean) :
        BestsellerEvents()
}
