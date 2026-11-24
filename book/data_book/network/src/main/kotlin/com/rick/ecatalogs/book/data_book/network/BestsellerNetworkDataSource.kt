package com.rick.ecatalogs.book.data_book.network

import com.rick.ecatalogs.book.data_book.network.model.BestsellerOverviewResponse
import com.rick.ecatalogs.book.data_book.network.model.BestsellerResponse
import com.rick.ecatalogs.book.data_book.network.model.BookReviewsResponse
import com.rick.ecatalogs.book.data_book.network.model.ReviewArticleResponse
import com.rick.ecatalogs.book.data_book.network.model.SearchBestsellerResponse

interface BestsellerNetworkDataSource {
    /**
     * One page of a bestseller list. [offset] is a multiple of 20; `num_results` in the response
     * is the list's total length (most lists have 10-15 books, so they fit in the first page).
     */
    suspend fun fetchBestsellers(
        date: String,
        bookGenre: String,
        offset: Int = 0,
    ): BestsellerResponse

    /** Top entries of every bestseller list, in a single request (used by background sync). */
    suspend fun fetchOverview(): BestsellerOverviewResponse

    /** The NYT's reviews of the book with this ISBN-13 (often none). */
    suspend fun fetchReviews(isbn: String): BookReviewsResponse

    /** The nytimes.com article at [url] (Article Search), e.g. a book review. */
    suspend fun fetchArticle(url: String): ReviewArticleResponse

    suspend fun searchBestseller(
        query: String,
    ): SearchBestsellerResponse

}
