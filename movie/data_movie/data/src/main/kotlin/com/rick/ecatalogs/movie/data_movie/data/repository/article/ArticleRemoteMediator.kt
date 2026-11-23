package com.rick.ecatalogs.movie.data_movie.data.repository.article

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleDao
import com.rick.ecatalogs.movie.data_movie.database.dao.ArticleRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleEntity
import com.rick.ecatalogs.movie.data_movie.database.model.ArticleRemoteKeys
import com.rick.ecatalogs.movie.data_movie.network.ArticleNetworkDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val network: ArticleNetworkDataSource,
    private val articleDao: ArticleDao,
    private val keysDao: ArticleRemoteKeysDao,
    private val transactionRunner: MovieTransactionRunner,
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
                remoteKeys?.nextKey?.minus(1) ?: ARTICLE_FIRST_PAGE
            }
        }

        try {
            val response = network.fetchMovieArticles(page = page)
            val endOfPaginationReached = writeArticlePage(
                page = page,
                response = response,
                isRefresh = loadType == LoadType.REFRESH,
                articleDao = articleDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
            )

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
        private const val TAG = "ArticleRemoteMediator"
    }

}