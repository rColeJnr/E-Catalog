package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.network.model.BookReviewNetwork
import com.rick.ecatalogs.book.data_book.network.model.ReviewArticleNetwork
import com.rick.ecatalogs.book.data_book.network.model.ReviewByline
import com.rick.ecatalogs.book.data_book.network.model.ReviewHeadline
import com.rick.ecatalogs.book.data_book.network.model.ReviewImage
import com.rick.ecatalogs.book.data_book.network.model.ReviewMultimedia
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BestsellerPagingAndReviewTest {

    @Test
    fun usualListOfFifteen_endsOnFirstPage() {
        assertTrue(bestsellerEndReached(offset = 0, pageSize = 15, totalResults = 15))
    }

    @Test
    fun longerList_continuesThenEnds() {
        assertFalse(bestsellerEndReached(offset = 0, pageSize = 20, totalResults = 35))
        assertTrue(bestsellerEndReached(offset = 20, pageSize = 15, totalResults = 35))
    }

    @Test
    fun emptyPage_endsTheList() {
        assertTrue(bestsellerEndReached(offset = 20, pageSize = 0, totalResults = 40))
    }

    @Test
    fun remoteKeys_arePerList() {
        assertEquals("business-books|978", bestsellerRemoteKeyId("business-books", "978"))
    }

    @Test
    fun review_usesTheArticleWhenAvailable() {
        val review = asBookReview(
            listing = BookReviewNetwork(
                url = "https://www.nytimes.com/2025/01/07/books/review/let-them.html",
                byline = "JENNIFER SZALAI",
                bookTitle = "The Let Them Theory",
                summary = "",
                publicationDate = "2025-01-07",
            ),
            article = ReviewArticleNetwork(
                headline = ReviewHeadline("Mel Robbins Wants You to Let Them"),
                abstract = "Letting go, she argues, is how you get what you want.",
                leadParagraph = "The first lines of the review.",
                byline = ReviewByline("By Jennifer Szalai"),
                multimedia = ReviewMultimedia(ReviewImage("https://static01.nyt.com/x.jpg")),
            ),
            url = "https://www.nytimes.com/2025/01/07/books/review/let-them.html",
        )

        assertEquals("Mel Robbins Wants You to Let Them", review.headline)
        assertEquals("By Jennifer Szalai", review.reviewer)
        assertEquals("2025-01-07", review.publishedDate)
        assertEquals("Letting go, she argues, is how you get what you want.", review.summary)
        assertEquals("The first lines of the review.", review.excerpt)
        assertEquals("https://static01.nyt.com/x.jpg", review.imageUrl)
    }

    @Test
    fun review_fallsBackToTheListingWithoutTheArticle() {
        val review = asBookReview(
            listing = BookReviewNetwork(
                url = "https://www.nytimes.com/review.html",
                byline = "By Someone",
                bookTitle = "A Book",
                summary = "A short verdict.",
                publicationDate = "2024-05-02",
            ),
            article = null,
            url = "https://www.nytimes.com/review.html",
        )

        assertEquals("A Book", review.headline)
        assertEquals("A short verdict.", review.summary)
        assertNull(review.excerpt)
        assertNull(review.imageUrl)
    }

    @Test
    fun review_doesNotRepeatTheSummaryAsExcerpt() {
        val review = asBookReview(
            listing = BookReviewNetwork(url = "u"),
            article = ReviewArticleNetwork(abstract = "Same text", snippet = "Same text"),
            url = "u",
        )
        assertEquals("Same text", review.summary)
        assertNull(review.excerpt)
    }
}
