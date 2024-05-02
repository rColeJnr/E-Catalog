package com.rick.movie.data_movie.data.repository.trending_movie

import android.util.Log
import com.rick.data.model_movie.tmdb.movie.MyMovie
import com.rick.movie.data_movie.network.TmdbNetworkDataSource
import com.rick.movie.data_movie.data.model.asMyMovie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import retrofit2.HttpException
import javax.inject.Inject

class TrendingMovieByIdRepositoryImpl @Inject constructor(
    private val network: TmdbNetworkDataSource
) : TrendingMovieByIdRepository {

    override fun getTrendingMovieById(id: Int, apiKey: String): Flow<MyMovie> =
        channelFlow {
            try {
                val response = network.fetchMovie(
                    id = id,
                    apikey = apiKey
                )

                send(response.asMyMovie())
            } catch (e: HttpException) {
                Log.e(TAG, e.localizedMessage ?: "HTTPException")
            }
        }
}

private const val TAG = "TrendingMovieByIdRepository"