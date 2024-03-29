package com.rick.data_movie.imdb_am_not_paying.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TvSeriesInfoDto(
    @SerializedName("yearEnd")
    val yearEnd: String,
    @SerializedName("creators")
    val creators: String,
    @SerializedName("creatorList")
    val creatorList: List<CreatorDto>,
    @SerializedName("seasons")
    val seasons: List<String>
)
@Parcelize
data class TvSeriesInfo(
    @SerializedName("yearEnd")
    val yearEnd: String,
    @SerializedName("creators")
    val creators: String,
    @SerializedName("seasons")
    val seasons: List<String>
) : Parcelable