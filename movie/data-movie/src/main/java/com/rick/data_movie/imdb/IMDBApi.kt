package com.rick.data_movie.imdb

import retrofit2.http.GET
import retrofit2.http.Query

interface IMDBApi {

    @GET("SearchMovie")
    suspend fun searchMovie(
        @Query("apiKey") apiKey: String,
        @Query("expression") title : String
    ): IMDBResponse

    companion object {
        const val IMDB_BASE_URL = "https://imdb-api.com/en/API/"
    }
}