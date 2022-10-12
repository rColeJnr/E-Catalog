package com.rick.data_anime

import com.rick.data_anime.model_anime.AnimeResponseDto
import com.rick.data_anime.model_manga.MangaResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApi {

    @GET("/top/anime")
    suspend fun fetchTopAnime(
        @Query("page") page: Int
    ): AnimeResponseDto

    @GET("/top/manga")
    suspend fun fetchTopManga(
        @Query("page") page: Int
    ): MangaResponseDto

    @GET("/anime")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("sfw") sfw: Boolean = false,
        @Query("max_score") max_score: Int = RESULT_SIZE
    ): AnimeResponseDto

    @GET("/manga")
    suspend fun searchManga(
        @Query("q") query: String,
        @Query("sfw") sfw: Boolean = false,
        @Query("max_score") max_score: Int = RESULT_SIZE
    ): MangaResponseDto


    companion object {
        const val JIKAN_BASE_URL = "https://api.jikan.moe/v4/"
        private const val RESULT_SIZE = 25
    }
}