package com.rick.moviecatalog.data

import com.rick.moviecatalog.BuildConfig
import com.rick.moviecatalog.data.remote.MovieCatalogDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieCatalogApi {

    @GET("svc/movies/v2/reviews/all.json?")
    suspend fun fetchMovieCatalog(
        @Query("offset") offset: Int,
        @Query("order") order: String,
        @Query("api-key") apikey: String = BuildConfig.API_KEY
    ): MovieCatalogDto

    companion object {
        const val BASE_URL = "https://api.nytimes.com/"
    }

}