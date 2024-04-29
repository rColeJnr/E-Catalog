package com.rick.data.model_movie.tmdb.trending_series

data class TrendingSeries(
    val adult: Boolean,
    val image: String,
    val id: Int,
    val name: String,
    val overview: String,
    val popularity: Double,
    val firstAirDate: String,
    val voteAverage: Double,
    val voteCount: Int,
)