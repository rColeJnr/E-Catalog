package com.rick.ecatalogs.book.data_book.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** NYT Books `reviews.json?isbn=`: the NYT's reviews of a book (usually none or one). */
@Serializable
data class BookReviewsResponse(
    val results: List<BookReviewNetwork>? = null,
)

@Serializable
data class BookReviewNetwork(
    /** The review article on nytimes.com. */
    val url: String? = null,
    val byline: String? = null,
    @SerialName("book_title")
    @SerializedName("book_title")
    val bookTitle: String? = null,
    val summary: String? = null,
    /** yyyy-MM-dd */
    @SerialName("publication_dt")
    @SerializedName("publication_dt")
    val publicationDate: String? = null,
)

/** NYT Article Search, used to fetch the review article itself (headline, summary, photo). */
@Serializable
data class ReviewArticleResponse(
    val response: ReviewArticleDocs? = null,
)

@Serializable
data class ReviewArticleDocs(
    val docs: List<ReviewArticleNetwork>? = null,
)

@Serializable
data class ReviewArticleNetwork(
    val headline: ReviewHeadline? = null,
    val abstract: String? = null,
    val snippet: String? = null,
    @SerialName("lead_paragraph")
    @SerializedName("lead_paragraph")
    val leadParagraph: String? = null,
    val byline: ReviewByline? = null,
    @SerialName("pub_date")
    @SerializedName("pub_date")
    val pubDate: String? = null,
    val multimedia: ReviewMultimedia? = null,
)

@Serializable
data class ReviewHeadline(val main: String? = null)

@Serializable
data class ReviewByline(val original: String? = null)

@Serializable
data class ReviewMultimedia(
    @SerialName("default")
    @SerializedName("default")
    val default: ReviewImage? = null,
)

@Serializable
data class ReviewImage(val url: String? = null)
