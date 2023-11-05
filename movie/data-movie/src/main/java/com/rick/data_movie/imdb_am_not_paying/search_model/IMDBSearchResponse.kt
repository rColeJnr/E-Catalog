package com.rick.data_movie.imdb_am_not_paying.search_model

import com.google.gson.annotations.SerializedName

data class IMDBSearchResponse(
    @field:SerializedName("searchType")
    val searchType: String,
    @field:SerializedName("expression")
    val expression: String,
    @field:SerializedName("results")
    val results: List<IMDBSearchResult>,
    @field:SerializedName("errorMessage")
    val errorMessage: String
)