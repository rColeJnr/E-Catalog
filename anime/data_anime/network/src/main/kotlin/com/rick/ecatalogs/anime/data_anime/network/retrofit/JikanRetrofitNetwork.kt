package com.rick.ecatalogs.anime.data_anime.network.retrofit

import com.rick.ecatalogs.anime.data_anime.network.BuildConfig
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton

private interface JikanRetrofitNetworkApi {

    @GET("/v4/top/anime")
    suspend fun fetchTopAnime(
        @Query("page") page: Int,
        @Query("filter") filter: String = POPULARITY_FILTER
    ): AnimeResponse

    @GET("/v4/top/manga")
    suspend fun fetchTopManga(
        @Query("page") page: Int,
        @Query("filter") filter: String = POPULARITY_FILTER
    ): MangaResponse

    @GET("/v4/anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        // Exclude adult titles (Jikan only filters them when sfw=true)
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = RESULT_SIZE
    ): AnimeResponse

    @GET("/v4/manga")
    suspend fun searchManga(
        @Query("q") query: String,
        // Exclude adult titles (Jikan only filters them when sfw=true)
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = RESULT_SIZE
    ): MangaResponse

    @GET("/v4/seasons/now")
    suspend fun fetchSeasonNow(
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = ROW_SIZE,
    ): AnimeResponse

    @GET("/v4/seasons/upcoming")
    suspend fun fetchSeasonUpcoming(
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = ROW_SIZE,
    ): AnimeResponse

    @GET("/v4/genres/anime")
    suspend fun fetchAnimeGenres(@Query("filter") filter: String = "genres"): JikanGenresResponse

    @GET("/v4/genres/manga")
    suspend fun fetchMangaGenres(@Query("filter") filter: String = "genres"): JikanGenresResponse

    @GET("/v4/anime")
    suspend fun browseAnime(
        @Query("page") page: Int,
        @Query("order_by") orderBy: String,
        @Query("sort") sort: String,
        @Query("status") status: String?,
        @Query("genres") genres: Int?,
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = RESULT_SIZE,
    ): AnimeResponse

    @GET("/v4/manga")
    suspend fun browseManga(
        @Query("page") page: Int,
        @Query("order_by") orderBy: String,
        @Query("sort") sort: String,
        @Query("status") status: String?,
        @Query("genres") genres: Int?,
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = RESULT_SIZE,
    ): MangaResponse

    @GET("/v4/anime/{id}/full")
    suspend fun fetchAnimeFull(@Path("id") id: Int): AnimeFullResponse

    @GET("/v4/manga/{id}/full")
    suspend fun fetchMangaFull(@Path("id") id: Int): MangaFullResponse

    @GET("/v4/anime/{id}/episodes")
    suspend fun fetchAnimeEpisodes(
        @Path("id") id: Int,
        @Query("page") page: Int,
    ): JikanEpisodesResponse

    @GET("/v4/anime/{id}/recommendations")
    suspend fun fetchAnimeRecommendations(@Path("id") id: Int): JikanRecommendationsResponse

    @GET("/v4/manga/{id}/recommendations")
    suspend fun fetchMangaRecommendations(@Path("id") id: Int): JikanRecommendationsResponse

    @GET("/v4/anime/{id}/characters")
    suspend fun fetchAnimeCharacters(@Path("id") id: Int): JikanCharactersResponse

    @GET("/v4/people/{id}/full")
    suspend fun fetchPerson(@Path("id") id: Int): JikanPersonResponse

    @GET("/v4/schedules")
    suspend fun fetchSchedule(
        @Query("filter") day: String,
        @Query("page") page: Int,
        @Query("sfw") sfw: Boolean = true,
        @Query("limit") limit: Int = RESULT_SIZE,
    ): AnimeResponse

    @GET("/v4/random/anime")
    suspend fun fetchRandomAnime(@Query("sfw") sfw: Boolean = true): AnimeFullResponse

    @GET("/v4/random/manga")
    suspend fun fetchRandomManga(@Query("sfw") sfw: Boolean = true): MangaFullResponse

    companion object {
        private const val ROW_SIZE = 20
        const val JIKAN_BASE_URL = "https://api.jikan.moe/"
        private const val RESULT_SIZE = 25
        private const val POPULARITY_FILTER = "bypopularity"
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

    // Jikan allows about 3 requests a second (60 a minute): start requests at least
    // MIN_REQUEST_INTERVAL_MILLIS apart, app-wide (this class is a singleton)
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

    override suspend fun searchAnime(query: String, sfw: Boolean, limit: Int): AnimeResponse =
        throttled { networkApi.searchAnime(query, sfw, limit) }

    override suspend fun searchManga(query: String, sfw: Boolean, limit: Int): MangaResponse =
        throttled { networkApi.searchManga(query, sfw, limit) }

    override suspend fun fetchSeasonNow() = throttled { networkApi.fetchSeasonNow() }

    override suspend fun fetchSeasonUpcoming() = throttled { networkApi.fetchSeasonUpcoming() }

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

    override suspend fun fetchSchedule(day: String, page: Int) =
        throttled { networkApi.fetchSchedule(day, page) }

    override suspend fun fetchRandomAnime() = throttled { networkApi.fetchRandomAnime() }

    override suspend fun fetchRandomManga() = throttled { networkApi.fetchRandomManga() }
}

private const val MIN_REQUEST_INTERVAL_MILLIS = 400L
