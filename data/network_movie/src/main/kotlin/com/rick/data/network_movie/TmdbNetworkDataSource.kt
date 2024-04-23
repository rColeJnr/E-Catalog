package com.rick.data.network_movie

import com.rick.data.model_movie.tmdb.movie.MovieResponse
import com.rick.data.model_movie.tmdb.search.SearchResponse
import com.rick.data.model_movie.tmdb.series.SeriesResponse
import com.rick.data.network_movie.model.TrendingMovieResponse
import com.rick.data.network_movie.model.TrendingSeriesResponse

interface TmdbNetworkDataSource {


    suspend fun fetchTrendingMovies(
        page: Int,
        language: String = "en-US",
        apikey: String
    ): TrendingMovieResponse

    suspend fun fetchTrendingSeries(
        page: Int,
        language: String = "en-US",
        apikey: String
    ): TrendingSeriesResponse

    suspend fun fetchSearch(
        query: String,
        includeAdult: Boolean = true,
        language: String = "en-US",
        page: Int,
        apikey: String
    ): SearchResponse

    suspend fun fetchSeries(
        id: Int,
        appendResponse: String = "images,similar",
        language: String = "en-US",
        apikey: String
    ): SeriesResponse

    suspend fun fetchMovie(
        id: Int,
        appendResponse: String = "images,similar",
        language: String = "en-US",
        apikey: String
    ): MovieResponse
}