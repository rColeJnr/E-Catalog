package com.rick.data.movie_favorite.repository.trending_series

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_movie.dao.TrendingSeriesDao
import com.rick.data.database_movie.dao.TrendingSeriesRemoteKeysDao
import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.database_movie.model.asTrendingSeries
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries
import com.rick.data.network_movie.TmdbNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TrendingSeriesRepositoryImpl @Inject constructor(
    private val trendingSeriesDao: TrendingSeriesDao,
    private val keysDao: TrendingSeriesRemoteKeysDao,
    private val network: TmdbNetworkDataSource,
) : TrendingSeriesRepository {
    override fun getTrendingSeries(apiKey: String): Flow<PagingData<TrendingSeriesEntity>> {
        val pagingSourceFactory = { trendingSeriesDao.getTrendingSeries() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 16,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = TrendingSeriesRemoteMediator(
                network = network,
                trendingSeriesDao = trendingSeriesDao,
                keysDao = keysDao,
                key = apiKey
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getTrendingSeriesFavorites(favorites: Set<Int>): Flow<List<TrendingSeries>> =
        trendingSeriesDao.getTrendingSeriesWithFilters(favorites)
            .map { trendingSeriesEntities -> trendingSeriesEntities.map { it.asTrendingSeries() } }
}