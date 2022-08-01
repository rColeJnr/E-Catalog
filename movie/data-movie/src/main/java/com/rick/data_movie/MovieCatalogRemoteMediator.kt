package com.rick.data_movie

import androidx.paging.*
import androidx.room.withTransaction
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieCatalogRemoteMediator(
    private val api: MovieCatalogApi,
    private val db: MovieCatalogDatabase
): RemoteMediator<Int, MovieCatalogEntity>() {

    private val STARTING_PAGE_INDEX = 1
    private val LOAD_DELAY_MILLIS = 3_000L
    /*
        network items count, refer to documentation at https://developer.nytimes.com/docs/movie-reviews-api/1/routes/reviews/search.json/get
     */
    private val ITEMS_PER_PAGE = 20
    private var offset = 20

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieCatalogEntity>
    ): MediatorResult {

        val page = when(loadType) {
            LoadType.REFRESH -> TODO()
            LoadType.PREPEND -> TODO()
            LoadType.APPEND -> TODO()
        }

        val position = params.key ?: STARTING_PAGE_INDEX
        try {
            val response = api.fetchMovieCatalog(offset).toMovieCatalog()
            offset+=20
            val movies = response.results
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
                    RemoteKeys(movieId = it.i)
                }
            }
//            val nextKey = if (movies.isEmpty()) null
//            else {
//                position + offset / ITEMS_PER_PAGE
//            }
//            PagingSource.LoadResult.Page(
//                data = movies,
//                prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
//                nextKey = nextKey
//            )
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}