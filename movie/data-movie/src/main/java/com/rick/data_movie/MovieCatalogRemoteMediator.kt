package com.rick.data_movie

import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

/*
    network items count, refer to documentation at https://developer.nytimes.com/docs/movie-reviews-api/1/routes/reviews/search.json/get
 */
private val ITEMS_PER_PAGE = 20
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
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
            LoadType.PREPEND -> {

            }
            LoadType.REFRESH -> {

            }
        }

        try {
            val response = api.fetchMovieCatalog(offset).toMovieCatalog()
            offset+=20
            val movies = response.movieCatalog
            val endOfPaginationReached = movies.isEmpty()
            db.withTransaction {
                // claer all tables in the database
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao.clearRemoteKeys()
                    db.moviesDao.clearMovies()
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = movies.map {
                    RemoteKeys(movieId = it.id, prevKey, nextKey)
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
                db.remoteKeysDao.remoteKeysMovieId(movie.id)
            }
    }

}