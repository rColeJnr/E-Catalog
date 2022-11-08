package com.rick.data_movie

import com.rick.data_movie.ny_times.MovieCatalogDto
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieCatalogApi {

    @GET("svc/movies/v2/reviews/all.json?")
    suspend fun fetchMovieCatalog(
        @Query("offset") offset: Int,
        @Query("order") order_by: String = QUERY_ORDER,
        @Query("api-key") apikey: String
    ): MovieCatalogDto

    companion object {
        const val NY_TIMES_BASE_URL = "https://api.nytimes.com/"
        private const val QUERY_ORDER = "by-publication-date"
    }

}
