package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TrendingMovieRemoteMediator(
    private val network: TmdbNetworkDataSource,
    private val keysDao: TrendingMovieRemoteKeysDao,
    private val trendingMovieDao: TrendingMovieDao,
    private val transactionRunner: MovieTransactionRunner,
    private val listCache: ListCache,
    /** Rows a refresh keeps although they leave the list: the favorites. */
    private val favoriteIds: suspend () -> Set<Int>,
) : RemoteMediator<Int, TrendingMovieEntity>() {

    /**
     * Show the list stored on the phone while it's younger than the sync interval (no download
     * when opening the screen); otherwise refresh it. Retrying from the screen always downloads.
     */
    override suspend fun initialize(): InitializeAction =
        if (listCache.isFresh(CachedList.TRENDING_MOVIES)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, TrendingMovieEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }

            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
        }

        try {
            val response = network.fetchTrendingMovies(page = page)
            writeTrendingMoviePage(
                page = page,
                response = response,
                isRefresh = loadType == LoadType.REFRESH,
                trendingMovieDao = trendingMovieDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                keepIds = if (loadType == LoadType.REFRESH) favoriteIds() else emptySet(),
            )

            if (loadType == LoadType.REFRESH) listCache.markRefreshed(CachedList.TRENDING_MOVIES)
            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TrendingMovieEntity>): TrendingMovieRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { movie ->
                // Get the remote keys of the last item retrieved
                keysDao.remoteKeysMovieId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TrendingMovieEntity>): TrendingMovieRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                // GEt the remote keys of the first items retrieved
                keysDao.remoteKeysMovieId(movie.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, TrendingMovieEntity>): TrendingMovieRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { movie ->
                keysDao.remoteKeysMovieId(movie)
            }
        }
    }

}

private const val TAG = "TrendingMovieRemoteMediator"