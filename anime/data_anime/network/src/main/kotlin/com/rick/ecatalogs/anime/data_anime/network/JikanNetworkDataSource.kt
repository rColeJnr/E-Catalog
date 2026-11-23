package com.rick.ecatalogs.anime.data_anime.network

import com.rick.ecatalogs.anime.data_anime.network.model.AnimeResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaResponse

interface JikanNetworkDataSource {

    suspend fun fetchTopAnime(
        page: Int,
        filter: String = "bypopularity"
    ): AnimeResponse

    suspend fun fetchTopManga(
        page: Int,
        filter: String = "bypopularity"
    ): MangaResponse

    suspend fun searchAnime(
        query: String,
        sfw: Boolean = true,
        max_score: Int
    ): AnimeResponse

    suspend fun searchManga(
        query: String,
        sfw: Boolean = true,
        max_score: Int
    ): MangaResponse

}