package com.rick.ecatalogs.anime.data_anime.data.repository.manga

import com.rick.ecatalogs.anime.data_anime.data.repository.catalogPosition
import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.anime.data_anime.data.model.asEntity
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaDao
import com.rick.ecatalogs.anime.data_anime.database.dao.MangaRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.MangaEntity
import com.rick.ecatalogs.anime.data_anime.database.model.MangaRemoteKeys
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MangaRemoteMediator(
    private val mangaDao: MangaDao,
    private val keysDao: MangaRemoteKeysDao,
    private val network: JikanNetworkDataSource,
    private val transactionRunner: JikanTransactionRunner,
    private val listCache: ListCache,
    /** Rows a refresh keeps although they leave the list: the favorites. */
    private val favoriteIds: suspend () -> Set<Int>,
) : RemoteMediator<Int, MangaEntity>() {


    /**
     * Show the list stored on the phone while it's younger than the sync interval (no download
     * when opening the screen); otherwise refresh it. Retrying from the screen always downloads.
     */
    override suspend fun initialize(): InitializeAction =
        if (listCache.isFresh(CachedList.MANGA)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MangaEntity>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
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
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
        }

        try {
            val response = network.fetchTopManga(page)
            val mangas = response.data
            val endOfPaginationReached = response.data.isEmpty()

            // Read before the transaction: favorites live in DataStore, not in Room
            val keepIds = if (loadType == LoadType.REFRESH) favoriteIds() else emptySet()
            transactionRunner {
                if (loadType == LoadType.REFRESH) {
                    keysDao.clearRemoteKeys()
                    // Written anew: rows leave the list, and those that aren't favorites are deleted
                    mangaDao.clearCatalog()
                    mangaDao.deleteOutsideCatalog(keepIds)
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = mangas.map {
                    MangaRemoteKeys(
                        manga = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                keysDao.insertAll(keys)
                mangaDao.upsertManga(
                    mangas.mapIndexed { index, item ->
                        item.asEntity().copy(catalogPosition = catalogPosition(page, index))
                    },
                )
            }

            if (loadType == LoadType.REFRESH) listCache.markRefreshed(CachedList.MANGA)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MangaEntity>): MangaRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { anime ->
                // Get the remote keys of the last item retrieved
                keysDao.remoteKeysId(anime.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MangaEntity>): MangaRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { anime ->
                // GEt the remote keys of the first items retrieved
                keysDao.remoteKeysId(anime.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MangaEntity>): MangaRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { anime ->
                keysDao.remoteKeysId(anime)
            }
        }
    }

}

private const val STARTING_PAGE_INDEX = 1
