package com.rick.data.anime_favorite.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data.anime_favorite.model.asEntity
import com.rick.data.database_anime.dao.AnimeDao
import com.rick.data.database_anime.dao.AnimeRemoteKeysDao
import com.rick.data.database_anime.model.AnimeEntity
import com.rick.data.database_anime.model.AnimeRemoteKeys
import com.rick.data.network_anime.JikanNetworkDataSource
import com.rick.data.network_anime.model.AnimeNetwork
import okio.IOException
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class AnimeRemoteMediator(
    private val animeDao: AnimeDao,
    private val keysDao: AnimeRemoteKeysDao,
    private val network: JikanNetworkDataSource
) : RemoteMediator<Int, AnimeEntity>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
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
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
        }

        try {
            val response = network.fetchTopAnime(page)
            val animes = response.data
            val endOfPaginationReached = response.pagination.hasNextPage

            if (loadType == LoadType.REFRESH) {
                keysDao.clearRemoteKeys()
                animeDao.clearAnime()
            }
            val prevKey =
                if (page == STARTING_PAGE_INDEX) null else response.pagination.currentPage - 1
            val nextKey = if (endOfPaginationReached) null else response.pagination.currentPage + 1
            val keys = animes.map {
                AnimeRemoteKeys(
                    anime = it.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
            keysDao.insertAll(keys)
            animeDao.upsertAnime(animes.map(AnimeNetwork::asEntity))

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

private const val STARTING_PAGE_INDEX = 1