package com.rick.movie.data_movie.data.repository.trending_movie

import androidx.paging.PagingData
import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [TrendingMovie] & [TrendingMovieEntity]
 * */
interface TrendingMovieRepository {

    fun getTrendingMovies(apiKey: String): Flow<PagingData<TrendingMovieEntity>>

    fun getTrendingMovieFavorites(favorites: Set<Int>): Flow<List<TrendingMovie>>

    fun searchTrendingMovie(apiKey: String, query: String): Flow<List<TrendingMovie>>
}