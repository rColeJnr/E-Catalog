package com.rick.data_movie.imdb

import com.rick.data_movie.imdb.search_model.SearchResult

data class IMDBResponse(
    val searchType: String,
    val expression: String,
    val results: List<SearchResult>,
    val errorMessage: String,
)