package com.rick.ecatalogs.movie.data_movie.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `genre/movie/list` and `genre/tv/list`. */
@Serializable
data class GenresResponse(
    val genres: List<GenreNetwork>? = null,
)

@Serializable
data class GenreNetwork(
    val id: Int,
    val name: String? = null,
)

/** One entry of a series' `seasons` (from the series details response). */
@Serializable
data class SeasonSummaryNetwork(
    @SerialName("season_number")
    @SerializedName("season_number")
    val seasonNumber: Int,
    val name: String? = null,
    @SerialName("episode_count")
    @SerializedName("episode_count")
    val episodeCount: Int? = null,
    @SerialName("air_date")
    @SerializedName("air_date")
    val airDate: String? = null,
    @SerialName("poster_path")
    @SerializedName("poster_path")
    val posterPath: String? = null,
)

/** `tv/{id}/season/{season_number}`: a season with its episodes. */
@Serializable
data class SeasonResponse(
    @SerialName("season_number")
    @SerializedName("season_number")
    val seasonNumber: Int,
    val name: String? = null,
    val overview: String? = null,
    val episodes: List<EpisodeNetwork>? = null,
)

@Serializable
data class EpisodeNetwork(
    val id: Int,
    @SerialName("episode_number")
    @SerializedName("episode_number")
    val episodeNumber: Int,
    val name: String? = null,
    val overview: String? = null,
    @SerialName("air_date")
    @SerializedName("air_date")
    val airDate: String? = null,
    @SerialName("still_path")
    @SerializedName("still_path")
    val stillPath: String? = null,
    val runtime: Int? = null,
    @SerialName("vote_average")
    @SerializedName("vote_average")
    val voteAverage: Double? = null,
)
