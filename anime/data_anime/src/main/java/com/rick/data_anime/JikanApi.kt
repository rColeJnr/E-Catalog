package com.rick.data_anime

import com.rick.data_anime.model_jikan.JikanResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApi {

    @GET("/v4/top/anime")
    suspend fun fetchTopAnime(
        @Query("page") page: Int,
        @Query("filter") filter: String = POPULARITY_FILTER
    ): JikanResponseDto

    @GET("/v4/top/manga")
    suspend fun fetchTopManga(
        @Query("page") page: Int,
        @Query("filter") filter: String = POPULARITY_FILTER
    ): JikanResponseDto

    @GET("/v4/anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("sfw") sfw: Boolean = false,
        @Query("max_score") max_score: Int = RESULT_SIZE
    ): JikanResponseDto

    @GET("/v4/manga")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("sfw") sfw: Boolean = false,
        @Query("max_score") max_score: Int = RESULT_SIZE
    ): JikanResponseDto


    companion object {
        const val JIKAN_BASE_URL = "https://api.jikan.moe/"
        private const val RESULT_SIZE = 25
        private const val POPULARITY_FILTER = "bypopularity"
    }
}