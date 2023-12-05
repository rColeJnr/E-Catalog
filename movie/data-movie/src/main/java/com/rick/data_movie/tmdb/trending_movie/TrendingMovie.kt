package com.rick.data_movie.tmdb.trending_movie


import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "trending_movie", primaryKeys = ["id"])
data class TrendingMovie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropImage: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("overview")
    val summary: String,
    @SerializedName("poster_path")
    val image: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int
): Parcelable