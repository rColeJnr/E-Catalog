package com.rick.movie.data_movie.network.demo

import JvmUnitTestMovieDemoAssetManager
import com.rick.movie.data_movie.network.ArticleNetworkDataSource
import com.rick.movie.data_movie.network.model.ArticleResponse
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
        page: Int, apikey: String, section_name: String, sort: String
    ): ArticleResponse = withContext(Dispatchers.IO) {
        assets.open(ARTICLE_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchMovieArticles(
        apiKey: String, query: String, sort: String, section_name: String
    ): ArticleResponse = withContext(Dispatchers.IO) {
        assets.open(SEARCH_ARTICLE_ASSET).use(networkJson::decodeFromStream)
    }

    companion object {
        private const val ARTICLE_ASSET = "article.json"
        private const val SEARCH_ARTICLE_ASSET = "search_movie_article.json"
    }
}