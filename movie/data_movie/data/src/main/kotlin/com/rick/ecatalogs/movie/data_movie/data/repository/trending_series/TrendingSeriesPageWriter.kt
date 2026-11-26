package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import com.rick.ecatalogs.movie.data_movie.data.repository.catalogPosition
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesResponse

internal const val TRENDING_SERIES_FIRST_PAGE = 1
private const val TOTAL_NETWORK_PAGES = 100

/**
 * Stores one fetched page of trending series. Shared by [TrendingSeriesRemoteMediator] and the
 * background sync so both refresh the list the same way.
 *
 * On [isRefresh] the list is written anew: every row leaves the catalog list, the rows that aren't
 * in [keepIds] (the favorites) are deleted, and all remote keys are cleared.
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
    keepIds: Set<Int>,
): Boolean {
    val series = response.results
    val endOfPaginationReached = response.page == TOTAL_NETWORK_PAGES

    transactionRunner {
        if (isRefresh) {
            trendingSeriesDao.clearCatalog()
            trendingSeriesDao.deleteOutsideCatalog(keepIds)
            keysDao.clearRemoteKeys()
        }
        val prevKey = if (page == TRENDING_SERIES_FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        val keys = series.map {
            TrendingSeriesRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }
        keysDao.insertAll(keys)
        trendingSeriesDao.upsertTrendingSeries(
            series.mapIndexed { index, item ->
                item.asTrendingSeriesEntity().copy(catalogPosition = catalogPosition(page, index))
            },
        )
    }
    return endOfPaginationReached
}
