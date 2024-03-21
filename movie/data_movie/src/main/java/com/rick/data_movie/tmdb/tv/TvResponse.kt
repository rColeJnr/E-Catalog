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
    val backdropImage: String,
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
    @SerializedName("name")
    val name: String,
    @SerializedName("number_of_seasons")
    val numberOfSeasons: Int,
    @SerializedName("overview")
    val summary: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val image: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
    @SerializedName("similar")
    val similar: SimilarResponse
) {
    fun toFavorite(): Favorite =
        Favorite(
            id = id,
            title = name,
            summary = summary,
            image = image,
            type = "tv",
            isFavorite = false
        )
}