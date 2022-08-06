package com.rick.data_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20
class MovieCatalogRepository @Inject constructor(
    private val api: MovieCatalogApi,
    private val db: MovieCatalogDatabase,
) {
//
//    private val dao = db.moviesDao
//    suspend fun getMovieCatalog(offset: Int): Flow<Resource<MovieCatalog>> {
//        return flow {
//            emit(Resource.Loading(true))
//
//            val localMovieCatalog = dao.getMovies()
//
//            if (localMovieCatalog.isNotEmpty()) {
//                emit(
//                    Resource.Success(
//                        data = localMovieCatalog.map { it.toMovieCatalog() }.first()
//                    )
//                )
//                emit(Resource.Loading(false))
//            }
//            try {
//                val remoteMovieCatalog = api.fetchMovieCatalog(offset, QUERY_ORDER)
//
//                dao.clearMovies()
//                dao.insertMovies(remoteMovieCatalog.toMovieCatalogEntity())
//
//                emit(
//                    Resource.Success(
//                        data = dao.getMovies().map { it.toMovieCatalog() }.first()
//                    )
//                )
//            } catch (e: IOException) {
//                e.printStackTrace()
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                e.printStackTrace()
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//        }
//    }

    fun getMovies(): Flow<PagingData<Movie>> {

        val pagingSourceFactory = { db.moviesDao.getMovies() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            remoteMediator = MovieCatalogRemoteMediator(api, db),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}