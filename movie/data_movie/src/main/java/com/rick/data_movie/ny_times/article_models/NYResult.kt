package com.rick.data_movie.ny_times.article_models


import com.google.gson.annotations.SerializedName

data class NYResult(
    @SerializedName("status")
    val status: String,
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("response")
    val response: Response
)