package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovieSearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface TrendingMovieSearchRepository {

    /**
     * Populate the fast text search (fts) tables for the search contents.
     */
    suspend fun populateFtsTrendingMovieData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [TrendingMovieSearchResult]
     */
    fun trendingMovieSearchContents(searchQuery: String): Flow<TrendingMovieSearchResult>

    fun getTrendingMovieSearchContentsCount(): Flow<Int>

}