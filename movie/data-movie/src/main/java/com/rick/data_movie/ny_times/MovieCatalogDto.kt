package com.rick.data_movie.ny_times

data class MovieCatalogDto(
    val copyright: String,
    val has_more: Boolean,
    val num_results: Int,
    val results: List<ResultDto>,
    val status: String
)