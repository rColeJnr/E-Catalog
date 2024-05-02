package com.rick.movie.data_movie.network.model

import com.google.gson.annotations.SerializedName
import com.rick.data.model_movie.tmdb.movie.Genre
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val id: Int,
    val adult: Boolean,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val overview: String,
    val popularity: Double,
    @SerialName("poster_path")
    @SerializedName("poster_path")
    val image: String,
    @SerialName("release_date")
    @SerializedName("release_date")
    val releaseDate: String,
    val revenue: Long,
    val runtime: Int,
    val title: String,
    @SerialName("vote_average")
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerialName("vote_count")
    @SerializedName("vote_count")
    val voteCount: Int,
    val similar: TrendingMovieResponse
)