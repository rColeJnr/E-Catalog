package com.rick.data_movie.imdb.movie_model

data class Posters(
    val backdrops: List<Any>,
    val errorMessage: String,
    val fullTitle: String,
    val imDbId: String,
    val posters: List<Any>,
    val title: String,
    val type: String,
    val year: String
)