package com.rick.data_movie.tmdb.tv


import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.rick.data_movie.favorite.Favorite
import com.rick.data_movie.tmdb.movie.Genre

@Entity(tableName = "tmdb_tv", primaryKeys = ["id"])
data class TvResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("created_by")
    val createdBy: List<Creator>,
    @SerializedName("first_air_date")
    val firstAirDate: String,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("homepage")
    val homepage: String,
    @SerializedName("in_production")
    val inProduction: Boolean,
    @SerializedName("last_air_date")
    val lastAirDate: String,
    @SerializedName("last_episode_to_air")
    val lastEpisodeToAir: LastEpisodeToAir,
    @SerializedName("name")
    val name: String,
    @SerializedName("number_of_episodes")
    val numberOfEpisodes: Int,
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int,
    @SerializedName("original_name")
    val originalName: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("similar")
    val similar: SimilarResponse
) {
    fun toFavorite(): Favorite =
        Favorite (
            id = id,
            title = name,
            overview = overview,
            image = posterPath,
            type = "tv"
        )
}