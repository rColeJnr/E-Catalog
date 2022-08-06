package com.rick.data_movie

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieCatalogApi {

    @GET("svc/movies/v2/reviews/all.json?")
    suspend fun fetchMovieCatalog(
        @Query("offset") offset: Int,
        @Query("order") order_by: String =  QUERY_ORDER,
        @Query("api-key") apikey: String =  API_KEY
    ): MovieCatalogDto

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
    }

}

private const val QUERY_ORDER = "by-publication-date"
private const val API_KEY = "r2hGllYW5TVGnqspbO8u3li1Un4AlQgQ"