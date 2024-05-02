package com.rick.movie.data_movie.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingMovieNetwork(
    val id: Int,
    val adult: Boolean,
    @SerializedName("poster_path")
    @SerialName("poster_path")
    val image: String?,
    val title: String?,
    val overview: String?,
    val popularity: Double?,
    @SerializedName("release_date")
    @SerialName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    @SerialName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    @SerialName("vote_count")
    val voteCount: Int?,
)
