package com.rick.screen_book.bestseller_screen

sealed class BestsellerEvents {
    data class SelectedGenre(val bookGenre: BookGenre): BestsellerEvents()
}
