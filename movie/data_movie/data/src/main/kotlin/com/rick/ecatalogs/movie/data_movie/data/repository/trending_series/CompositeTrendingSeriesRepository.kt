package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rick.ecatalogs.movie.data_movie.database.model.asTrendingSeries
import com.rick.ecatalogs.movie.data_movie.model.UserTrendingSeries
import com.rick.ecatalogs.movie.data_movie.model.mapToUserTrendingSeries
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
class CompositeTrendingSeriesRepository @Inject constructor(
    private val trendingSeriesRepository: TrendingSeriesRepository,
    private val userDataRepository: UserTrendingSeriesDataRepository
) : UserTrendingSeriesRepository {
    override fun observeTrendingSeries(
        apiKey: String, viewModelScope: CoroutineScope
    ): Flow<PagingData<UserTrendingSeries>> =
        trendingSeriesRepository.getTrendingSeries(apiKey).cachedIn(viewModelScope)
            .combine(userDataRepository.userData) { trendingSeries, userData ->
                trendingSeries.map { it.asTrendingSeries().mapToUserTrendingSeries(userData) }
            }

    override fun observeTrendingSeriesFavorite(): Flow<List<UserTrendingSeries>> =
        userDataRepository.userData.map { it.trendingSeriesFavoriteIds }.distinctUntilChanged()
            .flatMapLatest { favoriteIds ->
                when {
                    favoriteIds.isEmpty() -> flowOf(emptyList())
                    else -> {
                        trendingSeriesRepository.getTrendingSeriesFavorites(favoriteIds)
                            .combine(userDataRepository.userData) { series, userData ->
                                series.mapToUserTrendingSeries(userData)
                            }
                    }
                }
            }

    override fun observeSearchTrendingSeries(
        query: String,
        apiKey: String
    ): Flow<List<UserTrendingSeries>> =
        trendingSeriesRepository.searchTrendingSeries(apiKey = apiKey, query = query)
            .combine(userDataRepository.userData) { series, userData ->
                series.map { it.mapToUserTrendingSeries(userData) }
            }
}
