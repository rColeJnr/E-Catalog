package com.rick.ecatalogs.anime.data_anime.network

import com.rick.ecatalogs.anime.data_anime.network.model.AnimeFullResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanEpisodesResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanGenresResponse
import com.rick.ecatalogs.anime.data_anime.network.model.JikanRecommendationsResponse
import com.rick.ecatalogs.anime.data_anime.network.model.MangaFullResponse
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

    /** Anime airing this season. */
    suspend fun fetchSeasonNow(): AnimeResponse

    /** Anime announced for next season(s). */
    suspend fun fetchSeasonUpcoming(): AnimeResponse

    suspend fun fetchAnimeGenres(): JikanGenresResponse

    suspend fun fetchMangaGenres(): JikanGenresResponse

    /**
     * Browse anime (Jikan `anime` search without a query): ordered by [orderBy]
     * ("members", "score", "favorites"…), optionally only one [status] ("airing", "upcoming")
     * and genre ([genres], a Jikan genre id).
     */
    suspend fun browseAnime(
        page: Int,
        orderBy: String,
        sort: String,
        status: String? = null,
        genres: Int? = null,
    ): AnimeResponse

    /** Browse manga; [status] e.g. "publishing". */
    suspend fun browseManga(
        page: Int,
        orderBy: String,
        sort: String,
        status: String? = null,
        genres: Int? = null,
    ): MangaResponse

    /** The anime with streaming services and broadcast time. */
    suspend fun fetchAnimeFull(id: Int): AnimeFullResponse

    suspend fun fetchMangaFull(id: Int): MangaFullResponse

    /** One page (up to 100) of an anime's episodes. */
    suspend fun fetchAnimeEpisodes(id: Int, page: Int): JikanEpisodesResponse

    suspend fun fetchAnimeRecommendations(id: Int): JikanRecommendationsResponse

    suspend fun fetchMangaRecommendations(id: Int): JikanRecommendationsResponse

    suspend fun searchAnime(
        query: String,
        sfw: Boolean = true,
        limit: Int
    ): AnimeResponse

    suspend fun searchManga(
        query: String,
        sfw: Boolean = true,
        limit: Int
    ): MangaResponse

}