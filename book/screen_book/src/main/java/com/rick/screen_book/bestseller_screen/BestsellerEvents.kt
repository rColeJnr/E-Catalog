package com.rick.screen_book.bestseller_screen

import com.rick.data_book.nytimes.model.NYBook

sealed class BestsellerEvents {

    data class SelectedGenre(val bookGenre: Int): BestsellerEvents()

    data class OnFavoriteClick(val book: NYBook): BestsellerEvents()

    object OnRemoveFavorite : BestsellerEvents()

}
