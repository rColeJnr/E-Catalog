package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.anime.data_anime.model.AnimeSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface AnimeSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsAnimeData(query: String)

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [animeSearchResult]
     */
    fun animeSearchContents(searchQuery: String): Flow<AnimeSearchResult>

    fun getAnimeSearchContentsCount(): Flow<Int>

}