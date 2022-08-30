package com.rick.data_movie.imdb

data class IMDBResponse(
    val searchType: String,
    val expression: String,
    val results: List<IMDBMovie>,
    val errorMessage: String,
)