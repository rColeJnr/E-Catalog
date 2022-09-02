package com.rick.data_movie.imdb.movie_model

data class Images(
    val id: String,
    val items: List<Image>,
    val errorMessage: String
)

data class Image (
    val title: String,
    val image: String
)
