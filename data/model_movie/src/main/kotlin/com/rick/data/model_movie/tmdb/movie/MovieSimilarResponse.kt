package com.rick.data.model_movie.tmdb.movie

data class MovieSimilarResponse(
    val page: Int,
    val results: List<MovieSimilar>,
    val totalPages: Int,
    val totalResults: Int
)