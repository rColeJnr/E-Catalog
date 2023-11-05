package com.rick.data_movie

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.core.Resource
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.ny_times.MovieCatalogApi
import com.rick.data_movie.ny_times.MovieCatalogRemoteMediator
import com.rick.data_movie.ny_times.article_models.Doc
import com.rick.data_movie.tmdb.TMDBApi
import com.rick.data_movie.tmdb.movie.MovieResponse
import com.rick.data_movie.tmdb.trending_movie.MovieRemoteMediator
import com.rick.data_movie.tmdb.trending_movie.TrendingMovie
import com.rick.data_movie.tmdb.trending_tv.TrendingTv
import com.rick.data_movie.tmdb.trending_tv.TvRemoteMediator
import com.rick.data_movie.tmdb.tv.TvResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val PAGE_SIZE = 20

class MovieCatalogRepository @Inject constructor(
    private val db: MovieCatalogDatabase,
    private val nyApi: MovieCatalogApi,
    private val tmdbApi: TMDBApi,
) {

    // TODO This could and should be separated into use cases in the next clean up branch

    fun getMovies(key: String): Flow<PagingData<Doc>> {

        val pagingSourceFactory = { db.articleDao.getMovieArticles() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 1
            ),
            remoteMediator = MovieCatalogRemoteMediator(nyApi, db, key),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
    fun getTrendingMovie(key: String): Flow<PagingData<TrendingMovie>> {

        val pagingSourceFactory = { db.tmdbDao.getTrendingMovie() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 1
            ),
            remoteMediator = MovieRemoteMediator(tmdbApi, db, key),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
    fun getTrendingTv(key: String): Flow<PagingData<TrendingTv>> {

        val pagingSourceFactory = { db.tmdbDao.getTrendingTv() }
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 1
            ),
            remoteMediator = TvRemoteMediator(tmdbApi, db, key),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getTmdbMovie(id: Int, key: String): Flow<Resource<MovieResponse>> {
        return flow {
            emit(Resource.Loading(true))
            val movie = db.tmdbDao.getMovie(id)
            if (movie != null) {
                emit(Resource.Success(data = movie))
                emit(Resource.Loading(false))
            } else {
                try {
                    val apiResponse = tmdbApi.getMovie(id = id, apikey = key)
                    db.tmdbDao.insertMovie(apiResponse)
                    emit(Resource.Success(data = apiResponse))
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

    fun getTmdbTv(id: Int, key: String): Flow<Resource<TvResponse>> {
        return flow {
            emit(Resource.Loading(true))
            val tv = db.tmdbDao.getTv(id)
            if (tv != null) {
                emit(Resource.Success(data = tv))
                emit(Resource.Loading(false))
            } else {
                try {
                    val apiResponse = tmdbApi.getTv(id = id, apikey = key)
                    db.tmdbDao.insertTv(apiResponse)
                    emit(Resource.Success(data = apiResponse))
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

    suspend fun insertTmdbMovie(movie: MovieResponse) {
        db.tmdbDao.insertMovie(movie)
    }

    suspend fun insertTmdbTv(tv: TvResponse) {
        db.tmdbDao.insertTv(tv)
    }

    suspend fun getFavoriteMovies(): Flow<Resource<List<Favorite>>> {
        return flow {

            emit(Resource.Loading(true))

            val favMovies: List<Favorite> = db.favoriteDao.getFavoriteMovies()
            emit(Resource.Success(favMovies))
            emit(Resource.Loading(false))

        }
    }

    suspend fun getFavoriteSeries(): Flow<Resource<List<Favorite>>> {
        return flow {

            emit(Resource.Loading(true))

            val favSeries: List<Favorite> = db.favoriteDao.getFavoriteSeries()
            emit(Resource.Success(favSeries))
            emit(Resource.Loading(false))
        }
    }

    suspend fun insertFavorite(favorite: Favorite) {
        db.favoriteDao.insertFavorite(favorite)
    }

    suspend fun deleteFavorite(favorite: Favorite) {
        db.favoriteDao.deleteFavorite(favorite)
    }

//    suspend fun searchSeries(
//        apiKey: String,
//        query: String
//    ): Flow<Resource<List<IMDBSearchResult>>> {
//        var data: List<IMDBSearchResult> = listOf()
//        // appending '%' so we can allow other characters to be before and after the query string
//        val dbQuery = "%${query.replace(' ', '%')}%"
//        return flow {
//
//            emit(Resource.Loading(true))
//            db.withTransaction {
//                data = db.imdbSearchDao.seriesByTitle(dbQuery)
//            }
//            if (data.isNotEmpty()) {
//                emit(
//                    Resource.Success<List<IMDBSearchResult>>(
//                        data = data
//                    )
//                )
//                emit(Resource.Loading(false))
//            }
//            try {
//                val apiResponse = imdbApi.searchSeries(apiKey = apiKey, title = query)
//                if (apiResponse.errorMessage.isEmpty()) {
//                    db.withTransaction {
//                        db.imdbSearchDao.insertAll(apiResponse.results)
//                        data = db.imdbSearchDao.seriesByTitle(dbQuery)
//                    }
//                    emit(
//                        Resource.Success<List<IMDBSearchResult>>(
//                            data = data
//                        )
//                    )
//                    emit(Resource.Loading(false))
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                } else {
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                    emit(Resource.Loading(false))
//                }
//            } catch (e: IOException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//
//        }
//    }
//
//    suspend fun getMovieOrSeries(
//        apiKey: String,
//        id: String
//    ): Flow<Resource<IMDBMovie>> {
//
//        var data: IMDBMovie? = null
//
//        return flow {
//            emit(Resource.Loading(true))
//            db.withTransaction {
//                data = db.imdbMovieAndSeriesDao.movieById(id)
//            }
//
//            if (data != null) {
//                emit(Resource.Success<IMDBMovie>(data = data))
//                emit(Resource.Loading(false))
//            }
//            try {
//                val apiResponse = imdbApi.getMovieOrSeries(apiKey = apiKey, id = id).toImdbMovie()
//                if (apiResponse.errorMessage.isNullOrEmpty()) {
//                    db.withTransaction {
//                        db.imdbMovieAndSeriesDao.insert(apiResponse)
//                        data = db.imdbMovieAndSeriesDao.movieById(id)
//                    }
//                    emit(Resource.Success<IMDBMovie>(data = data))
//                    emit(Resource.Loading(false))
//                } else {
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                    emit(Resource.Loading(false))
//                }
//            } catch (e: IOException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//        }
//    }
//
//    suspend fun searchMovies(
//        apiKey: String,
//        query: String
//    ): Flow<Resource<List<IMDBSearchResult>>> {
//        var data: List<IMDBSearchResult> = listOf()
//        // appending '%' so we can allow other characters to be before and after the query string
//        val dbQuery = "%${query.replace(' ', '%')}%"
//        return flow {
//
//            emit(Resource.Loading(true))
//            db.withTransaction {
//                data = db.imdbSearchDao.movieByTitle(dbQuery)
//            }
//            if (data.isNotEmpty()) {
//                emit(
//                    Resource.Success<List<IMDBSearchResult>>(
//                        data = data
//                    )
//                )
//                emit(Resource.Loading(false))
//            }
//            try {
//                val apiResponse = imdbApi.searchMovies(apiKey = apiKey, title = query)
//                if (apiResponse.errorMessage.isEmpty()) {
//                    db.withTransaction {
//                        db.imdbSearchDao.insertAll(apiResponse.results)
//                        data = db.imdbSearchDao.movieByTitle(dbQuery)
//                    }
//                    emit(
//                        Resource.Success<List<IMDBSearchResult>>(
//                            data = data
//                        )
//                    )
//                    emit(Resource.Loading(false))
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                } else {
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                    emit(Resource.Loading(false))
//                }
//            } catch (e: IOException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//
//        }
//    }
//
//    suspend fun searchExactMatch(
//        apiKey: String,
//        query: String
//    ): Flow<Resource<List<IMDBSearchResult>>> {
//        var data: List<IMDBSearchResult> = listOf()
//        return flow {
//
//            emit(Resource.Loading(true))
//            db.withTransaction {
//                data = db.imdbSearchDao.movieByTitle(query)
//            }
//            if (data.isNotEmpty()) {
//                emit(
//                    Resource.Success<List<IMDBSearchResult>>(
//                        data = data
//                    )
//                )
//                emit(Resource.Loading(false))
//            }
//            try {
//                val apiResponse = imdbApi.searchMovies(apiKey = apiKey, title = query)
//                if (apiResponse.errorMessage.isEmpty()) {
//                    db.withTransaction {
//                        db.imdbSearchDao.insertAll(apiResponse.results)
//                        data = db.imdbSearchDao.movieByTitle(query)
//                    }
//                    emit(
//                        Resource.Success<List<IMDBSearchResult>>(
//                            data = data
//                        )
//                    )
//                    emit(Resource.Loading(false))
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                } else {
//                    emit(Resource.Error(message = apiResponse.errorMessage))
//                    emit(Resource.Loading(false))
//                }
//            } catch (e: IOException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//        }
//    }
//
//    suspend fun getTvSeries(apiKey: String): Flow<Resource<List<TvSeries>>> {
//        return flow {
//            emit(Resource.Loading(true))
//
//            var tvSeries: List<TvSeries>? = null
//            db.withTransaction {
//                tvSeries = db.seriesDao.getTvSeries()
//            }
//            if (!tvSeries.isNullOrEmpty()) {
//                emit(Resource.Success<List<TvSeries>>(data = tvSeries))
//                emit(Resource.Loading(false))
//            }
//            try {
//                val apiResponse = imdbApi.getPopularTvSeries(apiKey = apiKey).toTvSeriesResponse()
//                db.withTransaction {
//                    db.seriesDao.insertTvSeries(apiResponse.tvSeries)
//                    tvSeries = db.seriesDao.getTvSeries()
//                }
//
//                emit(Resource.Success<List<TvSeries>>(data = tvSeries))
//                emit(Resource.Loading(false))
//            } catch (e: IOException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            } catch (e: HttpException) {
//                emit(Resource.Error(e.message))
//                emit(Resource.Loading(false))
//            }
//        }
//    }

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