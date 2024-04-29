package com.rick.movie.data_movie.data.repository.trending_series

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data.database_movie.dao.TrendingSeriesDao
import com.rick.data.database_movie.dao.TrendingSeriesRemoteKeysDao
import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.database_movie.model.TrendingSeriesRemoteKeys
import com.rick.data.network_movie.TmdbNetworkDataSource
import com.rick.data.network_movie.model.TrendingSeriesNetwork
import com.rick.movie.data_movie.data.model.asTrendingSeriesEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TrendingSeriesRemoteMediator(
    private val network: TmdbNetworkDataSource,
    private val trendingSeriesDao: TrendingSeriesDao,
    private val keysDao: TrendingSeriesRemoteKeysDao,
    private val key: String,
) : RemoteMediator<Int, TrendingSeriesEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrendingSeriesEntity>
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
            val response = network.fetchTrendingSeries(
                page = page,
                apikey = key,
            )
            val series = response.results
            val endOfPaginationReached = true

            if (loadType == LoadType.REFRESH) {
                trendingSeriesDao.deleteTrendingSeries(series.map(TrendingSeriesNetwork::id))
                keysDao.clearRemoteKeys()
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = series.map {
                TrendingSeriesRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            keysDao.insertAll(keys)
            trendingSeriesDao.upsertTrendingSeries(series.map { it.asTrendingSeriesEntity() })

            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TrendingSeriesEntity>): TrendingSeriesRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                keysDao.remoteKeysId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TrendingSeriesEntity>): TrendingSeriesRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { tv ->
                // GEt the remote keys of the first items retrieved
                keysDao.remoteKeysId(tv.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, TrendingSeriesEntity>): TrendingSeriesRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { tv ->
                keysDao.remoteKeysId(tv)
            }
        }
    }

}