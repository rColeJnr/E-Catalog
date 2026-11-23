package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import com.rick.ecatalogs.data.analytics.AnalyticsHelper
import com.rick.ecatalogs.data.datastore.PreferencesDataSource
import com.rick.ecatalogs.movie.data_movie.model.TrendingSeriesUserData
import com.rick.ecatalogs.movie.data_movie.data.repository.logTrendingSeriesFavoriteToggled
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserTrendingSeriesDataRepositoryImpl @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val analyticsHelper: AnalyticsHelper
) : UserTrendingSeriesDataRepository {

    override val userData: Flow<TrendingSeriesUserData>
        get() = preferencesDataSource.trendingSeriesUserData

    override suspend fun setTrendingSeriesFavoriteId(seriesId: Int, isFavorite: Boolean) {
        preferencesDataSource.setTrendingSeriesFavoriteIds(seriesId, isFavorite)
        analyticsHelper.logTrendingSeriesFavoriteToggled(seriesId.toString(), isFavorite)
    }

}