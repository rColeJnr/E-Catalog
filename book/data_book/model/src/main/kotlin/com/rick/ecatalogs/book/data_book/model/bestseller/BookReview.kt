package com.rick.ecatalogs.book.data_book.model.bestseller

/**
 * The New York Times' review of a book. The NYT APIs don't provide a review's full text: this
 * is the review's headline, summary and opening, with [url] to read the whole article.
 */
data class BookReview(
    val headline: String,
    /** e.g. "By Jennifer Szalai". */
    val reviewer: String?,
    /** yyyy-MM-dd, or null when unknown. */
    val publishedDate: String?,
    val summary: String?,
    /** The review's opening lines. */
    val excerpt: String?,
    val imageUrl: String?,
    val url: String,
)
