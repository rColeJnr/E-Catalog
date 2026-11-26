package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.data.repository.catalogPosition
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse

internal const val TRENDING_MOVIE_FIRST_PAGE = 1
private const val TOTAL_NETWORK_PAGES = 100

/**
 * Stores one fetched page of trending movies. Shared by [TrendingMovieRemoteMediator] and the
 * background sync so both refresh the list the same way.
 *
 * On [isRefresh] the list is written anew: every row leaves the catalog list, the rows that aren't
 * in [keepIds] (the favorites) are deleted, and all remote keys are cleared.
 *
 * @return true when the end of pagination has been reached.
 */
internal suspend fun writeTrendingMoviePage(
    page: Int,
    response: TrendingMovieResponse,
    isRefresh: Boolean,
    trendingMovieDao: TrendingMovieDao,
    keysDao: TrendingMovieRemoteKeysDao,
    transactionRunner: MovieTransactionRunner,
    keepIds: Set<Int>,
): Boolean {
    val movies = response.results
    val endOfPaginationReached = response.page == TOTAL_NETWORK_PAGES

    transactionRunner {
        if (isRefresh) {
            trendingMovieDao.clearCatalog()
            trendingMovieDao.deleteOutsideCatalog(keepIds)
            keysDao.clearRemoteKeys()
        }
        val prevKey = if (page == TRENDING_MOVIE_FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        val keys = movies.map {
            TrendingMovieRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }

        keysDao.insertAll(keys)
        trendingMovieDao.upsertTrendingMovie(
            movies.mapIndexed { index, item ->
                item.asTrendingMovieEntity().copy(catalogPosition = catalogPosition(page, index))
            },
        )
    }
    return endOfPaginationReached
}
