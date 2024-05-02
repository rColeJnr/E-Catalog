package com.rick.movie.data_movie.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrendingSeriesNetwork(
    val adult: Boolean,
    @SerializedName("backdrop_path")
    @SerialName("backdrop_path")
    val image: String,
    val id: Int,
    val name: String,
    val overview: String,
    val popularity: Double,
    @SerializedName("first_air_date")
    @SerialName("first_air_date")
    val firstAirDate: String,
    @SerializedName("vote_average")
    @SerialName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    @SerialName("vote_count")
    val voteCount: Int,
)
