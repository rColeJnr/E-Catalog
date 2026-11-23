package com.rick.ecatalogs.book.data_book.model.gutenberg

data class Gutenberg(
    val id: Int,
    val title: String,
    val authors: List<Author>,
    val bookshelves: List<String>,
    val downloads: Int,
    val formats: Formats,
    val mediaType: String,
)
