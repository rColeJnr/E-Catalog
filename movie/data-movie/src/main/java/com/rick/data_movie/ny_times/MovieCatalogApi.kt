package com.rick.data_movie.ny_times

import com.rick.data_movie.ny_times.article_models.NYResult
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieCatalogApi {

//    @Deprecated("Use fetchMovieArticles instead")
//    @GET("svc/movies/v2/reviews/all.json?")
//    suspend fun fetchMovieCatalog(
//        @Query("offset") offset: Int,
//        @Query("order") order_by: String = QUERY_ORDER,
//        @Query("api-key") apikey: String
//    ): MovieCatalogDto

    @GET("svc/search/v2/articlesearch.json?")
    suspend fun fetchMovieArticles(
        @Query("fq") section_name: String = MOVIE_QUERY,
        @Query("sort") sort: String = SORT,
        @Query("page") page: Int,
        @Query("api-key") apikey: String
    ): NYResult

    companion object {
        const val NY_TIMES_BASE_URL = "https://api.nytimes.com/"
        private const val QUERY_ORDER = "by-publication-date"
        private const val MOVIE_QUERY = "section_name:\"Movies\" AND type_of_material:\"Review\""
        private const val SORT = "newest"
    }

}

//https://api.nytimes.com/svc/search/v2/articlesearch.json?fq=section_name:"Movies"ANDtype_of_material:"Review"&sort=newest&page=0&api-key{your-key}