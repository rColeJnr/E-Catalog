package com.rick.data_movie.tmdb.search

import com.google.gson.annotations.SerializedName

data class Search(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("title")
    val title: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("overview")
    val summary: String,
    @SerializedName("poster_path")
    val posterImage: String?,
    @SerializedName("media_type")
    val mediaType: String,
)