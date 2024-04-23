package com.rick.data.movie_favorite.repository.trending_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data.database_movie.dao.TrendingMovieDao
import com.rick.data.database_movie.dao.TrendingMovieRemoteKeysDao
import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.database_movie.model.TrendingMovieRemoteKeys
import com.rick.data.movie_favorite.model.asTrendingMovieEntity
import com.rick.data.network_movie.TmdbNetworkDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TrendingMovieRemoteMediator(
    private val network: TmdbNetworkDataSource,
    private val keysDao: TrendingMovieRemoteKeysDao,
    private val trendingMovieDao: TrendingMovieDao,
    private val key: String,
) : RemoteMediator<Int, TrendingMovieEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrendingMovieEntity>
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
            val response = network.fetchTrendingMovies(
                page = page,
                apikey = key,
            )
            val movies = response.results
            val endOfPaginationReached = true /*response.page == TOTAL_NETWORK_PAGES*/

            if (loadType == LoadType.REFRESH) {
                trendingMovieDao.deleteTrendingMovie()
                keysDao.clearRemoteKeys()
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = movies.map {
                TrendingMovieRemoteKeys(
                    id = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            keysDao.insertAll(keys)
            trendingMovieDao.upsertTrendingMovie(movies.map { it.asTrendingMovieEntity() })

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
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                keysDao.remoteKeysMovieId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TrendingMovieEntity>): TrendingMovieRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
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
private const val TOTAL_NETWORK_PAGES = 1000