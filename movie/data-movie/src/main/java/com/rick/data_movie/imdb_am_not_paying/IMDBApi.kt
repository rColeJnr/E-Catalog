package com.rick.data_movie.imdb_am_not_paying

import com.rick.data_movie.imdb_am_not_paying.movie_model.IMDBMovieDto
import com.rick.data_movie.imdb_am_not_paying.search_model.IMDBSearchResponse
import com.rick.data_movie.imdb_am_not_paying.series_model.TvSeriesResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface IMDBApi {
    @GET("en/API/SearchMovie")
    suspend fun searchMovies(
        @Query("apiKey") apiKey: String,
        @Query("expression") title: String
    ): IMDBSearchResponse

    @GET("en/API/SearchSeries")
    suspend fun searchSeries(
        @Query("apiKey") apiKey: String,
        @Query("expression") title: String
    ): IMDBSearchResponse

    @GET("en/API/Title")
    suspend fun getMovieOrSeries(
        @Query("apiKey") apiKey: String,
        @Query("id") id: String,
        @Query("options") options: String = OPTIONS
    ): IMDBMovieDto

    @GET("en/API/MostPopularTVs")
    suspend fun getPopularTvSeries(
        @Query("apikey") apiKey: String
    ) : TvSeriesResponseDto

    companion object {
        const val IMDB_BASE_URL = "https://imdb-api.com/"
        private const val OPTIONS = "Images,Ratings,"
    }
}