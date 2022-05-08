package com.rick.moviecatalog.data.remote

data class MovieCatalogDto(
    val copyright: String,
    val has_more: Boolean,
    val num_results: Int,
    val results: List<ResultDto>,
    val status: String
)