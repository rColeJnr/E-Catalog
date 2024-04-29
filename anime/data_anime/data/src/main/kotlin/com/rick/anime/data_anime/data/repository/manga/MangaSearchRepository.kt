package com.rick.anime.data_anime.data.repository.manga

import com.rick.data.model_anime.MangaSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface MangaSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsMangaData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [MangaSearchResult]
     */
    fun mangaSearchContents(searchQuery: String): Flow<MangaSearchResult>

    fun getMangaSearchContentsCount(): Flow<Int>

}