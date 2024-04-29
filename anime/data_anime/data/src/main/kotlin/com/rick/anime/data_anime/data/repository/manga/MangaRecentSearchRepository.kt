package com.rick.anime.data_anime.data.repository.manga

import com.rick.data.model_anime.MangaRecentSearchQuery
import kotlinx.coroutines.flow.Flow


/**
 * Data layer interface for anime recent searches
 * */
interface MangaRecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getMangaRecentSearchQueries(limit: Int): Flow<List<MangaRecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     */
    suspend fun insertOrReplaceMangaRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearMangaRecentSearches()
}