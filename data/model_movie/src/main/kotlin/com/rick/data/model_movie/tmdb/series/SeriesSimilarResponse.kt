package com.rick.data.model_movie.tmdb.series


data class SeriesSimilarResponse(
    val page: Int,
    val results: List<SeriesSimilar>,
    val total_pages: Int,
    val total_results: Int
)