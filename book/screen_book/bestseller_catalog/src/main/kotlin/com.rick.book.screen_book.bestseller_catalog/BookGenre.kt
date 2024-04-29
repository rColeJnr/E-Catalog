package com.rick.book.screen_book.bestseller_catalog

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