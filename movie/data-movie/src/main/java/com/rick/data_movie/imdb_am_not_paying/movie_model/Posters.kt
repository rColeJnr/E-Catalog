package com.rick.data_movie.imdb_am_not_paying.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostersDto(
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
    val posters: List<PosterDto>,
    @SerializedName("backdrops")
    val backdrops: List<Backdrop>,
    @SerializedName("errorMessage")
    val errorMessage: String
) : Parcelable