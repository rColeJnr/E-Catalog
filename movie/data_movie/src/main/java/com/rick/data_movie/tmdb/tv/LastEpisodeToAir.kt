package com.rick.data_movie.tmdb.tv

import com.google.gson.annotations.SerializedName


data class LastEpisodeToAir(
    @SerializedName("id")
    val id: Int,
    @SerializedName("air_date")
    val airdate: String,
    @SerializedName("episode_number")
    val episodeNumber: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("season_number")
    val seasonNumber: Int,
    @SerializedName("still_path")
    val stillPath: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
)