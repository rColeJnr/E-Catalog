package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Posters(
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
    @SerializedName("posters")
    val posters: List<Poster>,
    @SerializedName("backdrops")
    val backdrops: List<Backdrop>,
    @SerializedName("errorMessage")
    val errorMessage: String
) : Parcelable