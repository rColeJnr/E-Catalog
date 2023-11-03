package com.rick.data_movie.ny_times.article_models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Keyword(
    @SerializedName("value")
    val value: String,
    @SerializedName("rank")
    val rank: Int,
): Parcelable