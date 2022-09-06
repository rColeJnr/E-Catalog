package com.rick.data_movie.imdb.movie_model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Writer(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
) : Parcelable