package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import kotlinx.coroutines.flow.Flow

/**
 * Data layer implementation for [TrendingSeries] & [TrendingSeriesEntity]
 * */
interface TrendingSeriesRepository {

    fun getTrendingSeries(): Flow<PagingData<TrendingSeriesEntity>>

    fun getTrendingSeriesFavorites(favorites: Set<Int>): Flow<List<TrendingSeries>>

    fun searchTrendingSeries(query: String): Flow<List<TrendingSeries>>

    /**
     * Fetches the first page of trending series. If its ids differ from the stored first page,
     * refreshes the list the same way a pull-to-refresh does.
     *
     * @return true if the content changed.
     * @throws java.io.IOException or retrofit2.HttpException on network failure.
     */
    suspend fun syncFirstPage(): Boolean
}
