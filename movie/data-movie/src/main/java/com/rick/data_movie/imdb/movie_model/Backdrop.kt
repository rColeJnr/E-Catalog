package com.rick.data_movie.imdb.movie_model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Backdrop(
    @SerializedName("id")
    val id: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("aspectRatio")
    val aspectRatio: Double,
    @SerializedName("language")
    val language: String,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int
) : Parcelable