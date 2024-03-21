package com.rick.data_movie.tmdb.trending_tv


import com.google.gson.annotations.SerializedName

data class TrendingTvResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<TrendingTv>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)