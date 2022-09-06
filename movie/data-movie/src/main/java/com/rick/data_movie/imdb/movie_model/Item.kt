package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String
) : Parcelable