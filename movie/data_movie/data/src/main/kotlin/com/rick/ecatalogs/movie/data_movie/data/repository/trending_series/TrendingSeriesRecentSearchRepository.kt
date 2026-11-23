package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import com.rick.ecatalogs.movie.data_movie.model.TmdbRecentSearchQuery
import kotlinx.coroutines.flow.Flow


/**
 * Data layer interface for anime recent searches
 * */
interface TrendingSeriesRecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getTrendingSeriesRecentSearchQueries(limit: Int): Flow<List<TmdbRecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     */
    suspend fun insertOrReplaceTrendingSeriesRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearTrendingSeriesRecentSearches()
}