package com.rick.data_movie.imdb_am_not_paying.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class SimilarDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("imDbRating")
    val imDbRating: String
)
@Parcelize
data class Similar(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("imDbRating")
    val imDbRating: String
) : Parcelable