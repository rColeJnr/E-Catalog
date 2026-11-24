package com.rick.ecatalogs.book.data_book.model.gutenberg

/**
 * Filters for the Gutenberg list (Gutendex `languages` and `topic`). null = no filter.
 * [language] is an ISO 639-1 code ("en", "pt"…); [topic] matches subjects and bookshelves
 * (e.g. "science fiction", "poetry").
 */
data class GutenbergFilter(
    val language: String? = null,
    val topic: String? = null,
) {
    /** Identifies this filter's list for the list cache. */
    val cacheKey: String get() = "gutenberg:${language ?: "all"}:${topic ?: "all"}"
}
