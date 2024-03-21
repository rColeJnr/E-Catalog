package com.rick.screen_book.bestseller_screen

enum class BookGenre(val listName: String) {
    FICTION("Hardcover Fiction"),
    NONFICTION("Hardcover Nonfiction"),
    MISCELLANEOUS("Advice How-To and Miscellaneous"),
    BUSINESS("Business Books"),
    GRAPHIC("Graphic Books and Manga");

    companion object {
        val DEFAULT = FICTION
    }
}