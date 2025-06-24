package com.rick.book.screen_book.bestseller_catalog

enum class BookGenre(val listName: String) {
    FICTION("hardcover-fiction"),
    NONFICTION("hardcover-nonfiction"),
    MISCELLANEOUS("advice-how-to-and-miscellaneous"),
    BUSINESS("business-books"),
    GRAPHIC("graphic-books-and-manga");

    companion object {
        val DEFAULT = FICTION
    }
}