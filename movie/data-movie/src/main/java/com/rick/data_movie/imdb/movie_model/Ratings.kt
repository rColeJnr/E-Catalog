package com.rick.data_movie.imdb.movie_model

data class Ratings(
    val errorMessage: String,
    val filmAffinity: String,
    val fullTitle: String,
    val imDb: String,
    val imDbId: String,
    val metacritic: String,
    val rottenTomatoes: String,
    val theMovieDb: String,
    val title: String,
    val type: String,
    val year: String
)