package com.rick.movie.data_movie.data.repository.trending_series

import com.rick.data.analytics.AnalyticsHelper
import com.rick.data.datastore.PreferencesDataSource
import com.rick.data.model_movie.TrendingSeriesUserData
import com.rick.movie.data_movie.data.repository.logTrendingSeriesFavoriteToggled
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