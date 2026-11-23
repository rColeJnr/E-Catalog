package com.rick.ecatalogs.movie.data_movie.network

import com.rick.ecatalogs.movie.data_movie.network.model.MovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse

interface TmdbNetworkDataSource {


    suspend fun fetchTrendingMovies(
        page: Int,
        language: String = "en-US",
    ): TrendingMovieResponse

    suspend fun fetchTrendingSeries(
        page: Int,
        language: String = "en-US",
    ): TrendingSeriesResponse

    suspend fun fetchSeries(
        id: Int,
        appendResponse: String = "similar",
        language: String = "en-US",
    ): SeriesResponse

    suspend fun fetchMovie(
        id: Int,
        appendResponse: String = "similar",
        language: String = "en-US",
    ): MovieResponse

    suspend fun searchSeries(
        query: String,
        include_adult: Boolean = false,
        language: String = "en-US",
    ): TrendingSeriesResponse

    suspend fun searchMovie(
        query: String,
        include_adult: Boolean = false,
        language: String = "en-US",
    ): TrendingMovieResponse
}
