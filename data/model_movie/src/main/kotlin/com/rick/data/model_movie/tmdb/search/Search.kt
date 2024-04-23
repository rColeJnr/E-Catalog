package com.rick.data.model_movie.tmdb.search

data class Search(
    val adult: Boolean,
    val title: String,
    val id: Int,
    val summary: String,
    val image: String,
    val mediaType: String,
)