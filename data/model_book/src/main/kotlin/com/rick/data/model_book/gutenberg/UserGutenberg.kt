package com.rick.data.model_book.gutenberg

data class UserGutenberg internal constructor(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val bookshelves: List<String>,
    val downloads: Int,
    val formats: Formats,
    val mediaType: String,
    val isFavorite: Boolean,
) {
    constructor(book: Gutenberg, userData: GutenbergUserData) : this(
        id = book.id,
        title = book.title,
        authors = book.authors,
        bookshelves = book.bookshelves,
        downloads = book.downloads,
        formats = book.formats,
        mediaType = book.mediaType,
        isFavorite = book.id in userData.gutenbergFavoriteIds
    )
}

fun List<Gutenberg>.mapToUserGutenberg(userData: GutenbergUserData): List<UserGutenberg> =
    map { UserGutenberg(it, userData) }

fun Gutenberg.mapToUserGutenberg(userData: GutenbergUserData): UserGutenberg =
    UserGutenberg(this, userData)