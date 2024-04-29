package com.rick.data.data_book.repository.gutenberg

import com.rick.data.model_book.gutenberg.GutenbergRecentSearchQuery
import kotlinx.coroutines.flow.Flow


/**
 * Data layer interface for anime recent searches
 * */
interface GutenbergRecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getGutenbergRecentSearchQueries(limit: Int): Flow<List<GutenbergRecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     */
    suspend fun insertOrReplaceGutenbergRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearGutenbergRecentSearches()
}