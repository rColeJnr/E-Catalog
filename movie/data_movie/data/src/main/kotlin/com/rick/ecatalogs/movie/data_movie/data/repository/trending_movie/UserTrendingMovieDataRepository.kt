package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.model.TrendingMovieUserData
import kotlinx.coroutines.flow.Flow

interface UserTrendingMovieDataRepository {

    /**
     * Stream of [TrendingMovieUserData]
     */
    val userData: Flow<TrendingMovieUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setTrendingMovieFavoriteId(movieId: Int, isFavorite: Boolean)
}