package com.rick.data.movie_favorite.repository.trending_movie

import com.rick.data.model_movie.TrendingMovieUserData
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