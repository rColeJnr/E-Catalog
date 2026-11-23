package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [TrendingSeries] & [TrendingSeriesEntity]
 * */
interface TrendingSeriesRepository {

    fun getTrendingSeries(apiKey: String): Flow<PagingData<TrendingSeriesEntity>>

    fun getTrendingSeriesFavorites(favorites: Set<Int>): Flow<List<TrendingSeries>>

    fun searchTrendingSeries(apiKey: String, query: String): Flow<List<TrendingSeries>>
}