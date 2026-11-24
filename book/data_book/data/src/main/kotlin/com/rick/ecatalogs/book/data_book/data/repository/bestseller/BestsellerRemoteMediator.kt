package com.rick.ecatalogs.book.data_book.data.repository.bestseller

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.book.data_book.database.BookTransactionRunner
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerDao
import com.rick.ecatalogs.book.data_book.database.dao.BestsellerRemoteKeysDao
import com.rick.ecatalogs.book.data_book.database.model.BestsellerEntity
import com.rick.ecatalogs.book.data_book.network.BestsellerNetworkDataSource
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import retrofit2.HttpException
import java.io.IOException

/**
 * Pages one NYT bestseller list into Room by `offset` (0, 20, 40, …) until `num_results`.
 * Most lists have 10-15 books and fit in the first page; longer ones load more as you scroll.
 */
@OptIn(ExperimentalPagingApi::class)
internal class BestsellerRemoteMediator(
    private val bestsellerDao: BestsellerDao,
    private val keysDao: BestsellerRemoteKeysDao,
    private val network: BestsellerNetworkDataSource,
    private val transactionRunner: BookTransactionRunner,
    private val listCache: ListCache,
    private val genre: String,
) : RemoteMediator<Int, BestsellerEntity>() {

    private val cacheKey = CachedList.bestseller(genre)

    /**
     * Show the stored list while it's younger than the sync interval (no download when opening
     * the screen); otherwise refresh it. Retrying from the screen always downloads.
     */
    override suspend fun initialize(): InitializeAction =
        if (listCache.isFresh(cacheKey)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, BestsellerEntity>,
    ): MediatorResult {
        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            // Lists are always loaded from the top
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val lastBook = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                val keys = keysDao.remoteKeysId(bestsellerRemoteKeyId(genre, lastBook.id))
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                keys.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val response = network.fetchBestsellers(
                date = CURRENT_LIST,
                bookGenre = genre,
                offset = offset,
            )
            val endReached = writeBestsellerPage(
                genre = genre,
                offset = offset,
                response = response,
                isRefresh = loadType == LoadType.REFRESH,
                bestsellerDao = bestsellerDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
            )
            if (loadType == LoadType.REFRESH) listCache.markRefreshed(cacheKey)
            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

internal const val CURRENT_LIST = "current"
