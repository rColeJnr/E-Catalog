package com.rick.data.movie_favorite.repository.trending_series

import com.rick.data.model_movie.tmdb.trending_series.TrendingSeriesSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface TrendingSeriesSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsTrendingSeriesData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [TrendingSeriesSearchResult]
     */
    fun trendingSeriesSearchContents(searchQuery: String): Flow<TrendingSeriesSearchResult>

    fun getTrendingSeriesSearchContentsCount(): Flow<Int>

}