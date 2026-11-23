package com.rick.ecatalogs.movie.data_movie.network.retrofit

import com.rick.ecatalogs.movie.data_movie.network.BuildConfig
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.MovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse
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
    ): TrendingSeriesResponse

    @GET("trending/movie/week?")
    suspend fun fetchTrendingMovie(
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
    ): TrendingMovieResponse

    @GET("search/movie?")
    suspend fun searchMovie(
        @Query("query") query: String,
        @Query("include_adult") include_adult: Boolean = true,
        @Query("language") language: String = "en-US",
    ): TrendingMovieResponse

    @GET("search/tv?")
    suspend fun searchSeries(
        @Query("query") query: String,
        @Query("include_adult") include_adult: Boolean = true,
        @Query("language") language: String = "en-US",
    ): TrendingSeriesResponse

    @GET("tv/{id}?")
    suspend fun fetchSeries(
        @Path("id") id: Int,
        @Query("append_to_response") appendResponse: String = APPEND_RESPONSE,
        @Query("language") language: String = LANGUAGE,
    ): SeriesResponse

    @GET("movie/{id}?")
    suspend fun fetchMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendResponse: String = APPEND_RESPONSE,
        @Query("language") language: String = LANGUAGE,
    ): MovieResponse

    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
        private const val LANGUAGE = "en-US"
        private const val APPEND_RESPONSE = "similar"
    }

}

@Singleton
class TmdbRetrofitNetwork @Inject constructor() : TmdbNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(ApiKeyInterceptor("api_key", BuildConfig.TMDB_API_KEY))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create(TmdbRetrofitNetworkApi::class.java)

    override suspend fun fetchTrendingSeries(
        page: Int,
        language: String,
    ): TrendingSeriesResponse =
        networkApi.fetchTrendingSeries(page, language)

    override suspend fun fetchTrendingMovies(
        page: Int,
        language: String,
    ): TrendingMovieResponse =
        networkApi.fetchTrendingMovie(page, language)

    override suspend fun fetchSeries(
        id: Int,
        appendResponse: String,
        language: String,
    ): SeriesResponse =
        networkApi.fetchSeries(id, appendResponse, language)

    override suspend fun fetchMovie(
        id: Int,
        appendResponse: String,
        language: String,
    ): MovieResponse =
        networkApi.fetchMovie(id, appendResponse, language)

    override suspend fun searchSeries(
        query: String,
        include_adult: Boolean,
        language: String,
    ): TrendingSeriesResponse =
        networkApi.searchSeries(query, include_adult, language)

    override suspend fun searchMovie(
        query: String,
        include_adult: Boolean,
        language: String,
    ): TrendingMovieResponse =
        networkApi.searchMovie(query, include_adult, language)
}
