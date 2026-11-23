package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [TrendingMovie] & [TrendingMovieEntity]
 * */
interface TrendingMovieRepository {

    fun getTrendingMovies(): Flow<PagingData<TrendingMovieEntity>>

    fun getTrendingMovieFavorites(favorites: Set<Int>): Flow<List<TrendingMovie>>

    fun searchTrendingMovie(query: String): Flow<List<TrendingMovie>>

    /**
     * Fetches the first page of trending movies. If its ids differ from the stored first page,
     * refreshes the list the same way a pull-to-refresh does.
     *
     * @return true if the content changed.
     * @throws java.io.IOException or retrofit2.HttpException on network failure.
     */
    suspend fun syncFirstPage(): Boolean
}
