package com.rick.data.movie_favorite.repository.trending_series

import androidx.paging.PagingData
import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [TrendingSeries] & [TrendingSeriesEntity]
 * */
interface TrendingSeriesRepository {
    fun getTrendingSeries(apiKey: String): Flow<PagingData<TrendingSeriesEntity>>
    fun getTrendingSeriesFavorites(favorites: Set<Int>): Flow<List<TrendingSeries>>
}