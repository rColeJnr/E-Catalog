package com.rick.data_movie.tmdb.trending_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rick.data_movie.MovieCatalogDatabase
import com.rick.data_movie.tmdb.TMDBApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val api: TMDBApi,
    private val db: MovieCatalogDatabase,
    private val key: String,
): RemoteMediator<Int, TrendingMovie>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TrendingMovie>
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
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
        }

        try {
            val response = api.getTrendingMovie(
                page = page,
                apikey = key,
            )
            val movies = response.results
            val endOfPaginationReached = movies.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.tmdbDao.clearMovies()
                    db.movieRemoteKeys.clearRemoteKeys()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = movies.map {
                    MovieRemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                db.movieRemoteKeys.insertAll(keys)
                db.tmdbDao.insertMovies(movies)
            }
            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TrendingMovie>): MovieRemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { movie ->
                // Get the remote keys of the last item retrieved
                db.movieRemoteKeysDao.remoteKeysMovieId(movie.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, TrendingMovie>): MovieRemoteKeys? {
        // GEt the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull() { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { movie ->
                // GEt the remote keys of the first items retrieved
                db.movieRemoteKeysDao.remoteKeysMovieId(movie.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, TrendingMovie>): MovieRemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { movie ->
                db.movieRemoteKeysDao.remoteKeysMovieId(movie)
            }
        }
    }

}