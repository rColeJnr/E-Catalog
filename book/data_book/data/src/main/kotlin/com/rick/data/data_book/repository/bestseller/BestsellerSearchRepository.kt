package com.rick.data.data_book.repository.bestseller

import com.rick.data.model_book.bestseller.BestsellerSearchResult
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