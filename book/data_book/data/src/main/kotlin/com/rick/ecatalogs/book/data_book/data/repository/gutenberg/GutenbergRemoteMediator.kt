package com.rick.ecatalogs.book.data_book.data.repository.gutenberg

import com.rick.ecatalogs.book.data_book.model.gutenberg.GutenbergFilter
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.book.data_book.network.GutenbergNetworkDataSource
import com.rick.ecatalogs.book.data_book.data.model.asGutenbergEntity
import com.rick.ecatalogs.book.data_book.database.BookTransactionRunner
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergDao
import com.rick.ecatalogs.book.data_book.database.dao.GutenbergRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.model.GutenbergEntity
import com.rick.ecatalogs.book.data_book.database.model.GutenbergRemoteKeys
import retrofit2.HttpException
import java.io.IOException


@OptIn(ExperimentalPagingApi::class)
class GutenbergRemoteMediator(
    private val gutenbergDao: GutenbergDao,
    private val keysDao: GutenbergRemoteKeysDao,
    private val network: GutenbergNetworkDataSource,
    private val transactionRunner: BookTransactionRunner,
    private val listCache: ListCache,
    private val filter: GutenbergFilter,
    /** Rows a refresh keeps although they leave the list: the favorites. */
    private val favoriteIds: suspend () -> Set<Int>,
) : RemoteMediator<Int, GutenbergEntity>() {

    /**
     * Show the list stored on the phone while it's younger than the sync interval (no download
     * when opening the screen); otherwise refresh it. Retrying from the screen always downloads.
     */
    override suspend fun initialize(): InitializeAction =
        // Fresh only if the stored list is this filter's (one filter is stored at a time)
        if (listCache.isFresh(filter.cacheKey)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
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
            val response = network.fetchBooks(page, languages = filter.language, topic = filter.topic)
            val books = response.results
            val endOfPaginationReached = books.isEmpty()

            // Read before the transaction: favorites live in DataStore, not in Room
            val keepIds = if (loadType == LoadType.REFRESH) favoriteIds() else emptySet()
            transactionRunner {
                if (loadType == LoadType.REFRESH) {
                    keysDao.clearRemoteKeys()
                    // Written anew for this filter: the previous filter's books leave the list,
                    // and those that aren't favorites are deleted
                    gutenbergDao.clearCatalog()
                    gutenbergDao.deleteOutsideCatalog(keepIds)
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
                gutenbergDao.upsertGutenberg(
                    books.sortedByDescending { it.downloads }.mapIndexed { index, book ->
                        book.asGutenbergEntity().copy(catalogPosition = page * PAGE_STRIDE + index)
                    },
                )
            }

            if (loadType == LoadType.REFRESH) {
                listCache.markRefreshedExclusive(filter.cacheKey, prefix = CachedList.GUTENBERG)
            }
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

/** Room for a page's books in catalog_position (Gutendex pages hold 32). */
private const val PAGE_STRIDE = 1_000
