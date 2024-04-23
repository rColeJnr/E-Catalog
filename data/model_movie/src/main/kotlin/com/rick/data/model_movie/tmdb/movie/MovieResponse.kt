package com.rick.data.model_movie.tmdb.movie


data class MovieResponse(
    val id: Int,
    val adult: Boolean,
    val backdropImage: String,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val summary: String,
    val popularity: Double,
    val image: String,
    val releaseDate: String,
    val revenue: Int,
    val runtime: Int,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,
    val similar: MovieSimilarResponse
)