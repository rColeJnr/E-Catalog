package com.rick.data_movie.imdb.search_model

data class IMDBResponse(
    val searchType: String,
    val expression: String,
    val results: List<SearchedMovie>,
    val errorMessage: String,
)