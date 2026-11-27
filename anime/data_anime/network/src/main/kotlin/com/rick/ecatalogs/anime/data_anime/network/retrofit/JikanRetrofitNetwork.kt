package com.rick.ecatalogs.anime.data_anime.network.retrofit

import android.util.Log
import com.google.gson.Gson
import com.rick.ecatalogs.anime.data_anime.network.BuildConfig
import com.rick.ecatalogs.anime.data_anime.network.model.EdgeErrorResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanCharactersResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanPersonResponse
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeFullResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanEpisodesResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanGenresResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanRecommendationsResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaFullResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.http.Path
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import com.rick.ecatalogs.anime.data_anime.network.model.AnimeResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaResponse
import com.rick.ecatalogs.anime.data_anime.network.retrofit.JikanRetrofitNetworkApi.Companion.JIKAN_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private interface JikanRetrofitNetworkApi {

    @GET("v1/top/anime")
    suspend fun fetchTopAnime(
        @Query("page") page: Int,
        @Query("filter") filter: String = POPULARITY_FILTER
    ): AnimeResponse

    @GET("v1/top/manga")
    suspend fun fetchTopManga(
        @Query("page") page: Int,
        @Query("filter") filter: String = POPULARITY_FILTER
    ): MangaResponse

    @GET("v1/anime")
    suspend fun searchAnime(@Query("q") query: String): AnimeResponse

    @GET("v1/manga")
    suspend fun searchManga(@Query("q") query: String): MangaResponse

    @GET("v1/seasons/now")
    suspend fun fetchSeasonNow(): AnimeResponse

    @GET("v1/seasons/upcoming")
    suspend fun fetchSeasonUpcoming(): AnimeResponse

    @GET("v1/anime")
    suspend fun fetchRecentAnime(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("order_by") orderBy: String = "members",
        @Query("sort") sort: String = "desc",
    ): AnimeResponse

    @GET("v1/manga")
    suspend fun fetchRecentManga(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("order_by") orderBy: String = "members",
        @Query("sort") sort: String = "desc",
    ): MangaResponse

    @GET("v1/genres/anime")
    suspend fun fetchAnimeGenres(@Query("filter") filter: String = "genres"): JikanGenresResponse

    @GET("v1/genres/manga")
    suspend fun fetchMangaGenres(@Query("filter") filter: String = "genres"): JikanGenresResponse

    @GET("v1/anime")
    suspend fun browseAnime(
        @Query("page") page: Int,
        @Query("order_by") orderBy: String,
        @Query("sort") sort: String,
        @Query("status") status: String?,
        @Query("genres") genres: Int?,
    ): AnimeResponse

    @GET("v1/manga")
    suspend fun browseManga(
        @Query("page") page: Int,
        @Query("order_by") orderBy: String,
        @Query("sort") sort: String,
        @Query("status") status: String?,
        @Query("genres") genres: Int?,
    ): MangaResponse

    @GET("v1/anime/{id}")
    suspend fun fetchAnimeFull(@Path("id") id: Int): AnimeFullResponse

    @GET("v1/manga/{id}")
    suspend fun fetchMangaFull(@Path("id") id: Int): MangaFullResponse

    @GET("v1/anime/{id}/episodes")
    suspend fun fetchAnimeEpisodes(
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): JikanEpisodesResponse

    @GET("v1/anime/{id}/recommendations")
    suspend fun fetchAnimeRecommendations(@Path("id") id: Int): JikanRecommendationsResponse

    @GET("v1/manga/{id}/recommendations")
    suspend fun fetchMangaRecommendations(@Path("id") id: Int): JikanRecommendationsResponse

    @GET("v1/anime/{id}/characters")
    suspend fun fetchAnimeCharacters(@Path("id") id: Int): JikanCharactersResponse

    @GET("v1/people/{id}/full")
    suspend fun fetchPerson(@Path("id") id: Int): JikanPersonResponse

    @GET("v1/schedules")
    suspend fun fetchSchedule(@Query("filter") day: String): AnimeResponse

    @GET("v1/random/anime")
    suspend fun fetchRandomAnime(): AnimeFullResponse

    @GET("v1/random/manga")
    suspend fun fetchRandomManga(): MangaFullResponse

    companion object {
        const val JIKAN_BASE_URL = "https://jikan.lucashdo.com/"
        private const val POPULARITY_FILTER = "bypopularity"
    }
}

class JikanEdgeException(
    val httpStatus: Int,
    val code: String?,
    val requestId: String?,
    message: String?,
) : IOException("jikan-edge $httpStatus ${code.orEmpty()}: ${message.orEmpty()} (requestId=$requestId)")

private class JikanEdgeErrorInterceptor : Interceptor {
    private val gson = Gson()

    override fun intercept(chain: Interceptor.Chain): Response {
        var response = chain.proceed(chain.request())
        if (response.code == 429 || response.code == 503) {
            val retryAfter = response.header("Retry-After")?.toLongOrNull()
            if (retryAfter != null && retryAfter in 0..MAX_RETRY_AFTER_SECONDS) {
                response.close()
                Thread.sleep(retryAfter * 1_000)
                response = chain.proceed(chain.request())
            }
        }
        if (response.isSuccessful) return response

        val error = runCatching {
            gson.fromJson(response.peekBody(MAX_ERROR_BODY_BYTES).string(), EdgeErrorResponse::class.java)?.error
        }.getOrNull()
        val requestId = error?.requestId ?: response.header("X-Request-Id")
        Log.w(
            TAG,
            "${chain.request().url.encodedPath} -> ${response.code} ${error?.code} " +
                "requestId=$requestId: ${error?.message}",
        )
        response.close()
        throw JikanEdgeException(response.code, error?.code, requestId, error?.message)
    }

    private companion object {
        const val TAG = "JikanEdge"
        const val MAX_ERROR_BODY_BYTES = 16_384L
        const val MAX_RETRY_AFTER_SECONDS = 10L
    }
}

@Singleton
internal class JikanRetrofitNetwork @Inject constructor(
) : JikanNetworkDataSource {
    private val networkApi = Retrofit.Builder()
        .baseUrl(JIKAN_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(JikanEdgeErrorInterceptor())
                .addInterceptor(
                    // URLs carry API keys: log them only in debug builds
                    HttpLoggingInterceptor().setLevel(
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE,
                    ),
                )
                .build()
        )
        .build()
        .create(JikanRetrofitNetworkApi::class.java)

    private val rateLimit = Mutex()
    private var lastRequestAt = 0L

    private suspend fun <T> throttled(request: suspend () -> T): T {
        rateLimit.withLock {
            val wait = lastRequestAt + MIN_REQUEST_INTERVAL_MILLIS - System.currentTimeMillis()
            if (wait > 0) delay(wait)
            lastRequestAt = System.currentTimeMillis()
        }
        return request()
    }

    override suspend fun fetchTopAnime(page: Int, filter: String): AnimeResponse =
        throttled { networkApi.fetchTopAnime(page, filter) }

    override suspend fun fetchTopManga(page: Int, filter: String): MangaResponse =
        throttled { networkApi.fetchTopManga(page, filter) }

    override suspend fun searchAnime(query: String): AnimeResponse =
        throttled { networkApi.searchAnime(query.take(MAX_QUERY_LENGTH)) }

    override suspend fun searchManga(query: String): MangaResponse =
        throttled { networkApi.searchManga(query.take(MAX_QUERY_LENGTH)) }

    override suspend fun fetchSeasonNow() = throttled { networkApi.fetchSeasonNow() }

    override suspend fun fetchSeasonUpcoming() = throttled { networkApi.fetchSeasonUpcoming() }

    override suspend fun fetchRecentAnime(startDate: String, endDate: String) =
        throttled { networkApi.fetchRecentAnime(startDate, endDate) }

    override suspend fun fetchRecentManga(startDate: String, endDate: String) =
        throttled { networkApi.fetchRecentManga(startDate, endDate) }

    override suspend fun fetchAnimeGenres() = throttled { networkApi.fetchAnimeGenres() }

    override suspend fun fetchMangaGenres() = throttled { networkApi.fetchMangaGenres() }

    override suspend fun browseAnime(page: Int, orderBy: String, sort: String, status: String?, genres: Int?) =
        throttled { networkApi.browseAnime(page, orderBy, sort, status, genres) }

    override suspend fun browseManga(page: Int, orderBy: String, sort: String, status: String?, genres: Int?) =
        throttled { networkApi.browseManga(page, orderBy, sort, status, genres) }

    override suspend fun fetchAnimeFull(id: Int) = throttled { networkApi.fetchAnimeFull(id) }

    override suspend fun fetchMangaFull(id: Int) = throttled { networkApi.fetchMangaFull(id) }

    override suspend fun fetchAnimeEpisodes(id: Int, page: Int) =
        throttled { networkApi.fetchAnimeEpisodes(id, page) }

    override suspend fun fetchAnimeRecommendations(id: Int) =
        throttled { networkApi.fetchAnimeRecommendations(id) }

    override suspend fun fetchMangaRecommendations(id: Int) =
        throttled { networkApi.fetchMangaRecommendations(id) }

    override suspend fun fetchAnimeCharacters(id: Int) = throttled { networkApi.fetchAnimeCharacters(id) }

    override suspend fun fetchPerson(id: Int) = throttled { networkApi.fetchPerson(id) }

    override suspend fun fetchSchedule(day: String) = throttled { networkApi.fetchSchedule(day) }

    override suspend fun fetchRandomAnime() = throttled { networkApi.fetchRandomAnime() }

    override suspend fun fetchRandomManga() = throttled { networkApi.fetchRandomManga() }
}

private const val MIN_REQUEST_INTERVAL_MILLIS = 1_000L
private const val MAX_QUERY_LENGTH = 64
