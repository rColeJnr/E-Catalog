package com.rick.data_anime

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rick.data_anime.model_jikan.Jikan
import com.rick.data_anime.model_jikan.toJikanResponse
import okio.IOException
import retrofit2.HttpException

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class AnimeRemoteMediator(
    private val api: JikanApi,
    private val db: JikanDatabase
) : RemoteMediator<Int, Jikan>() {

    override suspend fun initialize(): InitializeAction {
        // Launch remote refresh as soon as paging starts and do not trigger remote prepend or
        // append until refresh has succeeded. In cases where we don't mind showing out-of-date,
        // cached offline data, we can return SKIP_INITIAL_REFRESH instead to prevent paging
        // triggering remote refresh.
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Jikan>): MediatorResult {

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
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
        }

        try {
            val response = api.fetchTopAnime(page).toJikanResponse()
            val animes = response.data
            val endOfPaginationReached = response.data.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.animeRemoteKeysDao.clearRemoteKeys()
                    db.jikanDao.clearAnime()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = animes.map {
                    AnimeRemoteKeys(anime = it.malId, prevKey = prevKey, nextKey = nextKey)
                }
                db.animeRemoteKeysDao.insertAll(keys)
                db.jikanDao.insertJikan(animes)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Jikan>): AnimeRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { anime ->
                // Get the remote keys of the last item retrieved
                db.animeRemoteKeysDao.remoteKeysId(anime.malId)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Jikan>): AnimeRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { anime ->
                // GEt the remote keys of the first items retrieved
                db.animeRemoteKeysDao.remoteKeysId(anime.malId)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Jikan>): AnimeRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.malId?.let { anime ->
                db.animeRemoteKeysDao.remoteKeysId(anime)
            }
        }
    }

}