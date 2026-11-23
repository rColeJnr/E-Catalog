package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingMovieRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.database.model.asTrendingMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie.TrendingMovie
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingMovieEntity
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingMovieNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class TrendingMovieRepositoryImpl @Inject constructor(
    private val trendingMovieDao: TrendingMovieDao,
    private val keysDao: TrendingMovieRemoteKeysDao,
    private val network: TmdbNetworkDataSource,
    private val transactionRunner: MovieTransactionRunner,
) : TrendingMovieRepository {
    override fun getTrendingMovies(): Flow<PagingData<TrendingMovieEntity>> {
        val pagingSourceFactory = { trendingMovieDao.getTrendingMovie() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = TrendingMovieRemoteMediator(
                network = network,
                trendingMovieDao = trendingMovieDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getTrendingMovieFavorites(favorites: Set<Int>): Flow<List<TrendingMovie>> =
        trendingMovieDao.getTrendingMovieWithFilters(
            filterById = true,
            filterIds = favorites
        )
            .map { trendingMovieEntities -> trendingMovieEntities.map { it.asTrendingMovie() } }

    override suspend fun syncFirstPage(): Boolean {
        val response = network.fetchTrendingMovies(page = TRENDING_MOVIE_FIRST_PAGE)
        val fetchedIds = response.results.map(TrendingMovieNetwork::id).toSet()
        if (fetchedIds.isEmpty() || fetchedIds == keysDao.firstPageIds().toSet()) return false

        writeTrendingMoviePage(
            page = TRENDING_MOVIE_FIRST_PAGE,
            response = response,
            isRefresh = true,
            trendingMovieDao = trendingMovieDao,
            keysDao = keysDao,
            transactionRunner = transactionRunner,
        )
        return true
    }

    override fun searchTrendingMovie(query: String): Flow<List<TrendingMovie>> =
        channelFlow {
            try {
                val response = network.searchMovie(
                    query = query,
                ).results

                trendingMovieDao.upsertTrendingMovie(response.map(TrendingMovieNetwork::asTrendingMovieEntity))

                // appending '%' so we can allow other characters to be before and after the query string
                val dbQuery = "%${query.replace(' ', '%')}%"
                trendingMovieDao.getTrendingMovieWithFilters(
                    filterByQuery = true,
                    query = dbQuery
                )
                    .collectLatest { articleEntities ->
                        send(
                            articleEntities.map(
                                TrendingMovieEntity::asTrendingMovie
                            )
                        )
                    }

            } catch (e: IOException) {
                Log.e(TAG, "IOException ${e.message}")
                Log.e(TAG, "IOException ${e.cause}")
                Log.e(TAG, "IOException ${e.printStackTrace()}")
                e.printStackTrace()
            } catch (e: HttpException) {
                Log.e(TAG, "HTTPException ${e.localizedMessage}")
            }
        }
}

private const val TAG = "TrendingMovieRepositoryImpl"