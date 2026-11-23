package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse

internal const val TRENDING_SERIES_FIRST_PAGE = 1
private const val TOTAL_NETWORK_PAGES = 100

/**
 * Stores one fetched page of trending series. Shared by [TrendingSeriesRemoteMediator] and the
 * background sync so both refresh the list the same way.
 *
 * On [isRefresh] the fetched ids are deleted and all remote keys are cleared before inserting.
 *
 * @return true when the end of pagination has been reached.
 */
internal suspend fun writeTrendingSeriesPage(
    page: Int,
    response: TrendingSeriesResponse,
    isRefresh: Boolean,
    trendingSeriesDao: TrendingSeriesDao,
    keysDao: TrendingSeriesRemoteKeysDao,
    transactionRunner: MovieTransactionRunner,
): Boolean {
    val series = response.results
    val endOfPaginationReached = response.page == TOTAL_NETWORK_PAGES

    transactionRunner {
        if (isRefresh) {
            trendingSeriesDao.deleteTrendingSeries(series.map(TrendingSeriesNetwork::id))
            keysDao.clearRemoteKeys()
        }
        val prevKey = if (page == TRENDING_SERIES_FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        val keys = series.map {
            TrendingSeriesRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }
        keysDao.insertAll(keys)
        trendingSeriesDao.upsertTrendingSeries(series.map { it.asTrendingSeriesEntity() })
    }
    return endOfPaginationReached
}
