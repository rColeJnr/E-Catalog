package com.rick.ecatalogs.movie.data_movie.domain

import com.rick.ecatalogs.movie.data_movie.model.TrendingMovieUserData
import com.rick.ecatalogs.movie.data_movie.model.UserMovie
import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.MyMovie
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.TrendingMovieByIdRepository
import com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie.UserTrendingMovieDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTrendingMovieByIdUseCase @Inject constructor(
    private val trendingMovieByIdRepository: TrendingMovieByIdRepository,
    private val userTrendingMovieDataRepository: UserTrendingMovieDataRepository
) {
    operator fun invoke(id: Int): Flow<UserMovie> =
        trendingMovieByIdRepository.getTrendingMovieById(id)
            .mapToUserMovie(userTrendingMovieDataRepository.userData)
}

private fun Flow<MyMovie>.mapToUserMovie(userDataStream: Flow<TrendingMovieUserData>): Flow<UserMovie> =
    combine(userDataStream) { movie, userData ->
        UserMovie(movie = movie, userData = userData)
    }