package com.rick.movie.data_movie.data.repository.trending_movie

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.data.database_movie.model.asTrendingMovie
import com.rick.data.model_movie.UserTrendingMovie
import com.rick.data.model_movie.mapToUserTrendingMovie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * implements all repositories and provides all favorites
 * */
class CompositeTrendingMovieRepository @Inject constructor(
    private val trendingMovieRepository: TrendingMovieRepository,
    private val userDataRepository: UserTrendingMovieDataRepository
) : UserTrendingMovieRepository {

    override fun observeTrendingMovie(
        apiKey: String, viewModelScope: CoroutineScope
    ): Flow<PagingData<UserTrendingMovie>> =
        trendingMovieRepository.getTrendingMovies(apiKey).cachedIn(viewModelScope)
            .combine(userDataRepository.userData) { trendingMovie, userData ->
                trendingMovie.map { it.asTrendingMovie().mapToUserTrendingMovie(userData) }
            }

    override fun observeTrendingMovieFavorite(): Flow<List<UserTrendingMovie>> =
        userDataRepository.userData.map { it.trendingMovieFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { favoriteIds ->
                when {
                    favoriteIds.isEmpty() -> flowOf(emptyList())
                    else -> {
                        trendingMovieRepository.getTrendingMovieFavorites(favoriteIds)
                            .combine(userDataRepository.userData) { movie, userData ->
                                movie.mapToUserTrendingMovie(userData)
                            }
                    }
                }
            }

    override fun observeSearchTrendingMovie(
        query: String, apiKey: String
    ): Flow<List<UserTrendingMovie>> =
        trendingMovieRepository.searchTrendingMovie(apiKey = apiKey, query = query)
            .combine(userDataRepository.userData) { article, userData ->
                article.map { it.mapToUserTrendingMovie(userData) }
            }
}
