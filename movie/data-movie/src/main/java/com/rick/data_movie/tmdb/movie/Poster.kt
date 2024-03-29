package com.rick.data_movie.tmdb.movie


import com.google.gson.annotations.SerializedName

data class Poster(
    @SerializedName("aspect_ratio")
    val aspectRatio: Double,
    @SerializedName("height")
    val height: Int,
    @SerializedName("iso_639_1")
    val iso6391: String?,
    @SerializedName("file_path")
    val filePath: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("width")
    val width: Int
)