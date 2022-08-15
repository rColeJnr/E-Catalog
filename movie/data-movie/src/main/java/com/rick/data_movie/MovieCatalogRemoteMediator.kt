package com.rick.data_movie

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 0

private var offset = 20

@OptIn(ExperimentalPagingApi::class)
class MovieCatalogRemoteMediator(
    private val api: MovieCatalogApi,
    private val db: MovieCatalogDatabase
): RemoteMediator<Int, Movie>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Movie>
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
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
        }

        try {
            val response = api.fetchMovieCatalog(offset).toMovieCatalog()
            offset+=20
            val movies = response.movieCatalog
            val endOfPaginationReached = movies.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao.clearRemoteKeys()
                    db.moviesDao.clearMovies()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = movies.map {
                    RemoteKeys(movie = it.title, prevKey = prevKey, nextKey = nextKey)
                }
                db.remoteKeysDao.insertAll(keys)
                db.moviesDao.insertMovies(movies)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        }  catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Movie>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                Log.e("TAG", "Key - ${db.remoteKeysDao.remoteKeysMovieId(movie.title)}, last item")
                db.remoteKeysDao.remoteKeysMovieId(movie.title)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Movie>): RemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                // GEt the remote keys of the first items retrieved
                Log.e("TAG", "Key - ${db.remoteKeysDao.remoteKeysMovieId(movie.title)}, first item")
                db.remoteKeysDao.remoteKeysMovieId(movie.title)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Movie>): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.title?.let { movie ->
                Log.e("TAG", "Key - ${db.remoteKeysDao.remoteKeysMovieId(movie)}, refresh")
                db.remoteKeysDao.remoteKeysMovieId(movie)
            }
        }
    }

}