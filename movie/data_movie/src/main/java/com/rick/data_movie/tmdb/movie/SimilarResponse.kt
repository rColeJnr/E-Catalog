package com.rick.data_movie.tmdb.movie


import com.google.gson.annotations.SerializedName

data class SimilarResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<MovieSimilar>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)