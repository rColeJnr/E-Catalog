package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class StarDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)

@Parcelize
data class Star(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
) : Parcelable