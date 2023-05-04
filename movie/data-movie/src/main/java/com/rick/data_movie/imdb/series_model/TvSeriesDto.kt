package com.rick.data_movie.imdb.series_model


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.rick.data_movie.favorite.Favorite
import kotlinx.parcelize.Parcelize

data class TvSeriesDto(
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("rank")
    val rank: String,
    @field:SerializedName("rankUpDown")
    val rankUpDown: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("fullTitle")
    val fullTitle: String,
    @field:SerializedName("year")
    val year: String,
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("crew")
    val crew: String,
    @field:SerializedName("imDbRating")
    val imDbRating: String,
    @field:SerializedName("imDbRatingCount")
    val imDbRatingCount: String,
) {
    fun toTvSeries() : TvSeries =
        TvSeries(
            id,
            title,
            image,
            imDbRating,
            crew,
            favorite = false
        )
}

@Parcelize
@Entity(tableName = "tv_series")
data class TvSeries(
    @PrimaryKey(autoGenerate = false)
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("imDbRating")
    val imDbRating: String,
    @field:SerializedName("crew")
    val cast: String,
    @SerializedName("favorite")
    val favorite: Boolean,
) : Parcelable {
    fun toFavorite(): Favorite =
        Favorite(
            null,
            title,
            imDbRating,
            summary = "",
            image = image,
            authors = cast,
            type = "Tv"
        )
}