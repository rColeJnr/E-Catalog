package com.rick.ecatalogs.game.data_game.network.retrofit

import com.rick.ecatalogs.game.data_game.network.BuildConfig
import com.rick.ecatalogs.game.data_game.network.IgdbNetworkDataSource
import com.rick.ecatalogs.game.data_game.network.IgdbQueries
import com.rick.ecatalogs.game.data_game.network.model.IgdbGameNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbNamedNetwork
import com.rick.ecatalogs.game.data_game.network.model.IgdbPopularityNetwork
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject
import javax.inject.Singleton

private interface IgdbRetrofitNetworkApi {
    @POST("v4/games")
    suspend fun games(@Body query: RequestBody): List<IgdbGameNetwork>

    @POST("v4/popularity_primitives")
    suspend fun popularity(@Body query: RequestBody): List<IgdbPopularityNetwork>

    @POST("v4/genres")
    suspend fun genres(@Body query: RequestBody): List<IgdbNamedNetwork>
}

@Singleton
internal class IgdbRetrofitNetwork @Inject constructor(
    tokens: TwitchTokenProvider,
) : IgdbNetworkDataSource {

    private val networkApi = Retrofit.Builder()
        .baseUrl(IGDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(IgdbAuthInterceptor(tokens))
                .addInterceptor(
                    // Headers carry the token: log them only in debug builds
                    HttpLoggingInterceptor().setLevel(
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE,
                    ),
                )
                .build()
        )
        .build()
        .create(IgdbRetrofitNetworkApi::class.java)

    // IGDB allows 4 requests a second: start requests at least MIN_REQUEST_INTERVAL_MILLIS
    // apart, app-wide (this class is a singleton), like JikanRetrofitNetwork
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

    override val isConfigured: Boolean =
        BuildConfig.IGDB_CLIENT_ID.isNotBlank() && BuildConfig.IGDB_CLIENT_SECRET.isNotBlank()

    private fun String.asBody() = toRequestBody(TEXT)

    override suspend fun fetchPopularGames(page: Int): List<IgdbGameNetwork> {
        val ids = throttled { networkApi.popularity(IgdbQueries.popularIds(page).asBody()) }
            .map(IgdbPopularityNetwork::gameId)
            .distinct()
        if (ids.isEmpty()) return emptyList()
        val games = throttled { networkApi.games(IgdbQueries.gamesByIds(ids).asBody()) }
            .associateBy(IgdbGameNetwork::id)
        // IGDB returns the games in id order: put them back in popularity order
        return ids.mapNotNull(games::get)
    }

    override suspend fun fetchComingSoon(now: Long): List<IgdbGameNetwork> =
        throttled { networkApi.games(IgdbQueries.comingSoon(now).asBody()) }
            .sortedBy { it.firstReleaseDate ?: Long.MAX_VALUE }

    override suspend fun fetchRecentlyReleased(now: Long): List<IgdbGameNetwork> =
        throttled { networkApi.games(IgdbQueries.recentlyReleased(now).asBody()) }
            .sortedByDescending { it.firstReleaseDate ?: 0L }

    override suspend fun fetchGenres(): List<IgdbNamedNetwork> =
        throttled { networkApi.genres(IgdbQueries.GENRES.asBody()) }

    override suspend fun fetchGamesByGenre(genreId: Int, page: Int): List<IgdbGameNetwork> =
        throttled { networkApi.games(IgdbQueries.byGenre(genreId, page).asBody()) }

    override suspend fun fetchGameDetails(id: Int): IgdbGameNetwork? =
        throttled { networkApi.games(IgdbQueries.details(id).asBody()) }.firstOrNull()

    override suspend fun searchGames(query: String): List<IgdbGameNetwork> =
        throttled { networkApi.games(IgdbQueries.search(query).asBody()) }
}

private const val IGDB_BASE_URL = "https://api.igdb.com/"
private const val MIN_REQUEST_INTERVAL_MILLIS = 260L
private val TEXT = "text/plain".toMediaType()
