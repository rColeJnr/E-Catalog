package com.rick.ecatalogs.book.data_book.model.bestseller

/** The NYT bestseller lists the app shows. [listName] is the NYT Books API list name. */
enum class BestsellerGenre(val listName: String) {
    FICTION("combined-print-and-e-book-fiction"),
    NONFICTION("combined-print-and-e-book-nonfiction"),
    MISCELLANEOUS("advice-how-to-and-miscellaneous"),
    BUSINESS("business-books"),
    GRAPHIC("graphic-books-and-manga");

    companion object {
        fun fromListName(listName: String?): BestsellerGenre? =
            entries.firstOrNull { it.listName == listName }
    }
}
