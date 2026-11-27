package com.rick.ecatalogs.movie.data_movie.network.retrofit

import com.rick.ecatalogs.movie.data_movie.network.BuildConfig
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.GenresResponse
import com.rick.ecatalogs.movie.data_movie.network.model.MovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeasonResponse
import com.rick.ecatalogs.movie.data_movie.network.model.SeriesResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.rick.ecatalogs.movie.data_movie.network.IMAGE_LANGUAGES
import com.rick.ecatalogs.movie.data_movie.network.MOVIE_APPEND_TO_RESPONSE
import com.rick.ecatalogs.movie.data_movie.network.SERIES_APPEND_TO_RESPONSE
import com.rick.ecatalogs.movie.data_movie.network.PERSON_APPEND_TO_RESPONSE
import com.rick.ecatalogs.movie.data_movie.network.model.PersonResponse
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
        @Query("append_to_response") appendResponse: String = SERIES_APPEND_TO_RESPONSE,
        @Query("language") language: String = LANGUAGE,
        @Query("include_image_language") imageLanguages: String = IMAGE_LANGUAGES,
    ): SeriesResponse

    @GET("movie/upcoming")
    suspend fun fetchUpcomingMovies(
        @Query("region") region: String?,
        @Query("language") language: String = LANGUAGE,
    ): TrendingMovieResponse

    @GET("movie/now_playing")
    suspend fun fetchNowPlayingMovies(
        @Query("region") region: String?,
        @Query("language") language: String = LANGUAGE,
    ): TrendingMovieResponse

    @GET("tv/airing_today")
    suspend fun fetchAiringTodaySeries(
        @Query("language") language: String = LANGUAGE,
    ): TrendingSeriesResponse

    @GET("discover/movie")
    suspend fun fetchRecentlyReleasedMovies(
        @Query("primary_release_date.gte") from: String,
        @Query("primary_release_date.lte") to: String,
        @Query("region") region: String?,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = LANGUAGE,
    ): TrendingMovieResponse

    @GET("discover/tv")
    suspend fun fetchRecentlyReleasedSeries(
        @Query("first_air_date.gte") from: String,
        @Query("first_air_date.lte") to: String,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("include_null_first_air_dates") includeUndated: Boolean = false,
        @Query("language") language: String = LANGUAGE,
    ): TrendingSeriesResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = LANGUAGE,
    ): TrendingMovieResponse

    @GET("discover/tv")
    suspend fun discoverSeries(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String = LANGUAGE,
    ): TrendingSeriesResponse

    @GET("genre/movie/list")
    suspend fun fetchMovieGenres(@Query("language") language: String): GenresResponse

    @GET("genre/tv/list")
    suspend fun fetchSeriesGenres(@Query("language") language: String): GenresResponse

    @GET("tv/{id}/season/{season}")
    suspend fun fetchSeason(
        @Path("id") seriesId: Int,
        @Path("season") seasonNumber: Int,
        @Query("language") language: String = LANGUAGE,
    ): SeasonResponse

    @GET("person/{id}")
    suspend fun fetchPerson(
        @Path("id") id: Int,
        @Query("append_to_response") appendResponse: String = PERSON_APPEND_TO_RESPONSE,
        @Query("language") language: String = LANGUAGE,
    ): PersonResponse

    @GET("movie/{id}?")
    suspend fun fetchMovie(
        @Path("id") id: Int,
        @Query("append_to_response") appendResponse: String = MOVIE_APPEND_TO_RESPONSE,
        @Query("language") language: String = LANGUAGE,
        @Query("include_image_language") imageLanguages: String = IMAGE_LANGUAGES,
    ): MovieResponse

    companion object {
        const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
        private const val LANGUAGE = "en-US"
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
                .addInterceptor(
                    // URLs carry API keys: log them only in debug builds
                    HttpLoggingInterceptor().setLevel(
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE,
                    ),
                )
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

    override suspend fun fetchUpcomingMovies(region: String?) =
        networkApi.fetchUpcomingMovies(region)

    override suspend fun fetchNowPlayingMovies(region: String?) =
        networkApi.fetchNowPlayingMovies(region)

    override suspend fun fetchAiringTodaySeries() = networkApi.fetchAiringTodaySeries()

    override suspend fun fetchRecentlyReleasedMovies(from: String, to: String, region: String?) =
        networkApi.fetchRecentlyReleasedMovies(from, to, region)

    override suspend fun fetchRecentlyReleasedSeries(from: String, to: String) =
        networkApi.fetchRecentlyReleasedSeries(from, to)

    override suspend fun discoverMovies(genreId: Int, page: Int) =
        networkApi.discoverMovies(genreId, page)

    override suspend fun discoverSeries(genreId: Int, page: Int) =
        networkApi.discoverSeries(genreId, page)

    override suspend fun fetchMovieGenres(language: String) =
        networkApi.fetchMovieGenres(language)

    override suspend fun fetchSeriesGenres(language: String) =
        networkApi.fetchSeriesGenres(language)

    override suspend fun fetchSeason(seriesId: Int, seasonNumber: Int) =
        networkApi.fetchSeason(seriesId, seasonNumber)

    override suspend fun fetchPerson(
        id: Int,
        appendResponse: String,
        language: String,
    ): PersonResponse =
        networkApi.fetchPerson(id, appendResponse, language)

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
