package com.rick.movie.data_movie.network.model


import com.google.gson.annotations.SerializedName
import com.rick.data.model_movie.tmdb.movie.Genre
import com.rick.data.model_movie.tmdb.series.Creator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SeriesResponse(
    val id: Int,
    val adult: Boolean,
    @SerialName("created_by")
    @SerializedName("created_by")
    val createdBy: List<Creator>,
    @SerialName("episode_run_time")
    @SerializedName("episode_run_time")
    val episodeRuntime: List<Int>,
    @SerializedName("first_air_date")
    @SerialName("first_air_date")
    val firstAirDate: String,
    val genres: List<Genre>,
    val homepage: String,
    @SerialName("in_production")
    @SerializedName("in_production")
    val inProduction: Boolean,
    @SerialName("last_air_date")
    @SerializedName("last_air_date")
    val lastAirDate: String?,
    val name: String,
    @SerializedName("number_of_seasons")
    @SerialName("number_of_seasons")
    val numberOfSeasons: Int,
    @SerializedName("number_of_episodes")
    @SerialName("number_of_episodes")
    val numberOfEpisodes: Int,
    val overview: String,
    val popularity: Double,
    @SerializedName("poster_path")
    @SerialName("poster_path")
    val image: String,
    @SerializedName("vote_average")
    @SerialName("vote_average")
    val voteAverage: Double,
    val similar: TrendingSeriesResponse
)