package com.rick.movie.data_movie.data.repository.trending_movie

import com.rick.data.model_movie.tmdb.movie.MyMovie
import kotlinx.coroutines.flow.Flow

interface TrendingMovieByIdRepository {

    fun getTrendingMovieById(id: Int, apiKey: String): Flow<MyMovie>
}