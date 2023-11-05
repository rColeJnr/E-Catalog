package com.rick.data_movie.tmdb

import com.rick.data_movie.tmdb.movie.MovieResponse
import com.rick.data_movie.tmdb.search.SearchResponse
import com.rick.data_movie.tmdb.trending_movie.TrendingMovieResponse
import com.rick.data_movie.tmdb.trending_tv.TrendingTvResponse
import com.rick.data_movie.tmdb.tv.TvResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBApi {
//    https://api.themoviedb.org/3/trending/movie/week?page=2/language=en-US/&api_key=9b25e27e722ef7a92d3e6bd98977bc09
//    https://api.themoviedb.org/3/movie/507089?append_to_response=similar,images&language=en-US/&api_key=9b25e27e722ef7a92d3e6bd98977bc09
//    https://api.themoviedb.org/3/tv/60625?append_to_response=similar,images&language=en-US/&api_key=
//    https://api.themoviedb.org/3/trending/movie/week?language=en-US/&api_key=9b25e27e722ef7a92d3e6bd98977bc09
//    https://api.themoviedb.org/3/trending/tv/week?language=en-US/&api_key=9b25e27e722ef7a92d3e6bd98977bc09
//    https://api.themoviedb.org/3/search/multi?query=Jack+Reacher&api_key=9b25e27e722ef7a92d3e6bd98977bc09
//    https://api.themoviedb.org/3/search/multi?query=something&include_adult=true&language=en-US&page=1
//    https://api.themoviedb.org/3/tv/157065?api_key=9b25e27e722ef7a92d3e6bd98977bc09&append_to_response=images,similar
    @GET("trending/tv/week?")
    suspend fun getTrendingTv(
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("api_key") apikey: String
    ): TrendingTvResponse

    @GET("trending/movie/week?")
    suspend fun getTrendingMovie(
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("api_key") apikey: String
    ): TrendingMovieResponse

    @GET("search/multi?")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("include_adult") include_adult: Boolean = true,
        @Query("language") language: String = LANGUAGE,
        @Query("page") page: Int = 1,
        @Query("api_key") apikey: String
    ): SearchResponse

    @GET("tv/")
    suspend fun getTv(
        @Query("tv_id") id: Int,
        @Query("append_to_response") appendResponse: String = APPEND_RESPONSE,
        @Query("language") language: String = LANGUAGE,
        @Query("api_key") apikey: String
    ): TvResponse

    @GET("movie/")
    suspend fun getMovie(
        @Query("tv_id") id: Int,
        @Query("append_to_response") appendResponse: String = APPEND_RESPONSE,
        @Query("language") language: String = LANGUAGE,
        @Query("api_key") apikey: String
    ): MovieResponse

    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
        private const val LANGUAGE = "en-US"
        private const val APPEND_RESPONSE = "images,similar"
    }
}