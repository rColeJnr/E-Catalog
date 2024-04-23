package com.rick.data.movie_favorite.repository.trending_series

import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_movie.TrendingSeriesUserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserTrendingSeriesDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource
) : UserTrendingSeriesDataRepository {

    override val userData: Flow<TrendingSeriesUserData>
        get() = preferencesDataSource.trendingSeriesUserData

    override suspend fun setTrendingSeriesFavoriteId(seriesId: Int, isFavorite: Boolean) {
        preferencesDataSource.setTrendingSeriesFavoriteIds(seriesId, isFavorite)
    }

}