package com.rick.movie.data_movie.network.retrofit

import com.rick.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.movie.data_movie.network.model.MovieResponse
import com.rick.movie.data_movie.network.model.SeriesResponse
import com.rick.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.movie.data_movie.network.model.TrendingSeriesResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface TmdbRetrofitNetworkApi {

    @GET("trending/tv/week?")
    suspend fun fetchTrendingSeries(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("api_key") apikey: String
    ): TrendingSeriesResponse

    @GET("trending/movie/week?")
    suspend fun fetchTrendingMovie(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("api_key") apikey: String
    ): TrendingMovieResponse

    @GET("search/movie?")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("include_adult") include_adult: Boolean = true,
        @Query("language") language: String = "en-US",
        @Query("api_key") apikey: String
    ): TrendingMovieResponse

    @GET("search/tv?")
    suspend fun searchSeries(
        @Query("query") query: String,
        @Query("include_adult") include_adult: Boolean = true,
        @Query("language") language: String = "en-US",
        @Query("api_key") apikey: String
    ): TrendingSeriesResponse

    @GET("tv/{id}?")
    suspend fun fetchSeries(
        @Path("id") id: Int,
        @Query("append_to_response") appendResponse: String = APPEND_RESPONSE,
        @Query("language") language: String = LANGUAGE,
        @Query("api_key") apikey: String
    ): SeriesResponse

    @GET("movie/{id}?")
    suspend fun fetchMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendResponse: String = APPEND_RESPONSE,
        @Query("language") language: String = LANGUAGE,
        @Query("api_key") apikey: String,
    ): MovieResponse

    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
        private const val LANGUAGE = "en-US"
        private const val APPEND_RESPONSE = "similar"
    }

}
//    https://api.themoviedb.org/3/trending/movie/week?page=2/language=en-US/&api_key=
//    https://api.themoviedb.org/3/multi/507089?append_to_response=similar,images&language=en-US/&api_key=
//    https://api.themoviedb.org/3/tv/60625?append_to_response=similar,images&language=en-US/&api_key=
//    https://api.themoviedb.org/3/trending/movie/week?language=en-US/&api_key=
//    https://api.themoviedb.org/3/trending/tv/week?language=en-US/&api_key=
//    https://api.themoviedb.org/3/search/multi?query=Jack+Reacher&api_key=
//    https://api.themoviedb.org/3/search/multi?query=something&include_adult=true&language=en-US&page=1
//    https://api.themoviedb.org/3/tv/157065?api_key=&append_to_response=images,similar

@Singleton
class TmdbRetrofitNetwork @Inject constructor() : TmdbNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create(TmdbRetrofitNetworkApi::class.java)

    override suspend fun fetchTrendingSeries(
        page: Int,
        language: String,
        apikey: String
    ): TrendingSeriesResponse =
        networkApi.fetchTrendingSeries(page, language, apikey)

    override suspend fun fetchTrendingMovies(
        page: Int,
        language: String,
        apikey: String
    ): TrendingMovieResponse =
        networkApi.fetchTrendingMovie(page, language, apikey)

    override suspend fun fetchSeries(
        id: Int,
        appendResponse: String,
        language: String,
        apikey: String,
    ): SeriesResponse =
        networkApi.fetchSeries(id, appendResponse, language, apikey)

    override suspend fun fetchMovie(
        id: Int,
        appendResponse: String,
        language: String,
        apikey: String,
    ): MovieResponse =
        networkApi.fetchMovie(id, appendResponse, language, apikey)

    override suspend fun searchSeries(
        query: String,
        include_adult: Boolean,
        language: String,
        apikey: String
    ): TrendingSeriesResponse =
        networkApi.searchSeries(query, include_adult, language, apikey)

    override suspend fun searchMovie(
        query: String,
        include_adult: Boolean,
        language: String,
        apikey: String
    ): TrendingMovieResponse =
        networkApi.searchMovie(query, include_adult, language, apikey)
}