package com.rick.ecatalogs.movie.data_movie.data.repository.trending_movie

import com.rick.ecatalogs.movie.data_movie.model.tmdb.movie.MyMovie
import kotlinx.coroutines.flow.Flow

interface TrendingMovieByIdRepository {

    fun getTrendingMovieById(id: Int, apiKey: String): Flow<MyMovie>
}