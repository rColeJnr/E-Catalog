package com.rick.ecatalogs.book.screen_book.bestseller_catalog

import androidx.annotation.StringRes
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre

/** The NYT lists the catalog offers, with their chip labels. */
enum class BookGenre(val genre: BestsellerGenre, @StringRes val labelRes: Int) {
    FICTION(BestsellerGenre.FICTION, R.string.book_screen_book_bestseller_catalog_genre_fiction),
    NONFICTION(BestsellerGenre.NONFICTION, R.string.book_screen_book_bestseller_catalog_genre_nonfiction),
    MISCELLANEOUS(BestsellerGenre.MISCELLANEOUS, R.string.book_screen_book_bestseller_catalog_genre_miscellaneous),
    BUSINESS(BestsellerGenre.BUSINESS, R.string.book_screen_book_bestseller_catalog_genre_business),
    GRAPHIC(BestsellerGenre.GRAPHIC, R.string.book_screen_book_bestseller_catalog_genre_graphic);

    val listName: String get() = genre.listName

    companion object {
        val DEFAULT = FICTION

        fun fromListName(listName: String?): BookGenre? =
            entries.firstOrNull { it.listName == listName }
    }
}
