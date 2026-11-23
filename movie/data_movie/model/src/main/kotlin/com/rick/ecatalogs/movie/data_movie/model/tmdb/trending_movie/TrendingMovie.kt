package com.rick.ecatalogs.movie.data_movie.model.tmdb.trending_movie


data class TrendingMovie(
    val id: Int,
    val adult: Boolean,
    val image: String,
    val title: String,
    val overview: String,
    val popularity: Double,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
)