package com.rick.data_movie.imdb.movie_model


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TvSeriesInfo(
    @SerializedName("yearEnd")
    val yearEnd: String,
    @SerializedName("creators")
    val creators: String,
    @SerializedName("creatorList")
    val creatorList: List<Creator>,
    @SerializedName("seasons")
    val seasons: List<String>
) : Parcelable