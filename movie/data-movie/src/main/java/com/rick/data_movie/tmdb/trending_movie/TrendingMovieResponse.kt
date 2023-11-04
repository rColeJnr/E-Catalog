package com.rick.data_movie.tmdb.trending_movie


import com.google.gson.annotations.SerializedName

data class TrendingMovieResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)