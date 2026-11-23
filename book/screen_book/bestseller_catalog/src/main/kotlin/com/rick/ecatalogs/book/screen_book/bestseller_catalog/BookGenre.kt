package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre

enum class BookGenre(val genre: BestsellerGenre, val iconRes: Int = -1) {
    FICTION(BestsellerGenre.FICTION, R.drawable.book_screen_book_bestseller_catalog_category_fiction),
    NONFICTION(BestsellerGenre.NONFICTION, R.drawable.book_screen_book_bestseller_catalog_category_nonfiction),
    MISCELLANEOUS(BestsellerGenre.MISCELLANEOUS, R.drawable.book_screen_book_bestseller_catalog_category_miscellaneous),
    BUSINESS(BestsellerGenre.BUSINESS, R.drawable.book_screen_book_bestseller_catalog_category_business),
    GRAPHIC(BestsellerGenre.GRAPHIC, R.drawable.book_screen_book_bestseller_catalog_category_graphic);

    val listName: String get() = genre.listName

    companion object {
        val DEFAULT = FICTION

        fun fromListName(listName: String?): BookGenre? =
            entries.firstOrNull { it.listName == listName }
    }
}
