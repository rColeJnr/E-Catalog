package com.rick.data_movie.ny_times

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rick.data_movie.MovieCatalogDatabase
import com.rick.data_movie.ny_times.article_models.Doc
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieCatalogRemoteMediator(
    private val api: MovieCatalogApi,
    private val db: MovieCatalogDatabase,
    private val key: String
): RemoteMediator<Int, Doc>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Doc>
    ): MediatorResult {

        val page = when(loadType) {
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
                remoteKeys?.nextKey?.minus(1) ?: Companion.STARTING_PAGE_INDEX
            }
        }

        try {
            // val response = api.fetchMovieCatalog(offset = offset, apikey = key).toMovieCatalog()
            val response = api.fetchMovieArticles(page = page, apikey = key)
//            val movies = response.movieCatalog
//            movies.forEach {
//                it.id = count++
//            }
            val articles = response.response.docs
//            offset += 20
            val endOfPaginationReached = articles.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao.clearRemoteKeys()
                    db.articleDao.clearArticles()
//                    offset = 20
//                    movies = emptyList()
                }
                val prevKey = if (page == Companion.STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = articles.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                db.remoteKeysDao.insertAll(keys)
                db.articleDao.insertArticles(articles)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }  catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Doc>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { doc ->
                // Get the remote keys of the last item retrieved
                db.remoteKeysDao.remoteKeysMovieId(doc.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Doc>): RemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { doc ->
                // GEt the remote keys of the first items retrieved
                db.remoteKeysDao.remoteKeysMovieId(doc.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Doc>): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { doc ->
                db.remoteKeysDao.remoteKeysMovieId(doc)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
    }

}