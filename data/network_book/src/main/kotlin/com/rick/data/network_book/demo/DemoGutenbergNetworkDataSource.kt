package com.rick.data.network_book.demo

import JvmUnitTestBookDemoAssetManager
import com.rick.data.network_book.GutenbergNetworkDataSource
import com.rick.data.network_book.model.GutenbergResponse
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
    override suspend fun searchBook(query: String): GutenbergResponse =
        withContext(IO) {
            assets.open(SEARCH_GUTENBERG).use(networkJson::decodeFromStream)
        }

    companion object {
        private const val GUTENBERG_ASSET = "gutenberg.json"
        private const val SEARCH_GUTENBERG = "search_gutenberg.json"
    }
}