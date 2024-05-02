package com.rick.movie.data_movie.data.repository.article

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.data.database_movie.dao.ArticleDao
import com.rick.data.database_movie.dao.ArticleRemoteKeysDao
import com.rick.data.database_movie.model.ArticleEntity
import com.rick.data.database_movie.model.ArticleRemoteKeys
import com.rick.movie.data_movie.data.model.asArticleEntity
import com.rick.movie.data_movie.network.ArticleNetworkDataSource
import com.rick.movie.data_movie.network.model.ArticleNetwork
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val network: ArticleNetworkDataSource,
    private val articleDao: ArticleDao,
    private val keysDao: ArticleRemoteKeysDao,
    private val key: String
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, ArticleEntity>
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
            // val response = api.fetchMovieCatalog(offset = offset, apikey = key).toMovieCatalog()
            val response = network.fetchMovieArticles(page = page, apikey = key)
//            val movies = response.movieCatalog
//            movies.forEach {
//                it.id = count++
//            }
            val articles = response.response.docs
//            offset += 20
            val endOfPaginationReached = articles.isEmpty()

            if (loadType == LoadType.REFRESH) {
                keysDao.clearRemoteKeys()
            }

            val offset = response.response.meta.offset

            val prevKey = if (page == STARTING_PAGE_INDEX) null else (offset / 10) - 1
            val nextKey = if (endOfPaginationReached) null else {
                when (offset) {
                    0 -> {
                        1
                    }

                    else -> {
                        (offset / 10) + 1
                    }
                }

            }
            val keys = articles.map {
                ArticleRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
            }
            keysDao.insertAll(keys)
//            val sortedArticles = articles.sortedBy { it.pubDate }
            articleDao.upsertArticles(articles.map(ArticleNetwork::asArticleEntity))

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): ArticleRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { doc ->
            // Get the remote keys of the last item retrieved
            keysDao.remoteKeysId(doc.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ArticleEntity>): ArticleRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { doc ->
            // GEt the remote keys of the first items retrieved
            keysDao.remoteKeysId(doc.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ArticleEntity>): ArticleRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { doc ->
                keysDao.remoteKeysId(doc)
            }
        }
    }

    companion object {
        private const val STARTING_PAGE_INDEX = 0
        private const val TAG = "ArticleRemoteMediator"
    }

}