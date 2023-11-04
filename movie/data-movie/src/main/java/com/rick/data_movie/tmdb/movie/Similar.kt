package com.rick.data_movie.tmdb.movie

data class Similar (
    val adult: Boolean,
    val backdropPath: String,
    val id: Int,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
)
