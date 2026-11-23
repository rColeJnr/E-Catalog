package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import androidx.paging.PagingData
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserTrendingMovieRepository {

    fun observeTrendingMovie(
        viewModelScope: CoroutineScope
    ): Flow<PagingData<UserTrendingMovie>>

    /**
     * observe the user's favorite [UserTrendingMovie]
     * */
    fun observeTrendingMovieFavorite(): Flow<List<UserTrendingMovie>>

    fun observeSearchTrendingMovie(query: String): Flow<List<UserTrendingMovie>>

}
