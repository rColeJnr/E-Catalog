package com.rick.data.model_movie.tmdb.search


data class SearchResponse(
    val page: Int,
    val results: List<Search>,
    val totalPages: Int,
    val totalResults: Int
)