package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ImageDto(
    @SerializedName("imDbId")
    val imDbId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("fullTitle")
    val fullTitle: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("year")
    val year: String,
    @SerializedName("items")
    val items: List<ItemDto>,
    @SerializedName("errorMessage")
    val errorMessage: String
)

@Parcelize
data class Image(
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("errorMessage")
    val errorMessage: String
) : Parcelable