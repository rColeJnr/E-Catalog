package com.rick.data.movie_favorite.repository.trending_movie

import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_movie.TrendingMovieUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserTrendingMovieDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserTrendingMovieDataRepository {

    override val userData: Flow<TrendingMovieUserData>
        get() = preferencesDataSource.trendingMovieUserData

    override suspend fun setTrendingMovieFavoriteId(movieId: Int, isFavorite: Boolean) {
        preferencesDataSource.setTrendingMovieFavoriteIds(movieId, isFavorite)
    }

}