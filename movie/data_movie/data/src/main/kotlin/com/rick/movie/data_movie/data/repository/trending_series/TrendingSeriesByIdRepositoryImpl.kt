package com.rick.movie.data_movie.data.repository.trending_series

import android.util.Log
import com.rick.data.model_movie.tmdb.series.MySeries
import com.rick.data.network_movie.TmdbNetworkDataSource
import com.rick.movie.data_movie.data.model.asMySeries
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import javax.inject.Inject

class TrendingSeriesByIdRepositoryImpl @Inject constructor(
    private val network: TmdbNetworkDataSource
) : TrendingSeriesByIdRepository {

    override fun getTrendingSeriesById(id: Int, apiKey: String): Flow<MySeries> =
        channelFlow {
            try {
                val response = network.fetchSeries(
                    id = id,
                    apikey = apiKey
                )

                send(response.asMySeries())
            } catch (e: HttpException) {
                Log.e(TAG, e.localizedMessage ?: "HTTPException")
            }
        }
}

private const val TAG = "TrendingSeriesByIdRepository"