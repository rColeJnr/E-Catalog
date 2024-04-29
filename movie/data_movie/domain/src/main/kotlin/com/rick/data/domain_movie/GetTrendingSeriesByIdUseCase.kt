package com.rick.data.domain_movie

import com.rick.data.model_movie.TrendingSeriesUserData
import com.rick.data.model_movie.UserSeries
import com.rick.data.model_movie.tmdb.series.MySeries
import com.rick.movie.data_movie.data.repository.trending_series.TrendingSeriesByIdRepository
import com.rick.movie.data_movie.data.repository.trending_series.UserTrendingSeriesDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTrendingSeriesByIdUseCase @Inject constructor(
    private val trendingSeriesByIdRepository: TrendingSeriesByIdRepository,
    private val userDataRepository: UserTrendingSeriesDataRepository
) {
    operator fun invoke(id: Int, apiKey: String): Flow<UserSeries> =
        trendingSeriesByIdRepository.getTrendingSeriesById(id, apiKey)
            .mapToUserSeries(userDataRepository.userData)
}

private fun Flow<MySeries>.mapToUserSeries(userDataStream: Flow<TrendingSeriesUserData>): Flow<UserSeries> =
    combine(userDataStream) { series, userData ->
        UserSeries(series = series, userData = userData)
    }