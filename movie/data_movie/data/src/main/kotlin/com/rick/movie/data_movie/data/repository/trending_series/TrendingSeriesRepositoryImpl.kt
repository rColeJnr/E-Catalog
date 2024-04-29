package com.rick.movie.data_movie.data.repository.trending_series

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rick.data.database_movie.dao.TrendingSeriesDao
import com.rick.data.database_movie.dao.TrendingSeriesRemoteKeysDao
import com.rick.data.database_movie.model.TrendingSeriesEntity
import com.rick.data.database_movie.model.asTrendingSeries
import com.rick.data.model_movie.tmdb.trending_series.TrendingSeries
import com.rick.data.network_movie.TmdbNetworkDataSource
import com.rick.data.network_movie.model.TrendingSeriesNetwork
import com.rick.movie.data_movie.data.model.asTrendingSeriesEntity
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
) : TrendingSeriesRepository {
    override fun getTrendingSeries(apiKey: String): Flow<PagingData<TrendingSeriesEntity>> {
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
                key = apiKey
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

    override fun searchTrendingSeries(apiKey: String, query: String): Flow<List<TrendingSeries>> =
        channelFlow {
            try {
                val response = network.searchSeries(
                    query = query,
                    apikey = apiKey
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