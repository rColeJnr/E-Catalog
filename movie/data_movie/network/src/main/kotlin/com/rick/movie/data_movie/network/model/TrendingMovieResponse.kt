package com.rick.movie.data_movie.network.model

import kotlinx.serialization.Serializable

@Serializable
data class TrendingMovieResponse(
    val page: Int,
    val results: List<TrendingMovieNetwork>,
)