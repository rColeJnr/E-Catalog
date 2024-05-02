package com.rick.book.data_book.network.demo

import JvmUnitTestBookDemoAssetManager
import com.rick.book.data_book.network.GutenbergNetworkDataSource
import com.rick.book.data_book.network.model.GutenbergResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoGutenbergNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoBookAssetManager = JvmUnitTestBookDemoAssetManager,
) : GutenbergNetworkDataSource {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchBooks(page: Int): GutenbergResponse =
        withContext(IO) {
            assets.open(GUTENBERG_ASSET).use(networkJson::decodeFromStream)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchGutenberg(query: String): GutenbergResponse =
        withContext(IO) {
            assets.open(SEARCH_GUTENBERG).use(networkJson::decodeFromStream)
        }

    companion object {
        private const val GUTENBERG_ASSET = "gutenberg.json"
        private const val SEARCH_GUTENBERG = "searchgutenberg.json"
    }
}