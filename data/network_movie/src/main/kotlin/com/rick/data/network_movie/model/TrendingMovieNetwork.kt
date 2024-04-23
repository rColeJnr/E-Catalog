package com.rick.data.network_movie.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingMovieNetwork(
    val id: Int,
    val adult: Boolean,
    @SerializedName("backdrop_path")
    @SerialName("backdrop_path")
    val image: String,
    val title: String,
    val overview: String,
    @SerializedName("media_type")
    @SerialName("media_type")
    val mediaType: String,
    val popularity: Double,
    @SerializedName("release_date")
    @SerialName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    @SerialName("vote_count")
    val voteCount: Int,
)
