package com.rick.data_movie.tmdb.tv

import com.google.gson.annotations.SerializedName

data class Creator (
    @SerializedName("name")
    val name: String,
    @SerializedName("profile_path")
    val profileImage: String
)
