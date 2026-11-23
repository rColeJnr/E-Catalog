package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface GutenbergSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsGutenbergData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [GutenbergSearchResult]
     */
    fun gutenbergSearchContents(searchQuery: String): Flow<GutenbergSearchResult>

    fun getGutenbergSearchContentsCount(): Flow<Int>

}