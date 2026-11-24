package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.model.bestseller.BookReview
import com.rick.ecatalogs.book.data_book.network.BestsellerNetworkDataSource
import com.rick.ecatalogs.book.data_book.network.model.BookReviewNetwork
import com.rick.ecatalogs.book.data_book.network.model.ReviewArticleNetwork
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface BookReviewRepository {
    /**
     * The NYT's review of the book with this ISBN-13, or null when the NYT hasn't reviewed it.
     * @throws IOException or retrofit2.HttpException when it can't be loaded.
     */
    suspend fun getReview(isbn: String): BookReview?
}

/**
 * Two NYT requests per book (the review listing, then the article for its headline, summary and
 * photo), so results, including "not reviewed", are kept for the app's lifetime: NYT allows
 * 5 requests a minute. Failures aren't cached, so retrying works.
 */
@Singleton
internal class BookReviewRepositoryImpl @Inject constructor(
    private val network: BestsellerNetworkDataSource,
) : BookReviewRepository {

    private sealed interface Cached {
        data class Found(val review: BookReview) : Cached
        data object NotReviewed : Cached
    }

    private val cache = mutableMapOf<String, Cached>()
    private val mutex = Mutex()

    override suspend fun getReview(isbn: String): BookReview? {
        mutex.withLock { cache[isbn] }?.let { return (it as? Cached.Found)?.review }

        val listing = network.fetchReviews(isbn).results.orEmpty()
            .firstOrNull { !it.url.isNullOrBlank() }
        val review = listing?.let { reviewWithArticle(it) }

        mutex.withLock { cache[isbn] = review?.let(Cached::Found) ?: Cached.NotReviewed }
        return review
    }

    /** Adds the article's headline, summary, opening and photo; falls back to the listing. */
    private suspend fun reviewWithArticle(listing: BookReviewNetwork): BookReview {
        val url = listing.url!!.replaceFirst("http://", "https://")
        val article = try {
            network.fetchArticle(url).response?.docs?.firstOrNull()
        } catch (e: IOException) {
            null // the listing alone is still worth showing
        } catch (e: retrofit2.HttpException) {
            null
        }
        return asBookReview(listing, article, url)
    }
}

internal fun asBookReview(
    listing: BookReviewNetwork,
    article: ReviewArticleNetwork?,
    url: String,
): BookReview {
    val summary = article?.abstract.orBlankToNull() ?: listing.summary.orBlankToNull()
    val excerpt = (article?.leadParagraph.orBlankToNull() ?: article?.snippet.orBlankToNull())
        ?.takeIf { it != summary }
    return BookReview(
        headline = article?.headline?.main.orBlankToNull()
            ?: listing.bookTitle.orBlankToNull()
            ?: "",
        reviewer = (article?.byline?.original ?: listing.byline).orBlankToNull(),
        publishedDate = (listing.publicationDate ?: article?.pubDate)
            ?.take(10)?.takeIf { it.length == 10 },
        summary = summary,
        excerpt = excerpt,
        imageUrl = article?.multimedia?.default?.url.orBlankToNull(),
        url = url,
    )
}

private fun String?.orBlankToNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
