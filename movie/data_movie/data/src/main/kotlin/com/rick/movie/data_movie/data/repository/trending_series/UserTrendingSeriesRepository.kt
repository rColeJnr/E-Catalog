package com.rick.movie.data_movie.data.repository.trending_series

import androidx.paging.PagingData
import com.rick.data.model_movie.UserTrendingSeries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserTrendingSeriesRepository {

    /**
     * Returns available animes as a stream.
     */
    fun observeTrendingSeries(
        apiKey: String,
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserTrendingSeries>>

    /**
     * observe the user's favorite series
     * */
    fun observeTrendingSeriesFavorite(): Flow<List<UserTrendingSeries>>

    fun observeSearchTrendingSeries(query: String, apiKey: String): Flow<List<UserTrendingSeries>>

}
