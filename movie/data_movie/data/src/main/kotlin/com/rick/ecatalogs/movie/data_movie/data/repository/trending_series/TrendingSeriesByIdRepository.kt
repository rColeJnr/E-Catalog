package com.rick.ecatalogs.movie.data_movie.data.repository.trending_series

import com.rick.ecatalogs.movie.data_movie.model.tmdb.series.MySeries
import kotlinx.coroutines.flow.Flow

interface TrendingSeriesByIdRepository {

    fun getTrendingSeriesById(id: Int): Flow<MySeries>
}