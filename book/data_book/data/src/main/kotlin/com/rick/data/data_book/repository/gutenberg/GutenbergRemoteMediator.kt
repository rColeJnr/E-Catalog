package com.rick.data.data_book.repository.gutenberg

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data.data_book.model.asGutenbergEntity
import com.rick.data.database_book.dao.GutenbergDao
import com.rick.data.database_book.dao.GutenbergRemoteKeysDao
import com.rick.data.database_book.model.GutenbergEntity
import com.rick.data.database_book.model.GutenbergRemoteKeys
import com.rick.data.network_book.GutenbergNetworkDataSource
import com.rick.data.network_book.model.GutenbergNetwork
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class GutenbergRemoteMediator(
    private val gutenbergDao: GutenbergDao,
    private val keysDao: GutenbergRemoteKeysDao,
    private val network: GutenbergNetworkDataSource
) : RemoteMediator<Int, GutenbergEntity>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GutenbergEntity>
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
            val response = network.fetchBooks(page)
            val books = response.results
            val endOfPaginationReached = books.isEmpty()


            if (loadType == LoadType.REFRESH) {
                keysDao.clearRemoteKeys()
                gutenbergDao.clearGutenbergs(books.map(GutenbergNetwork::id))
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = books.map {
                GutenbergRemoteKeys(
                    book = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            keysDao.insertAll(keys)
            gutenbergDao.upsertGutenberg(books.sortedByDescending { it.downloads }
                .map(GutenbergNetwork::asGutenbergEntity))

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, GutenbergEntity>): GutenbergRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { book ->
                // Get the remote keys of the last item retrieved
                keysDao.remoteKeysId(book.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, GutenbergEntity>): GutenbergRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { book ->
                // GEt the remote keys of the first items retrieved
                keysDao.remoteKeysId(book.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, GutenbergEntity>): GutenbergRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { book ->
                keysDao.remoteKeysId(book)
            }
        }
    }
}