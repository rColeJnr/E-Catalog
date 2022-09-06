package com.rick.data_movie.imdb.movie_model


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


data class BoxOfficeDto(
    @SerializedName("budget")
    val budget: String,
    @SerializedName("openingWeekendUSA")
    val openingWeekendUSA: String,
    @SerializedName("grossUSA")
    val grossUSA: String,
    @SerializedName("cumulativeWorldwideGross")
    val cumulativeWorldwideGross: String
)
@Parcelize
data class BoxOffice(
    @SerializedName("budget")
    val budget: String,
    @SerializedName("openingWeekendUSA")
    val openingWeekendUSA: String,
    @SerializedName("cumulativeWorldwideGross")
    val cumulativeWorldwideGross: String
) : Parcelable