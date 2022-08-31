package com.rick.data_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.rick.core.Resource
import com.rick.data_movie.imdb.IMDBApi
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.data_movie.ny_times.Movie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val ITEMS_PER_PAGE = 20

class MovieCatalogRepository @Inject constructor(
    private val db: MovieCatalogDatabase,
    private val nyApi: MovieCatalogApi,
    private val imdbApi: IMDBApi,
) {

    fun getMovies(key: String): Flow<PagingData<Movie>> {

        val pagingSourceFactory = { db.moviesDao.getMovies() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = ITEMS_PER_PAGE
            ),
            remoteMediator = MovieCatalogRemoteMediator(nyApi, db, key),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun searchMovies(apiKey: String, title: String): Flow<Resource<List<IMDBSearchResult>>> {

        return flow {
            emit(Resource.Loading(true))

            try {
                val apiResponse = imdbApi.searchMovies(apiKey = apiKey, title = title)
                db.withTransaction {
                    if (apiResponse.errorMessage.isEmpty()) {
                        db.searchedMoviesDao.insertAll(apiResponse.results)
                        emit(
                            Resource.Success<List<IMDBSearchResult>>(
                                data = db.searchedMoviesDao.movieByTitle(queryString = title)
                            )
                        )
                    } else {
                        emit(Resource.Error(message = apiResponse.errorMessage))
                        emit(Resource.Loading(false))
                    }
                }
            } catch (e: IOException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }
    }

    suspend fun searchSeries(apiKey: String, title: String): Flow<Resource<List<IMDBSearchResult>>> {

        return flow {
            emit(Resource.Loading(true))

            try {
                val apiResponse = imdbApi.searchSeries(apiKey = apiKey, title = title)
                db.withTransaction {
                    if (apiResponse.errorMessage.isEmpty()) {
                        db.searchedMoviesDao.insertAll(apiResponse.results)
                        emit(
                            Resource.Success<List<IMDBSearchResult>>(
                                data = db.searchedMoviesDao.seriesByTitle(queryString = title)
                            )
                        )
                    } else {
                        emit(Resource.Error(message = apiResponse.errorMessage))
                        emit(Resource.Loading(false))
                    }
                }
            } catch (e: IOException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            } catch (e: HttpException) {
                emit(Resource.Error(e.message))
                emit(Resource.Loading(false))
            }
        }
    }

}


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