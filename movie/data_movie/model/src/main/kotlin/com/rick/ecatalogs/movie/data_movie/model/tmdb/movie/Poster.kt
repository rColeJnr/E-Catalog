package com.rick.ecatalogs.movie.data_movie.model.tmdb.movie


data class Poster(
    val aspectRatio: Double,
    val height: Int,
    val iso6391: String?,
    val filePath: String,
    val voteAverage: Double,
    val voteCount: Int,
    val width: Int
)