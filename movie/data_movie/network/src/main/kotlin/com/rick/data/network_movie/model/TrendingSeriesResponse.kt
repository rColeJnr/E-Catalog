package com.rick.data.network_movie.model

import kotlinx.serialization.Serializable

@Serializable
data class TrendingSeriesResponse(
    val page: Int,
    val results: List<TrendingSeriesNetwork>
)