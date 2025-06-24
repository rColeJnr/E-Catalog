package com.rick.data.data_book.repository.bestseller

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.book.data_book.network.BestsellerNetworkDataSource
import com.rick.book.data_book.network.model.BestsellerNetwork
import com.rick.data.data_book.model.asBestsellerEntity
import com.rick.data.database_book.dao.BestsellerDao
import com.rick.data.database_book.dao.BestsellerRemoteKeysDao
import com.rick.data.database_book.model.BestsellerEntity
import com.rick.data.database_book.model.BestsellerRemoteKeys
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class BestsellerRemoteMediator(
    private val bestsellerDao: BestsellerDao,
    private val keysDao: BestsellerRemoteKeysDao,
    private val network: BestsellerNetworkDataSource,
    private val apiKey: String,
    private val bookGenre: String,
    private val date: String,
) : RemoteMediator<Int, BestsellerEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, BestsellerEntity>
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
            val response =
                network.fetchBestsellers(apiKey = apiKey, bookGenre = bookGenre, date = date)
            val books = response.results.books
            val endOfPaginationReached = books.isEmpty()


            if (loadType == LoadType.REFRESH) {
                keysDao.clearRemoteKeys()
                bestsellerDao.clearBestsellers()
            }
            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = books.map {
                BestsellerRemoteKeys(
                    id = it.rank.toString(), prevKey = prevKey, nextKey = nextKey
                )
            }
            keysDao.insertAll(keys)
            bestsellerDao.upsertBestsellers(books.map(BestsellerNetwork::asBestsellerEntity))

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, BestsellerEntity>): BestsellerRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { book ->
            // Get the remote keys of the last item retrieved
            keysDao.remoteKeysId(book.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, BestsellerEntity>): BestsellerRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { book ->
                // GEt the remote keys of the first items retrieved
                keysDao.remoteKeysId(book.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, BestsellerEntity>): BestsellerRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { book ->
                keysDao.remoteKeysId(book)
            }
        }
    }
}