package com.rick.data_movie.tmdb

import com.google.gson.annotations.SerializedName

data class Similar(
    @SerializedName("id")
    val id: Int,
    @SerializedName("poster_path")
    val image: String,
    @SerializedName("name")
    val name: String
)