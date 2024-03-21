package com.rick.data_book.nytimes.model


import com.google.gson.annotations.SerializedName

data class NYTimesResponse(
    @SerializedName("num_results")
    val numResults: Int,
    @SerializedName("results")
    val results: Results
)