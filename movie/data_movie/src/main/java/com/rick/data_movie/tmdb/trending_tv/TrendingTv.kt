package com.rick.data_movie.tmdb.trending_tv


import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.rick.data_movie.favorite.Favorite
import kotlinx.parcelize.Parcelize

@Entity(tableName = "trending_tv", primaryKeys = ["id"])
@Parcelize
data class TrendingTv(
    @SerializedName("adult")
    val adult: Boolean,
    @SerializedName("backdrop_path")
    val backdropImage: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("overview")
    val summary: String,
    @SerializedName("poster_path")
    val image: String,
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("popularity")
    val popularity: Double,
    @SerializedName("first_air_date")
    val firstAirDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int,
) : Parcelable {
    fun toFavorite(): Favorite = Favorite(
        id = id,
        title = name,
        summary = summary,
        image = image,
        type = mediaType,
        isFavorite = false
    )
}