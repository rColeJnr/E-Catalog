package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.movie.data_movie.model.TrendingMovieUserData
import com.rick.ecatalogs.movie.data_movie.data.repository.logTrendingMovieFavoriteToggled
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserTrendingMovieDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserTrendingMovieDataRepository {

    override val userData: Flow<TrendingMovieUserData>
        get() = preferencesDataSource.trendingMovieUserData

    override suspend fun setTrendingMovieFavoriteId(movieId: Int, isFavorite: Boolean) {
        preferencesDataSource.setTrendingMovieFavoriteIds(movieId, isFavorite)
        analyticsHelper.logTrendingMovieFavoriteToggled(movieId.toString(), isFavorite)
    }

}