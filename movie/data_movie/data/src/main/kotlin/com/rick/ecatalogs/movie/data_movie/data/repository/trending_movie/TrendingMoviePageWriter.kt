package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieNetwork
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieResponse

internal const val TRENDING_MOVIE_FIRST_PAGE = 1
private const val TOTAL_NETWORK_PAGES = 100

/**
 * Stores one fetched page of trending movies. Shared by [TrendingMovieRemoteMediator] and the
 * background sync so both refresh the list the same way.
 *
 * On [isRefresh] the fetched ids are deleted and all remote keys are cleared before inserting.
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
): Boolean {
    val movies = response.results
    val endOfPaginationReached = response.page == TOTAL_NETWORK_PAGES

    transactionRunner {
        if (isRefresh) {
            trendingMovieDao.deleteTrendingMovie(movies.map(TrendingMovieNetwork::id))
            keysDao.clearRemoteKeys()
        }
        val prevKey = if (page == TRENDING_MOVIE_FIRST_PAGE) null else page - 1
        val nextKey = if (endOfPaginationReached) null else page + 1
        val keys = movies.map {
            TrendingMovieRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
        }

        keysDao.insertAll(keys)
        trendingMovieDao.upsertTrendingMovie(movies.map { it.asTrendingMovieEntity() })
    }
    return endOfPaginationReached
}
