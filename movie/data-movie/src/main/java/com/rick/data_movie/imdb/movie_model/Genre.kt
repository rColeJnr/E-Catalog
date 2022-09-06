package com.rick.data_movie.imdb.movie_model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Genre(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String
) : Parcelable