package com.rick.data.network_movie

import com.rick.data.network_movie.model.MovieResponse
import com.rick.data.network_movie.model.SeriesResponse
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

    suspend fun fetchSeries(
        id: Int,
        appendResponse: String = "similar",
        language: String = "en-US",
        apikey: String,
    ): SeriesResponse

    suspend fun fetchMovie(
        id: Int,
        appendResponse: String = "similar",
        language: String = "en-US",
        apikey: String,
    ): MovieResponse

    suspend fun searchSeries(
        query: String,
        include_adult: Boolean = true,
        language: String = "en-US",
        apikey: String
    ): TrendingSeriesResponse

    suspend fun searchMovie(
        query: String,
        include_adult: Boolean = true,
        language: String = "en-US",
        apikey: String
    ): TrendingMovieResponse
}