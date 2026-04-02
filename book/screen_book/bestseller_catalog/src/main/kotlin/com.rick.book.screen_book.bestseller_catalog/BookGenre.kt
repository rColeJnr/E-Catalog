package com.rick.book.screen_book.bestseller_catalog

enum class BookGenre(val listName: String, val iconRes: Int = -1) {
    FICTION("combined-print-and-e-book-fiction", R.drawable.book_screen_book_bestseller_catalog_category_fiction),
    NONFICTION("combined-print-and-e-book-nonfiction", R.drawable.book_screen_book_bestseller_catalog_category_nonfiction),
    MISCELLANEOUS("advice-how-to-and-miscellaneous", R.drawable.book_screen_book_bestseller_catalog_category_miscellaneous),
    BUSINESS("business-books", R.drawable.book_screen_book_bestseller_catalog_category_business),
    GRAPHIC("graphic-books-and-manga", R.drawable.book_screen_book_bestseller_catalog_category_graphic);

    companion object {
        val DEFAULT = FICTION
    }
}