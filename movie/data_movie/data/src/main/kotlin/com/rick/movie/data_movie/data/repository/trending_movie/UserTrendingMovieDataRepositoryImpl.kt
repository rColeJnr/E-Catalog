package com.rick.movie.data_movie.data.repository.trending_movie

import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_movie.TrendingMovieUserData
import com.rick.movie.data_movie.data.repository.logTrendingMovieFavoriteToggled
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