package com.rick.ecatalogs.book.data_book.network.demo

import JvmUnitTestBookDemoAssetManager
import com.rick.ecatalogs.book.data_book.network.BestsellerNetworkDataSource
import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerGenre
import com.rick.ecatalogs.book.data_book.network.model.BestsellerOverviewResponse
import com.rick.ecatalogs.book.data_book.network.model.BestsellerResponse
import com.rick.ecatalogs.book.data_book.network.model.BookReviewsResponse
import com.rick.ecatalogs.book.data_book.network.model.ReviewArticleResponse
import com.rick.ecatalogs.book.data_book.network.model.OverviewBook
import com.rick.ecatalogs.book.data_book.network.model.OverviewList
import com.rick.ecatalogs.book.data_book.network.model.OverviewResults
import com.rick.ecatalogs.book.data_book.network.model.SearchBestsellerResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoBestsellerNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoBookAssetManager = JvmUnitTestBookDemoAssetManager,
) : BestsellerNetworkDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchBestsellers(
        date: String,
        bookGenre: String,
        offset: Int,
    ): BestsellerResponse =
        withContext(IO) {
            val list: BestsellerResponse =
                assets.open(BESTSELLER_ASSET).use(networkJson::decodeFromStream)
            // The bundled list is a single page
            if (offset == 0) list else list.copy(results = list.results.copy(books = emptyList()))
        }

    /** Built from the bundled list: every genre shows the demo list's top books. */
    override suspend fun fetchOverview(): BestsellerOverviewResponse {
        val books = fetchBestsellers(date = "current", bookGenre = "").results.books
            .map { OverviewBook(id = it.id, rank = it.rank) }
        return BestsellerOverviewResponse(
            OverviewResults(
                lists = BestsellerGenre.entries.map { OverviewList(it.listName, books.take(5)) },
            ),
        )
    }

    /** No review assets are bundled: demo books show no NYT review. */
    override suspend fun fetchReviews(isbn: String) = BookReviewsResponse(results = emptyList())

    override suspend fun fetchArticle(url: String) = ReviewArticleResponse()

    override suspend fun searchBestseller(query: String): SearchBestsellerResponse {
        TODO("Not yet implemented")
    }
//    @OptIn(ExperimentalSerializationApi::class)
//    override suspend fun searchBook(query: String): GutenbergResponse =
//        withContext(IO) {
//            assets.open(SEARCH_GUTENBERG).use(networkJson::decodeFromStream)
//        }

    companion object {
        private const val BESTSELLER_ASSET = "bestseller_miscellan.json"
        private const val SEARCH_GUTENBERG = "searchgutenberg.json"
    }
}