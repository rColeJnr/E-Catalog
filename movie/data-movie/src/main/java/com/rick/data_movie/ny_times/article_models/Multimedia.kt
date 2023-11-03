package com.rick.data_movie.ny_times.article_models


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Multimedia(
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String,
): Parcelable