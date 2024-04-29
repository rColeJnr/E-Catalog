package com.rick.data.network_anime.retrofit

import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.model.AnimeResponse
import com.rick.data.network_anime.model.MangaResponse
import com.rick.data.network_anime.retrofit.JikanRetrofitNetworkApi.Companion.JIKAN_BASE_URL
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
        @Query("sfw") sfw: Boolean = false,
        @Query("max_score") max_score: Int = RESULT_SIZE
    ): AnimeResponse

    @GET("/v4/manga")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("sfw") sfw: Boolean = false,
        @Query("max_score") max_score: Int = RESULT_SIZE
    ): MangaResponse

    companion object {
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
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build()
        )
        .build()
        .create(JikanRetrofitNetworkApi::class.java)

    override suspend fun fetchTopAnime(page: Int, filter: String): AnimeResponse {
        return networkApi.fetchTopAnime(page, filter)
    }

    override suspend fun fetchTopManga(page: Int, filter: String): MangaResponse {
        return networkApi.fetchTopManga(page, filter)
    }

    override suspend fun searchAnime(query: String, sfw: Boolean, max_score: Int): AnimeResponse {
        return networkApi.searchAnime(query, sfw, max_score)
    }

    override suspend fun searchManga(query: String, sfw: Boolean, max_score: Int): MangaResponse {
        return networkApi.searchManga(query, sfw, max_score)
    }
}