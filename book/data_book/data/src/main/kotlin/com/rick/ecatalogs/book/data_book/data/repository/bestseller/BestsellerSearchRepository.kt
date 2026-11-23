package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import com.rick.ecatalogs.book.data_book.model.bestseller.BestsellerSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface BestsellerSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsBestsellerData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [BestsellerSearchResult]
     */
    fun bestsellerSearchContents(searchQuery: String): Flow<BestsellerSearchResult>

    fun getBestsellerSearchContentsCount(): Flow<Int>

}