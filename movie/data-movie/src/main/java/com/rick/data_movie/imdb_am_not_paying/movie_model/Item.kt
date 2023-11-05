package com.rick.data_movie.imdb_am_not_paying.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ItemDto(
    @SerializedName("title")
    val title: String,
    @SerializedName("image")
    val image: String
)

@Parcelize
data class Item(
    @SerializedName("image")
    val image: String
) : Parcelable