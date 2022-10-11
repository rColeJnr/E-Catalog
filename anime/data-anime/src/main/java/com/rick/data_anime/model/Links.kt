package com.rick.data_anime.model


import com.google.gson.annotations.SerializedName

data class Links(
    @SerializedName("first")
    val first: String,
    @SerializedName("last")
    val last: String,
    @SerializedName("next")
    val next: String,
    @SerializedName("prev")
    val prev: Any?
)