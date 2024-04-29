package com.rick.data.domain_movie

import com.rick.data.model_movie.TrendingMovieUserData
import com.rick.data.model_movie.UserMovie
import com.rick.data.model_movie.tmdb.movie.MyMovie
import com.rick.movie.data_movie.data.repository.trending_movie.TrendingMovieByIdRepository
import com.rick.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTrendingMovieByIdUseCase @Inject constructor(
    private val trendingMovieByIdRepository: TrendingMovieByIdRepository,
    private val userTrendingMovieDataRepository: UserTrendingMovieDataRepository
) {
    operator fun invoke(id: Int, apiKey: String): Flow<UserMovie> =
        trendingMovieByIdRepository.getTrendingMovieById(id, apiKey)
            .mapToUserMovie(userTrendingMovieDataRepository.userData)
}

private fun Flow<MyMovie>.mapToUserMovie(userDataStream: Flow<TrendingMovieUserData>): Flow<UserMovie> =
    combine(userDataStream) { movie, userData ->
        UserMovie(movie = movie, userData = userData)
    }