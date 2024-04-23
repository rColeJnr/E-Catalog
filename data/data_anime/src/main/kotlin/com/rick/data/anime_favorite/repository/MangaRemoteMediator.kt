package com.rick.data.anime_favorite.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data.anime_favorite.model.asEntity
import com.rick.data.database_anime.dao.MangaDao
import com.rick.data.database_anime.dao.MangaRemoteKeysDao
import com.rick.data.database_anime.model.MangaEntity
import com.rick.data.database_anime.model.MangaRemoteKeys
import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.model.MangaNetwork
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MangaRemoteMediator(
    private val mangaDao: MangaDao,
    private val keysDao: MangaRemoteKeysDao,
    private val network: JikanNetworkDataSource
) : RemoteMediator<Int, MangaEntity>() {


    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
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

            if (loadType == LoadType.REFRESH) {
                keysDao.clearRemoteKeys()
                mangaDao.clearManga()
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
            mangaDao.upsertManga(mangas.map(MangaNetwork::asEntity))

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
