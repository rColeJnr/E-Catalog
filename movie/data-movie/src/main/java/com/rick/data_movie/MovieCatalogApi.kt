package com.rick.data_movie

import retrofit2.http.GET
import retrofit2.http.Query

interface MovieCatalogApi {

    @GET("svc/movies/v2/reviews/all.json?")
    suspend fun fetchMovieCatalog(
        @Query("offset") offset: Int,
        @Query("api-key") apikey: String = "r2hGllYW5TVGnqspbO8u3li1Un4AlQgQ"
    ): MovieCatalogDto

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
    }

}