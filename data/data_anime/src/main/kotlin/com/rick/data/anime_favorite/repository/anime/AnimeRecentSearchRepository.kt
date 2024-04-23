package com.rick.data.anime_favorite.repository.anime

import com.rick.data.model_anime.AnimeRecentSearchQuery
import kotlinx.coroutines.flow.Flow


/**
 * Data layer interface for anime recent searches
 * */
interface AnimeRecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getAnimeRecentSearchQueries(limit: Int): Flow<List<AnimeRecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     */
    suspend fun insertOrReplaceAnimeRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearAnimeRecentSearches()
}