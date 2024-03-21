package com.rick.data_book.nytimes

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rick.data_book.BookDatabase
import com.rick.data_book.nytimes.model.NYBook
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class NYBookRemoteMediator(
    private val api: NYTimesAPI,
    private val db: BookDatabase,
    private val key: String,
    private val bookGenre: String
) : RemoteMediator<Int, NYBook>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, NYBook>): MediatorResult {
        try {
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

            val response =
                api.getBestsellers(bookGenre = bookGenre, apiKey = key, page = 3)
            val bestsellers = response.results.books

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.nyRemoteKeysDao.clearRemoteKeys()
                    db.bookDao.clearBestsellers()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (bestsellers.isEmpty()) null else page + 1
                Log.e("TAGGG", "NEXTKEY огые фзш api call:$nextKey")
                val keys = bestsellers.map {
                    NYBookRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                db.nyRemoteKeysDao.insertAll(keys)
                db.bookDao.insertBestsellers(bestsellers)
            }

            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, NYBook>): NYBookRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { book ->
                // Get the remote keys of the last item retrieved
                db.nyRemoteKeysDao.remoteKeysId(book.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, NYBook>): NYBookRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { book ->
                // GEt the remote keys of the first items retrieved
                db.nyRemoteKeysDao.remoteKeysId(book.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, NYBook>): NYBookRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { book ->
                db.nyRemoteKeysDao.remoteKeysId(book)
            }
        }
    }
}