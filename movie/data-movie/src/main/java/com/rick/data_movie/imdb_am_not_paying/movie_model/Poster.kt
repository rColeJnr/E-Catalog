package com.rick.data_movie.imdb_am_not_paying.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PosterDto(
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
): Parcelable
//
//@Parcelize
//data class Poster(
//    @SerializedName("id")
//    val id: String,
//    @SerializedName("link")
//    val link: String,
//    @SerializedName("aspectRatio")
//    val aspectRatio: Double,
//    @SerializedName("language")
//    val language: String,
//    @SerializedName("width")
//    val width: Int,
//    @SerializedName("height")
//    val height: Int
//) : Parcelable