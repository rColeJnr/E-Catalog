package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.database.MovieTransactionRunner
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesDao
import com.rick.ecatalogs.movie.data_movie.database.dao.TrendingSeriesRemoteKeysDao
import com.rick.ecatalogs.movie.data_movie.database.model.TrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.database.model.asTrendingSeries
import com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_series.TrendingSeries
import com.rick.ecatalogs.movie.data_movie.data.model.asTrendingSeriesEntity
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.ecatalogs.movie.data_movie.network.model.TrendingSeriesNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

internal class TrendingSeriesRepositoryImpl @Inject constructor(
    private val trendingSeriesDao: TrendingSeriesDao,
    private val keysDao: TrendingSeriesRemoteKeysDao,
    private val network: TmdbNetworkDataSource,
    private val transactionRunner: MovieTransactionRunner,
) : TrendingSeriesRepository {
    override fun getTrendingSeries(): Flow<PagingData<TrendingSeriesEntity>> {
        val pagingSourceFactory = { trendingSeriesDao.getTrendingSeries() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 16,
                enablePlaceholders = true,
                prefetchDistance = 1,
                initialLoadSize = 1
            ),
            remoteMediator = TrendingSeriesRemoteMediator(
                network = network,
                trendingSeriesDao = trendingSeriesDao,
                keysDao = keysDao,
                transactionRunner = transactionRunner,
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override fun getTrendingSeriesFavorites(favorites: Set<Int>): Flow<List<TrendingSeries>> =
        trendingSeriesDao.getTrendingSeriesWithFilters(
            filterById = true,
            filterIds = favorites
        )
            .map { trendingSeriesEntities -> trendingSeriesEntities.map { it.asTrendingSeries() } }

    override suspend fun syncFirstPage(): Boolean {
        val response = network.fetchTrendingSeries(page = TRENDING_SERIES_FIRST_PAGE)
        val fetchedIds = response.results.map(TrendingSeriesNetwork::id).toSet()
        if (fetchedIds.isEmpty() || fetchedIds == keysDao.firstPageIds().toSet()) return false

        writeTrendingSeriesPage(
            page = TRENDING_SERIES_FIRST_PAGE,
            response = response,
            isRefresh = true,
            trendingSeriesDao = trendingSeriesDao,
            keysDao = keysDao,
            transactionRunner = transactionRunner,
        )
        return true
    }

    override fun searchTrendingSeries(query: String): Flow<List<TrendingSeries>> =
        channelFlow {
            try {
                val response = network.searchSeries(
                    query = query,
                ).results

                trendingSeriesDao.upsertTrendingSeries(response.map(TrendingSeriesNetwork::asTrendingSeriesEntity))

                // appending '%' so we can allow other characters to be before and after the query string
                val dbQuery = "%${query.replace(' ', '%')}%"
                trendingSeriesDao.getTrendingSeriesWithFilters(
                    filterByQuery = true,
                    query = dbQuery
                )
                    .collectLatest { articleEntities ->
                        send(
                            articleEntities.map(
                                TrendingSeriesEntity::asTrendingSeries
                            )
                        )
                    }

            } catch (e: IOException) {
                Log.e(TAG, e.localizedMessage ?: "IOException")
            } catch (e: HttpException) {
                Log.e(TAG, e.localizedMessage ?: "HTTPException")
            }
        }
}

private const val TAG = "TrendingSeriesRepositoryImpl"