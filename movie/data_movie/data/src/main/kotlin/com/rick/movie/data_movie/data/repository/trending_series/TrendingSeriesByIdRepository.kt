package com.rick.movie.data_movie.data.repository.trending_series

import com.rick.data.model_movie.tmdb.series.MySeries
import kotlinx.coroutines.flow.Flow

interface TrendingSeriesByIdRepository {

    fun getTrendingSeriesById(id: Int, apiKey: String): Flow<MySeries>
}