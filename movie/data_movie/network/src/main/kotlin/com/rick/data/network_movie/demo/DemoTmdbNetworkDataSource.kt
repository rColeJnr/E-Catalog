package com.rick.data.network_movie.demo

import JvmUnitTestMovieDemoAssetManager
import com.rick.data.network_movie.TmdbNetworkDataSource
import com.rick.data.network_movie.model.MovieResponse
import com.rick.data.network_movie.model.SeriesResponse
import com.rick.data.network_movie.model.TrendingMovieResponse
import com.rick.data.network_movie.model.TrendingSeriesResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import javax.inject.Inject

class DemoTmdbNetworkDataSource @Inject constructor(
    private val networkJson: Json,
    private val assets: DemoMovieAssetManager = JvmUnitTestMovieDemoAssetManager,
) : TmdbNetworkDataSource {
    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchTrendingMovies(
        page: Int,
        language: String,
        apikey: String
    ): TrendingMovieResponse =
        withContext(IO) {
            assets.open(TRENDING_MOVIE_ASSET).use(networkJson::decodeFromStream)
        }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchTrendingSeries(
        page: Int,
        language: String,
        apikey: String
    ): TrendingSeriesResponse = withContext(IO) {
        assets.open(TRENDING_SERIES_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchSeries(
        id: Int,
        appendResponse: String,
        language: String,
        apikey: String,
    ): SeriesResponse = withContext(IO) {
        assets.open(SERIES_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun fetchMovie(
        id: Int,
        appendResponse: String,
        language: String,
        apikey: String,
    ): MovieResponse = withContext(IO) {
        assets.open(MOVIE_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchSeries(
        query: String,
        include_adult: Boolean,
        language: String,
        apikey: String
    ): TrendingSeriesResponse = withContext(IO) {
        assets.open(SEARCH_SERIES_ASSET).use(networkJson::decodeFromStream)
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun searchMovie(
        query: String,
        include_adult: Boolean,
        language: String,
        apikey: String
    ): TrendingMovieResponse = withContext(IO) {
        assets.open(SEARCH_MOVIE_ASSET).use(networkJson::decodeFromStream)
    }

    companion object {
        private const val TRENDING_MOVIE_ASSET = "trendingmovie.json"
        private const val TRENDING_SERIES_ASSET = "trendingseries.json"
        private const val MOVIE_ASSET = "movie_details.json"
        private const val SERIES_ASSET = "series_details.json"
        private const val SEARCH_SERIES_ASSET = "search_series.json"
        private const val SEARCH_MOVIE_ASSET = "searchMovie"
    }
}