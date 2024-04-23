package com.rick.data.network_movie.demo

import JvmUnitTestMovieDemoAssetManager
import com.rick.data.network_movie.ArticleNetworkDataSource
import com.rick.data.network_movie.model.ArticleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoNyTimesNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoMovieAssetManager = JvmUnitTestMovieDemoAssetManager
) : ArticleNetworkDataSource {
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchMovieArticles(
        section_name: String, sort: String, page: Int, apikey: String
    ): ArticleResponse = withContext(Dispatchers.IO) {
        assets.open(ARTICLES_ASSET).use(networkJson::decodeFromStream)
    }

    companion object {
        private const val ARTICLES_ASSET = "article.json"
    }
}