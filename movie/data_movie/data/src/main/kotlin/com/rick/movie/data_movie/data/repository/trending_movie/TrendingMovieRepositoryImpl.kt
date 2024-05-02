package com.rick.movie.data_movie.data.repository.trending_movie

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_movie.dao.TrendingMovieDao
import com.rick.data.database_movie.dao.TrendingMovieRemoteKeysDao
import com.rick.data.database_movie.model.TrendingMovieEntity
import com.rick.data.database_movie.model.asTrendingMovie
import com.rick.data.model_movie.tmdb.trending_movie.TrendingMovie
import com.rick.movie.data_movie.data.model.asTrendingMovieEntity
import com.rick.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.movie.data_movie.network.model.TrendingMovieNetwork
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
) : TrendingMovieRepository {
    override fun getTrendingMovies(apiKey: String): Flow<PagingData<TrendingMovieEntity>> {
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
                key = apiKey
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

    override fun searchTrendingMovie(apiKey: String, query: String): Flow<List<TrendingMovie>> =
        channelFlow {
            try {
                val response = network.searchMovie(
                    query = query,
                    apikey = apiKey
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