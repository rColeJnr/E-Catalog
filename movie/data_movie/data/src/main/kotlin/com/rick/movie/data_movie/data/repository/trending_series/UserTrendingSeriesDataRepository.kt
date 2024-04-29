package com.rick.movie.data_movie.data.repository.trending_series

import com.rick.data.model_movie.TrendingSeriesUserData
import kotlinx.coroutines.flow.Flow

interface UserTrendingSeriesDataRepository {

    /**
     * Stream of [TrendingSeriesUserData]
     */
    val userData: Flow<TrendingSeriesUserData>

    /**
     * updates the favorite status of the resource
     */
    suspend fun setTrendingSeriesFavoriteId(seriesId: Int, isFavorite: Boolean)
}