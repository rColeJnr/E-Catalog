package com.rick.data_anime

import com.rick.data_anime.model.AnimeResponseDto
import retrofit2.http.GET

interface JikanApi {

    @GET("/top/anime")
    fun fetchTopAnime(
    ): AnimeResponseDto

    @GET("/top/anime")
    fun fetchTopManga(
    ): AnimeResponseDto


    companion object {
        const val JIKAN_BASE_URL = "https://api.jikan.moe/v4/"
    }
}