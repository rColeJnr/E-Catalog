package com.rick.data_movie.imdb

import com.rick.data_movie.imdb.search_model.IMDBSearchResult

data class IMDBSearchResponse(
    val searchType: String,
    val expression: String,
    val results: List<IMDBSearchResult>,
    val errorMessage: String,
)