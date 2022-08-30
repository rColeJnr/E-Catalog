package com.rick.data_movie.imdb

import retrofit2.http.GET
import retrofit2.http.Query

interface IMDBApi {

    @GET("en/API/SearchMovie")
    suspend fun searchMovie(
        @Query("apiKey") apiKey: String,
        @Query("expression") title : String
    ): IMDBResponse


    @GET("en/API/SearchSeries")
    suspend fun searchSeries(
        @Query("apiKey") apiKey: String,
        @Query("expression") title : String
    ): IMDBResponse

    companion object {
        const val IMDB_BASE_URL = "https://imdb-api.com/"
    }
}