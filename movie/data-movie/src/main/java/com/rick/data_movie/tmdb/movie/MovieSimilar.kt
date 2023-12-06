package com.rick.data_movie.tmdb.movie

import com.google.gson.annotations.SerializedName
import com.rick.data_movie.tmdb.Similar

data class MovieSimilar (
    @SerializedName("id")
    val id: Int,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("title")
    val title: String,
){
    fun toSimilar(): Similar = Similar(
        id, posterPath, title
    )
}

