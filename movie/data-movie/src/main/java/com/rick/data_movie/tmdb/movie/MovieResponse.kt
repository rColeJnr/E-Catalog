package com.rick.data_movie.tmdb.movie


import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.rick.data_movie.favorite.Favorite

@Entity(tableName = "tmdb_movie", primaryKeys = ["id"])
data class MovieResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropPath: String,
    @SerializedName("budget")
    val budget: Int,
    @SerializedName("genres")
    val genres: List<Genre>,
    @SerializedName("homepage")
    val homepage: String,
    @SerializedName("overview")
    val overview: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("poster_path")
    val posterPath: String,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("revenue")
    val revenue: Int,
    @SerializedName("runtime")
    val runtime: Int,
    @SerializedName("title")
    val title: String,
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
            title = title ,
            overview = overview,
            image = posterPath,
            type = "tv"
        )
}