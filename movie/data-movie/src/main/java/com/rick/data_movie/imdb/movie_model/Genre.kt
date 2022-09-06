package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenreDto(
    @SerializedName("key")
    val key: String,
    @SerializedName("value")
    val value: String
) : Parcelable