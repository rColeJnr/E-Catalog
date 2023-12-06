package com.rick.data_movie.tmdb.tv

import com.google.gson.annotations.SerializedName
import com.rick.data_movie.tmdb.Similar

data class TvSimilar (
    @SerializedName("id")
    val id: Int,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("name")
    val name: String,
) {
    fun toSimilar(): Similar = Similar(
        id, posterPath, name
    )
}
