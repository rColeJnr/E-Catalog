package com.rick.data_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.rick.core.Resource
import com.rick.data_movie.imdb.IMDBApi
import com.rick.data_movie.imdb.movie_model.IMDBMovie
import com.rick.data_movie.imdb.movie_model.toImdbMovie
import com.rick.data_movie.imdb.search_model.IMDBSearchResult
import com.rick.data_movie.imdb.series_model.TvSeriesResponse
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

    // TODO This could be separated into use cases in the next clean up branch

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

    suspend fun searchSeries(
        apiKey: String,
        query: String
    ): Flow<Resource<List<IMDBSearchResult>>> = flow {
        emit(Resource.Loading(true))

        try {
            val apiResponse = imdbApi.searchSeries(apiKey = apiKey, title = query)
            db.withTransaction {
                if (apiResponse.errorMessage.isEmpty()) {
                    db.imdbSearchDao.insertAll(apiResponse.results)
                    emit(
                        Resource.Success<List<IMDBSearchResult>>(
                            data = db.imdbSearchDao.seriesByTitle(queryString = query)
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

    suspend fun getMovieOrSeries(
        apiKey: String,
        id: String
    ): Flow<Resource<IMDBMovie>> {

        var data: IMDBMovie? = null

        return flow {
            emit(Resource.Loading(true))
            db.withTransaction {
                data = db.imdbMovieAndSeriesDao.movieById(id)
            }

            if (data != null) {
                emit(Resource.Success<IMDBMovie>(data = data))
                emit(Resource.Loading(false))
            }
            try {
                val apiResponse = imdbApi.getMovieOrSeries(apiKey = apiKey, id = id).toImdbMovie()
                if (apiResponse.errorMessage == null) {
                    db.withTransaction {
                        db.imdbMovieAndSeriesDao.insert(apiResponse)
                        data = db.imdbMovieAndSeriesDao.movieById(id)
                    }
                    emit(Resource.Success<IMDBMovie>(data = data))
                    emit(Resource.Loading(false))
                } else {
                    emit(Resource.Error(message = apiResponse.errorMessage))
                    emit(Resource.Loading(false))
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

    suspend fun searchMovies(
        apiKey: String,
        query: String
    ): Flow<Resource<List<IMDBSearchResult>>> {
        var data: List<IMDBSearchResult> = listOf()
        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        return flow {

            emit(Resource.Loading(true))
            db.withTransaction {
                data = db.imdbSearchDao.movieByTitle(dbQuery)
            }
            if (data.isNotEmpty()) {
                emit(
                    Resource.Success<List<IMDBSearchResult>>(
                        data = data
                    )
                )
                emit(Resource.Loading(false))
            }
            try {
                val apiResponse = imdbApi.searchMovies(apiKey = apiKey, title = query)
                if (apiResponse.errorMessage.isEmpty()) {
                    db.withTransaction {
                        db.imdbSearchDao.insertAll(apiResponse.results)
                        data = db.imdbSearchDao.movieByTitle(dbQuery)
                    }
                    emit(
                        Resource.Success<List<IMDBSearchResult>>(
                            data = data
                        )
                    )
                    emit(Resource.Loading(false))
                    emit(Resource.Error(message = apiResponse.errorMessage))
                } else {
                    emit(Resource.Error(message = apiResponse.errorMessage))
                    emit(Resource.Loading(false))
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

    suspend fun searchExactMatch(
        apiKey: String,
        query: String
    ): Flow<Resource<List<IMDBSearchResult>>> {
        var data: List<IMDBSearchResult> = listOf()
        return flow {

            emit(Resource.Loading(true))
            db.withTransaction {
                data = db.imdbSearchDao.movieByTitle(query)
            }
            if (data.isNotEmpty()) {
                emit(
                    Resource.Success<List<IMDBSearchResult>>(
                        data = data
                    )
                )
                emit(Resource.Loading(false))
            }
            try {
                val apiResponse = imdbApi.searchMovies(apiKey = apiKey, title = query)
                if (apiResponse.errorMessage.isEmpty()) {
                    db.withTransaction {
                        db.imdbSearchDao.insertAll(apiResponse.results)
                        data = db.imdbSearchDao.movieByTitle(query)
                    }
                    emit(
                        Resource.Success<List<IMDBSearchResult>>(
                            data = data
                        )
                    )
                    emit(Resource.Loading(false))
                    emit(Resource.Error(message = apiResponse.errorMessage))
                } else {
                    emit(Resource.Error(message = apiResponse.errorMessage))
                    emit(Resource.Loading(false))
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

    suspend fun getTvSeries(apiKey: String): Flow<Resource<TvSeriesResponse>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val apiResponse = imdbApi.getPopularTvSeries(apiKey = apiKey).toTvSeriesResponse()
                emit(Resource.Success<TvSeriesResponse>(data = apiResponse))
                emit(Resource.Loading(false))
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