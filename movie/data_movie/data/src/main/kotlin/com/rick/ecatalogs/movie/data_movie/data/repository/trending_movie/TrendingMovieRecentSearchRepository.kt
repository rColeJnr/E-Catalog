package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.model.TmdbRecentSearchQuery
import kotlinx.coroutines.flow.Flow


/**
 * Data layer interface for anime recent searches
 * */
interface TrendingMovieRecentSearchRepository {

    /**
     * Get the recent search queries up to the number of queries specified as [limit].
     */
    fun getTrendingMovieRecentSearchQueries(limit: Int): Flow<List<TmdbRecentSearchQuery>>

    /**
     * Insert or replace the [searchQuery] as part of the recent searches.
     */
    suspend fun insertOrReplaceTrendingMovieRecentSearch(searchQuery: String)

    /**
     * Clear the recent searches.
     */
    suspend fun clearTrendingMovieRecentSearches()
}