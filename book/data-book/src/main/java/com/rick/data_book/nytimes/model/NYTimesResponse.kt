package com.rick.data_book.nytimes.model


import com.google.gson.annotations.SerializedName

data class NYTimesResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("num_results")
    val numResults: Int,
    @SerializedName("last_modified")
    val lastModified: String,
    @SerializedName("results")
    val results: Results
)