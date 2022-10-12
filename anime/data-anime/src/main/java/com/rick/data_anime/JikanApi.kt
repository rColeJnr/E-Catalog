package com.rick.data_anime

import com.rick.data_anime.model_anime.AnimeResponseDto
import com.rick.data_anime.model_manga.MangaResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface JikanApi {

    @GET("/top/anime")
    fun fetchTopAnime(
        @Query("page") page: Int
    ): AnimeResponseDto

    @GET("/top/manga")
    fun fetchTopManga(
        @Query("page") page: Int
    ): MangaResponseDto


    companion object {
        const val JIKAN_BASE_URL = "https://api.jikan.moe/v4/"
    }
}