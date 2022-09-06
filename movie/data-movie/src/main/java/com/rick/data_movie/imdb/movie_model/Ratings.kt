package com.rick.data_movie.imdb.movie_model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Ratings(
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
    @SerializedName("imDb")
    val imDb: String,
    @SerializedName("metacritic")
    val metacritic: String,
    @SerializedName("theMovieDb")
    val theMovieDb: String,
    @SerializedName("rottenTomatoes")
    val rottenTomatoes: String,
    @SerializedName("filmAffinity")
    val filmAffinity: String,
    @SerializedName("errorMessage")
    val errorMessage: String
) : Parcelable