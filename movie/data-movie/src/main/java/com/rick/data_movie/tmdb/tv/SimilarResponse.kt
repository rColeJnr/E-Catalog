package com.rick.data_movie.tmdb.tv

import com.google.gson.annotations.SerializedName


data class SimilarResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Similar>,
    @SerializedName("total_pages")
    val total_pages: Int,
    @SerializedName("total_results")
    val total_results: Int
)