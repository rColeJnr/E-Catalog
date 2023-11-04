package com.rick.data_movie.tmdb.tv


data class SimilarResponse(
    val page: Int,
    val results: List<Similar>,
    val total_pages: Int,
    val total_results: Int
)