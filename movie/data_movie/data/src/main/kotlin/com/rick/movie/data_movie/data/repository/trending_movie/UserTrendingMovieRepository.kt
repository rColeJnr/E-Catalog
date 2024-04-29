package com.rick.movie.data_movie.data.repository.trending_movie

import androidx.paging.PagingData
import com.rick.data.model_movie.UserTrendingMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserTrendingMovieRepository {

    fun observeTrendingMovie(
        apiKey: String,
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserTrendingMovie>>

    /**
     * observe the user's favorite [UserTrendingMovie]
     * */
    fun observeTrendingMovieFavorite(): Flow<List<UserTrendingMovie>>

    fun observeSearchTrendingMovie(query: String, apiKey: String): Flow<List<UserTrendingMovie>>

}
