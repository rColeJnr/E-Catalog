package com.rick.data.network_book.demo

import JvmUnitTestBookDemoAssetManager
import com.rick.data.network_book.BestsellerNetworkDataSource
import com.rick.data.network_book.model.BestsellerResponse
import com.rick.data.network_book.model.SearchBestsellerResponse
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
        apiKey: String
    ): BestsellerResponse =
        withContext(IO) {
            assets.open(BESTSELLER_ASSET).use(networkJson::decodeFromStream)
        }

    override suspend fun searchBestseller(query: String, apiKey: String): SearchBestsellerResponse {
        TODO("Not yet implemented")
    }
//    @OptIn(ExperimentalSerializationApi::class)
//    override suspend fun searchBook(query: String): GutenbergResponse =
//        withContext(IO) {
//            assets.open(SEARCH_GUTENBERG).use(networkJson::decodeFromStream)
//        }

    companion object {
        private const val BESTSELLER_ASSET = "bestseller.json"
        private const val SEARCH_GUTENBERG = "searchgutenberg.json"
    }
}