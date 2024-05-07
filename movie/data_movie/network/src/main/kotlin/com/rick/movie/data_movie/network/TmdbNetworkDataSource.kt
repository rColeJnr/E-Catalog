package com.rick.movie.data_movie.network

import com.rick.movie.data_movie.network.model.MovieResponse
import com.rick.movie.data_movie.network.model.SeriesResponse
import com.rick.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.movie.data_movie.network.model.TrendingSeriesResponse

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
        include_adult: Boolean = false,
        language: String = "en-US",
        apikey: String
    ): TrendingSeriesResponse

    suspend fun searchMovie(
        query: String,
        include_adult: Boolean = false,
        language: String = "en-US",
        apikey: String
    ): TrendingMovieResponse
}