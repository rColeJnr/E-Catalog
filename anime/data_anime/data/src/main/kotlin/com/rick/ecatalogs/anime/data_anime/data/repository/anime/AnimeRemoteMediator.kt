package com.rick.ecatalogs.anime.data_anime.data.repository.anime

import com.rick.ecatalogs.data.datastore.CachedList
import com.rick.ecatalogs.data.datastore.ListCache
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.anime.data_anime.database.JikanTransactionRunner
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeDao
import com.rick.ecatalogs.anime.data_anime.database.dao.AnimeRemoteKeysDao
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeEntity
import com.rick.ecatalogs.anime.data_anime.database.model.AnimeRemoteKeys
import com.rick.ecatalogs.anime.data_anime.network.JikanNetworkDataSource
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class AnimeRemoteMediator(
    private val animeDao: AnimeDao,
    private val keysDao: AnimeRemoteKeysDao,
    private val network: JikanNetworkDataSource,
    private val transactionRunner: JikanTransactionRunner,
    private val listCache: ListCache,
    /** Rows a refresh keeps although they leave the list: the favorites. */
    private val favoriteIds: suspend () -> Set<Int>,
) : RemoteMediator<Int, AnimeEntity>() {

    /**
     * Show the list stored on the phone while it's younger than the sync interval (no download
     * when opening the screen); otherwise refresh it. Retrying from the screen always downloads.
     */
    override suspend fun initialize(): InitializeAction =
        if (listCache.isFresh(CachedList.ANIME)) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeEntity>
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
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: ANIME_FIRST_PAGE
            }
        }

        try {
            val response = network.fetchTopAnime(page)
            // Read before the transaction: favorites live in DataStore, not in Room
            val keepIds = if (loadType == LoadType.REFRESH) favoriteIds() else emptySet()
            val endOfPaginationReached = writeAnimePage(
                page = page,
                items = response.data,
                isRefresh = loadType == LoadType.REFRESH,
                animeDao = animeDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
                keepIds = keepIds,
            )

            if (loadType == LoadType.REFRESH) listCache.markRefreshed(CachedList.ANIME)
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, AnimeEntity>): AnimeRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { anime ->
                // Get the remote keys of the last item retrieved
                keysDao.remoteKeysId(anime.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, AnimeEntity>): AnimeRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { anime ->
                // GEt the remote keys of the first items retrieved
                keysDao.remoteKeysId(anime.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, AnimeEntity>): AnimeRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { anime ->
                keysDao.remoteKeysId(anime)
            }
        }
    }

}
